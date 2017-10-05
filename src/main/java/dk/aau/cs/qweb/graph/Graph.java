package dk.aau.cs.qweb.graph;

import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.jena.graph.GraphEventManager;
import org.apache.jena.graph.GraphEvents;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.impl.GraphBase;
import org.apache.jena.graph.impl.SimpleEventManager;
import org.apache.jena.util.iterator.ExtendedIterator;

import dk.aau.cs.qweb.triple.TripleStar;
import dk.aau.cs.qweb.triple.TripleStarBuilder;
import dk.aau.cs.qweb.triplepattern.TriplePatternBuilder;
import dk.aau.cs.qweb.triplepattern.TripleStarPattern;
import dk.aau.cs.qweb.triplestore.KeyContainer;
import dk.aau.cs.qweb.triplestore.TripleStore;

public class Graph extends GraphBase {

	protected int count;
	private final TripleStore store;
	
	public Graph() {
		store = createTripleStore();
        count = 1; 
	}
	
	@Override
	protected ExtendedIterator<Triple> graphBaseFind(Node s, Node p, Node o) {
		return super.graphBaseFind(s, p, o);
	}
	
	  /**
    Close this graph; if it is now fully closed, destroy its resources and run
    the GraphBase close.
	   */
	@Override
	public void close()
	   {
	   if (--count == 0)
	       {
	       destroy();
	       super.close();
	       }
	   }
	
	/**
	   Answer true iff this triple can be compared for sameValueAs by .equals(),
	   ie, it is a concrete triple with a non-literal object.
	*/
	protected final boolean isSafeForEquality( Triple t )
	   { return t.isConcrete() && !t.getObject().isLiteral(); 
	}

	public TripleStore getStore() {
		return store;
	}

	protected TripleStore createTripleStore()
    { return new TripleStore(); }

    protected void destroy()
    { store.close(); }

    public void performAdd( Triple t ) { 
    	TripleStarBuilder builder = new TripleStarBuilder();
    	List<TripleStar> triples = builder.createTriple(t);
    	for (TripleStar tripleStar : triples) {
    		store.add( tripleStar ); 
		}
    }
    
    public void performDelete( Triple t )
    { 
    	throw new NotImplementedException("MyGraph.performAdd");
    }

    public int graphBaseSize() { 
    	throw new NotImplementedException("MyGraph.graphBaseSize");
    }

    /**
         Answer an ExtendedIterator over all the triples in this graph that match the
         triple-pattern <code>m</code>. Delegated to the store.
     */
    @Override 
    public ExtendedIterator<Triple> graphBaseFind( Triple triplePattern ) {
    	TriplePatternBuilder builder = new TriplePatternBuilder();
    	builder.setSubject(triplePattern.getSubject());
    	builder.setPredicate(triplePattern.getPredicate());
    	builder.setObject(triplePattern.getObject());
    	
    	TripleStarPattern tripleStarPattern = builder.createTriplePatter();
		
		return new DecodingTriplesIterator(store.find(tripleStarPattern),tripleStarPattern );
    }
    
    public ExtendedIterator<KeyContainer> graphBaseFind( TripleStarPattern t ) {
		return store.find(t);
    }
    
    public boolean graphBaseContains( TripleStarPattern t ) {
		return store.contains(t);
    }

    
    /**
         Answer true iff this graph contains <code>t</code>. If <code>t</code>
         happens to be concrete, then we hand responsibility over to the store.
         Otherwise we use the default implementation.
     */
    @Override 
    public boolean graphBaseContains( Triple t ) { 
    	//return t.isConcrete() ? store.contains( t ) : super.graphBaseContains( t ); 
    	throw new NotImplementedException("MyGraph.graphBaseContains");
    }

    /**
        Clear this GraphMem, ie remove all its triples (delegated to the store).
     */
    @Override public void clear()
    { 
        clearStore(); 
        getEventManager().notifyEvent(this, GraphEvents.removeAll ) ;   
    }
    
    /**
    Clear this GraphMem, ie remove all its triples (delegated to the store).
     */
    public void clearStore()
    { 
        store.clear();
    }
    
    @Override
    public GraphEventManager getEventManager()
        { 
        if (gem == null) gem = new SimpleEventManager( ); 
        return gem;
        }
    
    /**
        The event manager that this Graph uses to, well, manage events; allocated on
        demand.
    */
    protected GraphEventManager gem;

	public void eliminateDuplicates() {
		store.eliminateDuplicates();
	}

	public int getNumberOfLookups() {
		return store.getNumberOfLookups();
	}
	
	public int getNumberOfTriples() {
		return store.getNumberOfTriples();
	}
	
}

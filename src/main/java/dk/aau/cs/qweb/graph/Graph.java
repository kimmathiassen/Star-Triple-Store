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

import dk.aau.cs.qweb.dictionary.MyDictionary;
import dk.aau.cs.qweb.triple.TriplePattern;
import dk.aau.cs.qweb.triple.TripleStar;
import dk.aau.cs.qweb.triplestore.TripleStore;

public class Graph extends GraphBase {

	protected int count;
	public final TripleStore store;
	
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


	protected TripleStore createTripleStore()
    { return new TripleStore( this ); }

    protected void destroy()
    { store.close(); }

    public void performAdd( Triple t )
    { 
    	MyDictionary dict = MyDictionary.getInstance();
    	List<TripleStar> triples = dict.createTriple(t);
    	for (TripleStar tripleStar : triples) {
    		store.add( tripleStar ); 
		}
    }
    
    public void performDelete( Triple t )
    { 
    	throw new NotImplementedException("MyGraph.performAdd");
    	//store.delete( t );
    }

    public int graphBaseSize() { 
    	throw new NotImplementedException("MyGraph.graphBaseSize");
    	//return store.size(); 
    }

//    protected GraphStatisticsHandler createStatisticsHandler()
//    { return new GraphMemStatisticsHandler( (MyTripleStore) store ); }

    /**
        The GraphMemStatisticsHandler exploits the existing TripleStoreMem
        indexes to deliver statistics information for single-concrete-node queries
        and for trivial cases of two-concrete-node queries.        
     */
//    protected static class GraphMemStatisticsHandler implements GraphStatisticsHandler
//    {
//        protected final MyTripleStore store;
//
//        public GraphMemStatisticsHandler( MyTripleStore store )
//        { this.store = store; }
//
//        private static class C 
//        {
//            static final int NONE = 0;
//            static final int S = 1, P = 2, O = 4;
//            static final int SP = S + P, SO = S + O, PO = P + O;
//            static final int SPO = S + P + O;
//        }
//
//        /**
//            Answer a good estimate of the number of triples matching (S, P, O)
//            if cheaply possible.
//
//            <p>If there are any reifier triples, return -1. (We may be able to
//            improve this later.)
//
//            <p>If only one of S, P, O is concrete, answers the number of triples
//            with that value in that field.
//
//            <p>If two of S, P, P are concrete and at least one of them has no
//            corresponding triples, answers 0.
//
//            <p>Otherwise answers -1, ie, no information available. (May change;
//            the two degenerate cases might deserve an answer.)
//
//         	@see org.apache.jena.graph.GraphStatisticsHandler#getStatistic(org.apache.jena.graph.Node, org.apache.jena.graph.Node, org.apache.jena.graph.Node)
//         */
//        @Override
//        public long getStatistic( Node S, Node P, Node O )
//        {
//            int concrete = (S.isConcrete() ? C.S : 0) + (P.isConcrete() ? C.P : 0) + (O.isConcrete() ? C.O : 0);
//            switch (concrete)
//            {
//                case C.NONE:
//                    return store.size();
//
//                case C.S:
//                    return countInMap( S, store.getSubjects() );
//
//                case C.SP:
//                    return countsInMap( S, store.getSubjects(), P, store.getPredicates() );
//
//                case C.SO:
//                    return countsInMap( S, store.getSubjects(), O, store.getObjects() );
//
//                case C.P:
//                    return countInMap( P, store.getPredicates() );
//
//                case C.PO:
//                    return countsInMap( P, store.getPredicates(), O, store.getObjects() );
//
//                case C.O:
//                    return countInMap( O, store.getObjects() );
//
//                case C.SPO:
//                    return store.contains( Triple.create( S, P, O ) ) ? 1 : 0;
//            }
//            return -1;
//        }
//
//        public long countsInMap( Node a, NodeToTriplesMapMem mapA, Node b, NodeToTriplesMapMem mapB )
//        {
//            long countA = countInMap( a, mapA ), countB = countInMap( b, mapB );
//            return countA == 0 || countB == 0 ? 0 : -1L;
//        }
//
//        public long countInMap( Node n, NodeToTriplesMapMem map )
//        {
//            //TripleBunch b = map.get( n.getIndexingValue() );
//            //return b == null ? 0 : b.size();
//        	//TODO
//        	return 0;
//        }
//    }

    /**
         Answer an ExtendedIterator over all the triples in this graph that match the
         triple-pattern <code>m</code>. Delegated to the store.
     */
    @Override 
    public ExtendedIterator<Triple> graphBaseFind( Triple triplePattern ) {
    	MyDictionary dict = MyDictionary.getInstance();
    	TriplePattern triple = dict.createTriplePattern(triplePattern);
		
		return new DecodingTriplesIterator(store.find(triple) );
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
	
}

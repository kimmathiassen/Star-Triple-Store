package dk.aau.cs.qweb.triplestore;

import java.util.Collections;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.util.iterator.ExtendedIterator;

import dk.aau.cs.qweb.triple.TripleStarPattern;
import dk.aau.cs.qweb.triple.TripleStar;
import dk.aau.cs.qweb.triplestore.Index.Field;

public class TripleStore {
	protected final Graph parent;
	
    protected Index SPO;
    protected Index POS;
    protected Index OSP;
    
    public TripleStore ( Graph parent) { 
    	this.SPO = new Index(Field.S,Field.P,Field.O );
    	this.POS = new Index(Field.P, Field.O, Field.S );
        this.OSP = new Index(Field.O, Field.S,Field.P);
        this.parent = parent; 
    }   
    
    /**
        Destroy this triple store - discard the indexes.
    */
    public void close()
         { SPO = POS = OSP = null; }
     
     /**
          Add a triple to this triple store.
     */
    public void add( TripleStar t ) {
         SPO.add( t );
         POS.add( t );
         OSP.add( t ); 
         }
     
     /**
          Remove a triple from this triple store.
     */
    public void delete( TripleStar t ) {
         if (SPO.remove( t ))
             {
             POS.remove( t );
             OSP.remove( t ); 
             }
         }
     
     /**
          Clear this store, ie remove all triples from it.
     */
    public void clear()
         {
         SPO.clear();
         POS.clear();
         OSP.clear();
         }

     /**
          Answer the size (number of triples) of this triple store.
     */
    public long size()
         { return SPO.size(); }
     
     /**
          Answer true iff this triple store is empty.
     */
    public boolean isEmpty()
         { return SPO.isEmpty(); }
     
    public ExtendedIterator<Node> listSubjects() { 
    	throw new NotImplementedException("MyTriplestore.listSubjects");
    }
     
    public ExtendedIterator<Node> listPredicates() {
    	throw new NotImplementedException("MyTriplestore.listPredicates");
    }
    
    public ExtendedIterator<Node> listObjects() {
    	throw new NotImplementedException("MyTriplestore.listObjects");
    }
     
     /**
          Answer true iff this triple store contains the (concrete) triple <code>t</code>.
     */
    public boolean contains( TripleStar t ) { 
    	return SPO.containsBySameValueAs( t ); 
    }
     
    
     public boolean containsByEquality( Triple t ) { 
    	 throw new NotImplementedException("MyTripleStore.containsByEquality");
    	 //return SPO.contains( t ); 
     }
     
     /** 
         Answer an ExtendedIterator returning all the triples from this store that
         match the pattern <code>m = (S, P, O)</code>.
         
         <p>Because the node-to-triples maps index on each of subject, predicate,
         and (non-literal) object, concrete S/P/O patterns can immediately select
         an appropriate map. Because the match for literals must be by sameValueAs,
         not equality, the optimisation is not applied for literals. [This is probably a
         Bad Thing for strings.]
         
         <p>Practice suggests doing the predicate test <i>last</i>, because there are
         "usually" many more statements than predicates, so the predicate doesn't
         cut down the search space very much. By "practice suggests" I mean that
         when the order went, accidentally, from S/O/P to S/P/O, performance on
         (ANY, P, O) searches on largish models with few predicates declined
         dramatically - specifically on the not-galen.owl ontology.
     */
    public ExtendedIterator<TripleStar> find( TripleStarPattern t ) {
    	if (!t.doesAllKeysExistInDictionary()) {
			return new TripleStoreIterator( parent, Collections.<TripleStar>emptyList().iterator());
		}
    	
		if (t.getSubject().isConcreate() && t.getPredicate().isConcreate())
		    return new TripleStoreIterator( parent, SPO.iterator( t ));
		else if (t.getObject().isConcreate() && t.getSubject().isConcreate())
		    return new TripleStoreIterator( parent, OSP.iterator( t ));
		else if (t.getPredicate().isConcreate() && t.getObject().isConcreate())
		    return new TripleStoreIterator( parent, POS.iterator( t ));
		else if (t.getSubject().isConcreate()) 
			return new TripleStoreIterator( parent, SPO.iterator( t ));
		else if (t.getObject().isConcreate()) 
			return new TripleStoreIterator( parent, OSP.iterator( t ));
		else if (t.getPredicate().isConcreate()) 
			return new TripleStoreIterator( parent, POS.iterator( t ));
		else return new TripleStoreIterator( parent, SPO.iterateAll());
    }
}

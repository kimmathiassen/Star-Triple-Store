package dk.aau.cs.qweb.triplestore;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.util.iterator.ExtendedIterator;

import dk.aau.cs.qweb.triple.MyTriple;

public class MyTripleStore {
	protected final Graph parent;
	
    protected Index SPO;
    protected Index POS;
    protected Index OSP;
    
    protected MyTripleStore ( Graph parent)
        { 
//    	this.subjects =new NodeToTriplesMapMem( Field.fieldSubject, Field.fieldPredicate, Field.fieldObject );
//    	this.predicates = new NodeToTriplesMapMem( Field.fieldPredicate, Field.fieldObject, Field.fieldSubject );
//        this.objects = new NodeToTriplesMapMem( Field.fieldObject, Field.fieldSubject, Field.fieldPredicate );
    	
    	this.SPO = new Index( );
    	this.POS = new Index( );
        this.OSP = new Index( );
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
    public void add( MyTriple t ) {
         if (SPO.add( t ))
             {
             POS.add( t );
             OSP.add( t ); 
             }
         }
     
     /**
          Remove a triple from this triple store.
     */
    public void delete( MyTriple t ) {
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
    public int size()
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
    public boolean contains( MyTriple t ) { 
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
    public ExtendedIterator<MyTriple> find( MyTriple t ) {
    	 
         if (t.isSubjectConcrete())
             return new MyTripleStoreIterator( parent, SPO.iterator( t ), SPO, POS, OSP );
         else if (t.isObjectConcrete())
             return new MyTripleStoreIterator( parent, OSP.iterator( t ), OSP, SPO, POS );
         else if (t.isPredicateConcrete())
             return new MyTripleStoreIterator( parent, POS.iterator( t ), POS, SPO, OSP );
         else
             return new MyTripleStoreIterator( parent, SPO.iterateAll(), SPO, POS, OSP );
         }


	
	
    
}

package dk.aau.cs.qweb.triplestore;

import java.util.Collections;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.util.iterator.ExtendedIterator;

import dk.aau.cs.qweb.dictionary.HashNodeDictionary;
import dk.aau.cs.qweb.dictionary.PrefixDictionary;
import dk.aau.cs.qweb.triple.TripleStar;
import dk.aau.cs.qweb.triple.TripleStarPattern;
import dk.aau.cs.qweb.triplestore.hashindex.HashIndex;
import dk.aau.cs.qweb.triplestore.hashindex.MapIndex.Field;

public class TripleStore {
	protected final Graph parent;
	
    protected Index SPO;
    protected Index POS;
    protected Index OSP;
    private int triplesAddedCount = 0;
    long millis = System.currentTimeMillis();
    int numberOfLookups = 0;
    
    public TripleStore ( Graph parent) { 
    	this.SPO = new HashIndex(Field.S, Field.P, Field.O );
    	this.POS = new HashIndex(Field.P, Field.O, Field.S );
        this.OSP = new HashIndex(Field.O, Field.S, Field.P);
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
    	triplesAddedCount++;
        SPO.add( t );
        POS.add( t );
        OSP.add( t ); 
        if (triplesAddedCount % 100000 == 0) {
			System.out.println("Triples added: " + triplesAddedCount+" Time diff: "+(millis - System.currentTimeMillis()));
			millis = System.currentTimeMillis();
		}
        
        if (triplesAddedCount % 10000000 == 0) {
			System.out.println("Index contains: " + SPO.size() );
			int embeddedTriples = HashNodeDictionary.getInstance().getNumberOfEmbeddedTriples();
			int overflow = HashNodeDictionary.getInstance().getNumberOfReferenceTriples();
			int size = HashNodeDictionary.getInstance().size();
			System.out.println("Node Dictionary size: " + size);
			System.out.println("+ Normal triples: " + (size-(embeddedTriples)));
			System.out.println("+ Embedded triples: " + (embeddedTriples));
			System.out.println("+ Overflow triples: " + (overflow));
			System.out.println("Prefix Dictionary size: " + PrefixDictionary.getInstance().size());
		}
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
	public boolean contains( TripleStarPattern t ) { 
		return SPO.contains( t ); 
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
    public ExtendedIterator<KeyContainer> find( TripleStarPattern t ) {
    	numberOfLookups++;
    	if (!t.doesAllKeysExistInDictionary()) {
			return new TripleStoreIterator( parent, Collections.<KeyContainer>emptyList().iterator());
		}
    	
    	if (t.getSubject().isConcrete() && t.getPredicate().isConcrete())
		    return new TripleStoreIterator( parent, SPO.iterator( t ));
		else if (t.getObject().isConcrete() && t.getSubject().isConcrete())
		    return new TripleStoreIterator( parent, OSP.iterator( t ));
		else if (t.getPredicate().isConcrete() && t.getObject().isConcrete())
		    return new TripleStoreIterator( parent, POS.iterator( t ));
		else if (t.getSubject().isConcrete()) 
			return new TripleStoreIterator( parent, SPO.iterator( t ));
		else if (t.getObject().isConcrete()) 
			return new TripleStoreIterator( parent, OSP.iterator( t ));
		else if (t.getPredicate().isConcrete()) 
			return new TripleStoreIterator( parent, POS.iterator( t ));
		else return new TripleStoreIterator( parent, SPO.iterateAll());
    }

	public void eliminateDuplicates() {
		SPO.eliminateDuplicates();
		POS.eliminateDuplicates();
		OSP.eliminateDuplicates();
	}

	public int getNumberOfLookups() {
		return numberOfLookups;
	}

	public Index getSPO() {
		return SPO;
	}
	public Index getPOS() {
		return POS;
	}
	public Index getOSP() {
		return OSP;
	}
}

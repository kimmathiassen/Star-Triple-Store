package dk.aau.cs.qweb.triplestore;

import java.util.Collections;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.util.iterator.WrappedIterator;
import org.apache.log4j.Logger;

import dk.aau.cs.qweb.dictionary.AbstractNodeDictionary;
import dk.aau.cs.qweb.dictionary.NodeDictionary;
import dk.aau.cs.qweb.dictionary.NodeDictionaryFactory;
import dk.aau.cs.qweb.dictionary.PrefixDictionary;
import dk.aau.cs.qweb.triple.TripleStar;
import dk.aau.cs.qweb.triplepattern.TripleStarPattern;
import dk.aau.cs.qweb.triplestore.flatindex.FlatIndex;
import dk.aau.cs.qweb.triplestore.hashindex.HashIndex;
import dk.aau.cs.qweb.triplestore.hashindex.MapIndex.Field;
import dk.aau.cs.qweb.triplestore.treeindex.TreeIndex;

public class TripleStore {
	static Logger log = Logger.getLogger(AbstractNodeDictionary.class.getName());
    protected Index SPO;
    protected Index POS;
    protected Index OSP;
    private int triplesAddedCount = 0;
    long millis = System.currentTimeMillis();
    int numberOfLookups = 0;
    
    public TripleStore () { 
    	this.SPO = IndexFactory.getIndex(Field.S, Field.P, Field.O );
    	this.POS = IndexFactory.getIndex(Field.P, Field.O, Field.S );
        this.OSP = IndexFactory.getIndex(Field.O, Field.S, Field.P );
    }   
    
    /**
        Destroy this triple store - discard the indexes.
    */
    public void close()
         { SPO = POS = OSP = null; }
     
     /**
          Add a triple to this triple store.
          This method is also responsible for outputting a status to the command line.
     */
    public void add( TripleStar t ) {
    	triplesAddedCount++;
        SPO.add( t );
        POS.add( t );
        OSP.add( t ); 
        if (triplesAddedCount % 100000 == 0) {
        	log.info("Triples added: " + triplesAddedCount+" Time diff: "+(millis - System.currentTimeMillis()));
			millis = System.currentTimeMillis();
		}
        
        if (triplesAddedCount % 10000000 == 0) {
        	log.info("Index contains: " + SPO.size() );
			NodeDictionary dict = NodeDictionaryFactory.getDictionary();
			int embeddedTriples = dict.getNumberOfEmbeddedTriples();
			int overflow = dict.getNumberOfReferenceTriples();
			int size = dict.size();
			log.info("Node Dictionary size: " + size);
			log.info("+ Embedded triples: " + (embeddedTriples));
			log.info("+ Overflow triples: " + (overflow));
			log.info("Prefix Dictionary size: " + PrefixDictionary.getInstance().size());
			
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
     }
     
     /** 
         Answer an ExtendedIterator returning all the triples from this store that
         match the pattern <code>m = (S, P, O)</code>.
         The order of the indexes are: SPO, OSP, POS (this ordering is based 
         on experiments described in the Jena documentation).
         
         SPO answers triple patterns: SP* and S**
         
         OSP answers triple patterns: OS* and O**
         
         POS answers triple patterns: PO* and P**
     */
    public ExtendedIterator<KeyContainer> find( TripleStarPattern tp ) {
    	numberOfLookups++;
    	if (!tp.doesAllKeysExistInDictionary()) {
			return WrappedIterator.create(Collections.<KeyContainer>emptyList().iterator());
		}
    	
    	if (tp.getSubject().isConcrete() && tp.getPredicate().isConcrete())
		    return WrappedIterator.create(SPO.iterator( tp ));
		else if (tp.getObject().isConcrete() && tp.getSubject().isConcrete())
		    return WrappedIterator.create(OSP.iterator( tp ));
		else if (tp.getPredicate().isConcrete() && tp.getObject().isConcrete())
		    return WrappedIterator.create(POS.iterator( tp ));
		else if (tp.getSubject().isConcrete()) 
			return WrappedIterator.create(SPO.iterator( tp ));
		else if (tp.getObject().isConcrete()) 
			return WrappedIterator.create(OSP.iterator( tp ));
		else if (tp.getPredicate().isConcrete()) 
			return WrappedIterator.create(POS.iterator( tp ));
		else return WrappedIterator.create(SPO.iterateAll());
    }

	/**
	 * 	Foreach of the materialized indexes (SPO, POS, OSP) duplicates are eliminated.
	 * See the implementation of Index ({@link Index}) for more details {@link HashIndex}, {@link FlatIndex}, {@link TreeIndex}
	 */
	public int eliminateDuplicates() {
		int count = SPO.eliminateDuplicates();
		POS.eliminateDuplicates();
		OSP.eliminateDuplicates();
		
		return count;
	}

	public int getNumberOfLookups() {
		return numberOfLookups;
	}

	public int getNumberOfTriples() {
		return triplesAddedCount;
	}
	
	/**
	 * Returns the SPO index, used for testing purposes.
	 */
	public Index getSPO() {
		return SPO;
	}
	
	/**
	 * Returns the SPO index, used for testing purposes.
	 */
	public Index getPOS() {
		return POS;
	}
	
	/**
	 * Returns the SPO index, used for testing purposes.
	 */
	public Index getOSP() {
		return OSP;
	}
}

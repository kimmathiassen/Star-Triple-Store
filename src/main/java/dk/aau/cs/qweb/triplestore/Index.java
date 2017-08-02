package dk.aau.cs.qweb.triplestore;

import java.util.Iterator;

import dk.aau.cs.qweb.triple.TripleStar;
import dk.aau.cs.qweb.triple.TripleStarPattern;

public interface Index   {
	
	void add(final TripleStar t) ;

	boolean remove(TripleStar t) ;

	void clear() ;

	long size() ;

	boolean isEmpty() ;

	boolean contains(TripleStarPattern t) ;

	Iterator<KeyContainer> iterator(TripleStarPattern triple) ;

	void removedOneViaIterator() ;

	Iterator<KeyContainer> iterateAll() ;

	void eliminateDuplicates() ;
}

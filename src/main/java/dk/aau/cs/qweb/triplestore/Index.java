package dk.aau.cs.qweb.triplestore;

import java.util.Iterator;

import dk.aau.cs.qweb.triple.TripleStar;
import dk.aau.cs.qweb.triple.TripleStarPattern;

public interface Index   {
	
	public void add(final TripleStar t) ;

	public boolean remove(TripleStar t) ;

	public void clear() ;

	public long size() ;

	public boolean isEmpty() ;

	public boolean contains(TripleStarPattern t) ;

	public Iterator<KeyContainer> iterator(TripleStarPattern triple) ;

	public void removedOneViaIterator() ;

	public Iterator<KeyContainer> iterateAll() ;

	public void eliminateDuplicates() ;
}

package dk.aau.cs.qweb.triplestore;

import java.util.Iterator;

import dk.aau.cs.qweb.triple.TripleStar;
import dk.aau.cs.qweb.triplepattern.TripleStarPattern;

/**
 * An index is the physical storage of the triples.
 * Unfortunately the Index is tightly coupled with the Triple Store {@link TripleStore}
 */
public interface Index   {
	/**
	 * Add a triple to the index structure
	 */
	void add(final TripleStar t) ;


	/**
	 * remove triple from index structure.
	 */
	boolean remove(TripleStar t) ;

	/**
	 *  remove all triples from index
	 */
	void clear() ;

	
	/**
	 * @return the number of triples the index contains.
	 */
	long size() ;

	/**
	 * @return true if the size is zero
	 */
	boolean isEmpty() ;

	/**
	 * @param triple pattern with no variables
	 * @return return true if the triple pattern is contained.
	 */
	boolean contains(TripleStarPattern t) ;

	
	/**
	 * @param triple pattern
	 * @return iterator over all triples matching the triple pattern
	 */
	Iterator<KeyContainer> iterator(TripleStarPattern triple) ;

	
	/**
	 * Use when the triple pattern have no concrete elements. I.e. all variables.
	 * @return return all triples.
	 * 
	 */
	Iterator<KeyContainer> iterateAll() ;

	
	/**
	 * Remove all duplicate triples
	 * return the count
	 */
	int eliminateDuplicates() ;
}

package dk.aau.cs.qweb.triplepattern;

import dk.aau.cs.qweb.triple.Key;

/**
 * Defines the elements of a triple pattern.
 */
public interface Element {

	/**
	 * if element is of the type key, i.e. a concrete subject, predicate, or object encoded as a key
	 */
	boolean isKey();
	Key getKey();
	
	/**
	 * if element is an embedded triple
	 */
	boolean isEmbeddedTriplePattern();
	TripleStarPattern getTriplePattern();
	
	/**
	 * if element is a variable
	 */
	boolean isVariable();
	Variable getVariable();
	
	/**
	 * if element is a key, the call should be recursive if case the element is an embedded triple. If one element in an embedded triple is an variable this method should return false.
	 */
	boolean isConcrete();
}

package dk.aau.cs.qweb.triple;

public interface Element {

	boolean isKey();
	Key getKey();
	
	boolean isEmbeddedTriplePattern();
	TripleStarPattern getTriplePattern();
	
	boolean isVariable();
	Variable getVariable();
	
	// Does the triple pattern contain any variables?
	boolean isConcrete();
}

package dk.aau.cs.qweb.triple;

public interface StarNode {

	boolean isKey();
	Key getKey();
	
	boolean isEmbeddedTriplePattern();
	TripleStarPattern getTriplePattern();
	
	boolean isVariable();
	Variable getVariable();
}

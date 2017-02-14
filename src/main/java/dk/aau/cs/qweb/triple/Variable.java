package dk.aau.cs.qweb.triple;

public class Variable implements StarNode {

	public final String label;
	
	public Variable (String label) {
		this.label = label;
	}
	
	@Override
	public boolean isKey() {
		return false;
	}

	@Override
	public Key getKey() {
		throw new IllegalArgumentException("Is not of the type Key");
	}

	@Override
	public boolean isEmbeddedTriplePattern() {
		return false;
	}

	@Override
	public TripleStarPattern getTriplePattern() {
		throw new IllegalArgumentException("Is not of the type TripleStarPattern");
	}

	@Override
	public boolean isVariable() {
		return true;
	}

	@Override
	public Variable getVariable() {
		return this;
	}

	@Override
	public boolean isConcreate() {
		return false;
	}

}

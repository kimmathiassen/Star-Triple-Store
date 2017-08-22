package dk.aau.cs.qweb.triplepattern;

import org.apache.jena.reasoner.TriplePattern;

import dk.aau.cs.qweb.triple.Key;

/**
 * A variable as part of a triple star pattern {@link TriplePattern}
 *
 */
public class Variable implements Element {

	private final int id;
	
	public Variable (int id) {
		this.id = id;
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
	public boolean isConcrete() {
		return false;
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return "#"+String.valueOf(getId());
	}

}

package dk.aau.cs.qweb.triple;

import java.util.Objects;

public class Key implements StarNode{
	
	public Key(long id) {
		this.id=id;
	}
	
	public long getId(){
		return id;
	}
	
	@Override
	public String toString() {
		//return String.valueOf(id);
		return Long.toBinaryString(id);
	}

	private final long id;
	
	@Override
	public boolean equals(final Object other) {
		if (other instanceof Key) {
			Key casted = (Key)other;
			return (this.id == casted.id);
		} else {
			return super.equals(other);
		}
	}
	
	@Override
    public int hashCode() {
        return Objects.hash(id);
    }

	@Override
	public boolean isKey() {
		return true;
	}

	@Override
	public Key getKey() {
		return this;
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
		return false;
	}

	@Override
	public Variable getVariable() {
		throw new IllegalArgumentException("Is not of the type Variable");
	}

	@Override
	public boolean isConcreate() {
		return true;
	}
}

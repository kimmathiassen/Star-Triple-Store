package dk.aau.cs.qweb.triplestore.flatindex;

import dk.aau.cs.qweb.triple.Key;

/**
 * This key is used by the Flat Index.
 * It have two states, single and double key.
 * 
 * Depending on the state only one or both keys are used in the equals method.
 * here are some examples of the behaviour
 * 
 * s1 == s1
 * s1 != s2
 * s1,s2 == s1,s2
 * s1,s2 != s2,s1
 * s1 != s1,s2
 *
 */
public class FlatKey {
	private Key field1;
	private Key field2;

	public FlatKey(Key field1) {
		this.field1 = field1;
		this.field2 = null;
	}
	
	public FlatKey(Key field1, Key field2) {
		this.field1 = field1;
		this.field2 = field2;
	}
	
	public Key getFirstField() {
		return field1;
	}
	
	public Key getSecondField() {
		return field2;
	}
	
	@Override
	public int hashCode() {
		return field1.hashCode()+(field2 != null ? field2.hashCode() : 0);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof FlatKey) {
			if (field2 == null) {
				return field1.equals(((FlatKey) o).getFirstField());
			} else {
				return field1.equals(((FlatKey) o).getFirstField()) && field2.equals(((FlatKey) o).getSecondField());
			}
		  }
		return false;
	}
	
	@Override
	public String toString() {
		if (field2 == null) {
			return "("+field1+",*)";
		} else {
			return "("+field1+","+field2+")";
		}
	}
}

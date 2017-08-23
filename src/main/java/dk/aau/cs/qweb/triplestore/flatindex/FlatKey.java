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
	private Key key1;
	private Key key2;

	public FlatKey(Key field1) {
		this.key1 = field1;
		this.key2 = null;
	}
	
	public FlatKey(Key field1, Key field2) {
		this.key1 = field1;
		this.key2 = field2;
	}
	
	public Key getFirstField() {
		return key1;
	}
	
	public Key getSecondField() {
		return key2;
	}
	
	@Override
	public int hashCode() {
		return key1.hashCode()+(key2 != null ? key2.hashCode() : 0);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof FlatKey) {
			if (key2 == null) {
				return key1.equals(((FlatKey) o).getFirstField());
			} else {
				return key1.equals(((FlatKey) o).getFirstField()) && key2.equals(((FlatKey) o).getSecondField());
			}
		  }
		return false;
	}
	
	@Override
	public String toString() {
		if (key2 == null) {
			return "("+key1+",*)";
		} else {
			return "("+key1+","+key2+")";
		}
	}
}

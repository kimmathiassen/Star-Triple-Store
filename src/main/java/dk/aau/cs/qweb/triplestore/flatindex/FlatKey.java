package dk.aau.cs.qweb.triplestore.flatindex;

import dk.aau.cs.qweb.triple.Key;

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
//		int result = 17;
//		
//		result = 31 * result + (int)(field1.getId() ^ (field1.getId() >>> 32));
//		if (field2 == null) {
//			result = 31 * result + 0; 
//		} else {
//			result = 31 * result + (int)(field2.getId() ^ (field2.getId() >>> 32)); 
//		}
		
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

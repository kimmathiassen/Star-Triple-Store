package dk.aau.cs.qweb.triple;

import java.util.Objects;

public class Key {
	
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
	public boolean equals(Object other) {
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
}

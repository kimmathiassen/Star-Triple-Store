package dk.aau.cs.qweb.triple;

public class Key {
	
	public Key(long id) {
		this.id=id;
	}
	
	public long getId(){
		return id;
	}
	
	@Override
	public String toString() {
		return String.valueOf(id);
	}

	private final long id;
}

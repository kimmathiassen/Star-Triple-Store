package dk.aau.cs.qweb.model;


public class SimpleBlankNode extends  SimpleNode {

	protected SimpleBlankNode(String label) {
		super(label);
	}
	
	@Override
	public boolean isBlank() {
		return true; 
	}
}

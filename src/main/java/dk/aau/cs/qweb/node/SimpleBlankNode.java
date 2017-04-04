package dk.aau.cs.qweb.node;


public class SimpleBlankNode extends  SimpleNode {

	protected SimpleBlankNode(String label) {
		super(label);
	}
	
	@Override
	public boolean isBlank() {
		return true; 
	}
}

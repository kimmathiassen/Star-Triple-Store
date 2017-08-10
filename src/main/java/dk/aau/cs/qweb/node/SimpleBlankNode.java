package dk.aau.cs.qweb.node;


public class SimpleBlankNode extends  SimpleNode {

	protected SimpleBlankNode(String label) {
		super(label);
	}
	
	@Override
	public boolean isBlank() {
		return true; 
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof SimpleBlankNode) {
			SimpleBlankNode other = (SimpleBlankNode) o;
			return label.equals(other.label);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return (String) label;
	}
	
	@Override
	public String serialize() {
		return "B"+toString();
	}
}

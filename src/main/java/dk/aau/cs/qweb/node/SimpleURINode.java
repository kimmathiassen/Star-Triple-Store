package dk.aau.cs.qweb.node;

public class SimpleURINode extends SimpleNode {

	protected SimpleURINode(String label) {
		super(label);
	}
	
	@Override
	public boolean isURI() {
		return true;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof SimpleURINode) {
			SimpleURINode other = (SimpleURINode) o;
			return getURI().equals(other.getURI());
		}
		return false;
	}
	
	@Override 
	public String getURI() {
		return (String) label;
	}
}

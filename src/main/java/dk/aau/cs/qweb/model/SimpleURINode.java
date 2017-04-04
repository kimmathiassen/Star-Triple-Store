package dk.aau.cs.qweb.model;

import org.apache.jena.graph.Node_URI;

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
		if (o instanceof SimpleNode) {
			SimpleNode other = (SimpleNode) o;
			return label.equals(other);
		} else if (o instanceof Node_URI) {
			Node_URI other = (Node_URI) o;
			this.label.equals(other.toString());
		}
		return false;
	}
	
	@Override 
	public String getURI() {
		return (String) label;
	}
}

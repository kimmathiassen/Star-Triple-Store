package dk.aau.cs.qweb.node;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.jena.graph.NodeVisitor;

public abstract class SimpleNode extends StarNode {

	protected SimpleNode(String label) {
		super(label);
	}

	@Override
	public Object visitWith(NodeVisitor v) {
		throw new NotImplementedException("simpleNode.visitWith()");
	}

	@Override
	public boolean isConcrete() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof SimpleNode) {
			SimpleNode other = (SimpleNode) o;
			return label.equals(other);
		}
		return false;
	}
	
	public String getLabel() {
		return (String) label;
	}
}

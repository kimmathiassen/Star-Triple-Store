package dk.aau.cs.qweb.model;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.jena.graph.NodeVisitor;
import org.apache.jena.graph.Node_Concrete;

public abstract class SimpleNode extends Node_Concrete {

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

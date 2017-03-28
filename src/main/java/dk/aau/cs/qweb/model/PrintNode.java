package dk.aau.cs.qweb.model;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.jena.graph.NodeVisitor;
import org.apache.jena.graph.Node_Concrete;

public class PrintNode extends Node_Concrete {

	PrintNode(String label) {
		super(label);
	}

	@Override
	public Object visitWith(NodeVisitor v) {
		throw new NotImplementedException("Not a real node, only ment for serializing");
	}

	@Override
	public boolean isConcrete() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		return false;
	}
	
	@Override
	public String toString() {
		return (String) label;
	}

}

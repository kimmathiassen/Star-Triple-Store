package dk.aau.cs.qweb.node;

import org.apache.jena.graph.Node_Concrete;

public abstract class StarNode extends Node_Concrete {

	protected StarNode(Object label) {
		super(label);
	}

	public abstract String serialize();
	
}

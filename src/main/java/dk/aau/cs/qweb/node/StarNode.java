package dk.aau.cs.qweb.node;

import org.apache.jena.graph.Node_Concrete;

/**
 * This class, in practice its children, replace the jena node classes.
 * The jena node classes contains some automatical caching which we do not wish to reuse because we use a dictionary instead.
 * The Factory class {@link NodeFactoryStar} is used to build all instances of this class.
 * This class, and its children, are used at three points in the program
 * 1) When the parser runs in craetes triples consisting of nodes, these are later encoded by the dictionary.
 * 2) During query evaluation, if an sparql operator (op) is not supported in sparql*, 
 * the iterator will be decoded into an iterator of triples containing StarNodes.
 * After the operator has been executed, the iterator will be encoded into a KeyContainer again. 
 * 3) When they query result needs to be serialized, all keys will be converted to StarNodes and printed. 
 */
public abstract class StarNode extends Node_Concrete {

	protected StarNode(Object label) {
		super(label);
	}

	/**
	 * This metod is used to create a serialization of the node which can be saved to disk and loaded for later use. 
	 */
	public abstract String serialize();
	
}

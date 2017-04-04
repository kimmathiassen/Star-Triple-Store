package dk.aau.cs.qweb.node;

import java.util.Objects;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeVisitor;
import org.apache.jena.graph.Node_Concrete;

public class Node_Triple extends Node_Concrete {

	private Node node1;
	private Node node2;
	private Node node3;

	protected Node_Triple(Object label) {
		super(label);
	}
	
	protected Node_Triple(Node node1, Node node2, Node node3) {
		super("<<"+node1+" "+node2+" "+node3+">>");
		this.node1 = node1;
		this.node2 = node2;
		this.node3 = node3;
	}
	
	public Node getSubject() {
		return node1;
	}
	
	public Node getPredicate() {
		return node2;
	}
	
	public Node getObject() {
		return node3;
	}

	@Override
	public Object visitWith(NodeVisitor v) {
		throw new NotImplementedException("Node_Triple.visitWith");
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Node_Triple) {
			Node_Triple casted = (Node_Triple)other;
			return node1.equals(casted.getSubject()) && node2.equals(casted.getPredicate()) && node3.equals(casted.getObject()); 
		} else {
			return this.hashCode() == other.hashCode();
		}
	}
	
	@Override
    public int hashCode() {
        return Objects.hash(node1,node2,node3);
    }
	
	@Override
	public String toString() {
		return "<<"+node1+" "+node2+" "+node3+">>";
	}

}
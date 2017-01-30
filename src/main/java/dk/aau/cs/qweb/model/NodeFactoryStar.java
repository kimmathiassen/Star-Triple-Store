package dk.aau.cs.qweb.model;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

public class NodeFactoryStar extends NodeFactory {

	public Node createEmbeddedNode(Node node1, Node node2, Node node3) {
		return new Node_Triple(node1,node2,node3);
	}
}

package dk.aau.cs.qweb.node;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

public class NodeFactoryStar extends NodeFactory {

	public static Node createEmbeddedNode(Node node1, Node node2, Node node3) {
		return new Node_Triple(node1,node2,node3);
	}

	public static Node createSimpleURINode(String label) {
		return new SimpleURINode(label);
	}
	
	public static Node createSimpleLiteralNode(String label, XSDDatatype xsdinteger) {
		return new SimpleLiteralNode(label);
	}

	public static Node createSimpleLiteralNode(java.lang.String lexicalForm, RDFDatatype dType) {
		if (dType.equals(XSDDatatype.XSDstring) || dType.equals(XSDDatatype.XSD)) {
			return new SimpleLiteralNode("\""+lexicalForm+"\"^^"+dType.getURI());
		} else {
			return new SimpleLiteralNode(lexicalForm+"^^"+dType.getURI());
		}
	}

	public static Node createSimpleLiteralNode(String lexicalForm, String langTag) {
		return new SimpleLiteralNode("\""+lexicalForm+"\"^^"+langTag);
	}

	public static Node createSimpleLiteralNode(String lexicalForm) {
		return new SimpleLiteralNode("\""+lexicalForm+"\"");
	}

	public static Node createSimpleBlankNode(Node blankNode) {
		return new SimpleBlankNode(blankNode.getBlankNodeLabel());
	}

}

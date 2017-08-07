package dk.aau.cs.qweb.node;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

import dk.aau.cs.qweb.dictionary.PrefixDictionary;
import dk.aau.cs.qweb.main.Config;

public class NodeFactoryStar extends NodeFactory {

	public static Node createEmbeddedNode(Node node1, Node node2, Node node3) {
		return new Node_Triple(node1,node2,node3);
	}

	public static SimpleURINode createSimpleURINode(String label) {
		String normalized = normalizeURI(label);
		if (Config.isPrefixDictionaryEnabled()) {
			if (normalized.startsWith("http")) {
				String[] hashTagSplit = normalized.split("#");
				if (hashTagSplit.length == 2) {
					int prefix = PrefixDictionary.getInstance().createId(hashTagSplit[0]);
					return new SimpleURINode(prefix,hashTagSplit[1]);
				} else {
					String[] slashSplit = normalized.split("/");
					if (slashSplit[slashSplit.length-1].equals("")) { //If end on slash
						String body = normalized.substring(0, normalized.length()-slashSplit[slashSplit.length-1].length());
						int prefix = PrefixDictionary.getInstance().createId(body);
						String head = slashSplit[slashSplit.length-2];
						return new SimpleURINode(prefix,head);
					} else {
						String body = normalized.substring(0, normalized.length()-slashSplit[slashSplit.length-1].length());
						int prefix = PrefixDictionary.getInstance().createId(body);
						String head = slashSplit[slashSplit.length-1];
						return new SimpleURINode(prefix,head);
					}
				}
			} else if (normalized.startsWith("file:///")) {
				String[] slashSplit = normalized.split("/");
				String head = "";
				int prefix;
				if (slashSplit[slashSplit.length-1].equals("")) { //If end on slash
					String body = normalized.substring(0, normalized.length()-slashSplit[slashSplit.length-1].length());
					prefix = PrefixDictionary.getInstance().createId(body);
					head = slashSplit[slashSplit.length-2];
				} else {
					String body = normalized.substring(0, normalized.length()-slashSplit[slashSplit.length-1].length());
					prefix = PrefixDictionary.getInstance().createId(body);
					head = slashSplit[slashSplit.length-1];
				}
				
				if (Config.ignoreFilePrefixInQueries()) {
					return new SimpleURINode(head);
				} else {
					return new SimpleURINode(prefix,head);
				}
			} else {
				return new SimpleURINode(normalized);
			}
		} else {
			return new SimpleURINode(normalized);
		}
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
	
	private static String normalizeURI(String uri) {
		if (uri.trim().startsWith("<")) {
			uri = uri.trim().substring(1);
		}
		if (uri.trim().endsWith(">")) {
			uri = uri.trim().substring(0, uri.trim().length()-1);
		}
		return uri;
	}
}

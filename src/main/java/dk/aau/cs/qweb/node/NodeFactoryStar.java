package dk.aau.cs.qweb.node;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

import dk.aau.cs.qweb.dictionary.PrefixDictionary;
import dk.aau.cs.qweb.main.Config;

public class NodeFactoryStar extends NodeFactory {

	public static StarNode createEmbeddedNode(Node node1, Node node2, Node node3) {
		return new EmbeddedNode(node1,node2,node3);
	}

	public static StarNode createSimpleURINode(String label) {
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
	
	//This seems wrong
	public static StarNode createSimpleLiteralNode(String label, XSDDatatype xsdinteger) {
		return new SimpleLiteralNode(label);
	}

	public static StarNode createSimpleLiteralNode(String lexicalForm, RDFDatatype dType) {
		if (dType.equals(XSDDatatype.XSDstring) || dType.equals(XSDDatatype.XSD)) {
			return new SimpleLiteralNode("\""+lexicalForm+"\"^^"+dType.getURI());
		} else {
			return new SimpleLiteralNode(lexicalForm+"^^"+dType.getURI());
		}
	}

	public static StarNode createSimpleLiteralNode(String lexicalForm, String langTag) {
		return new SimpleLiteralNode("\""+lexicalForm+"\"@"+langTag);
	}

	public static StarNode createSimpleLiteralNode(String lexicalForm) {
		return new SimpleLiteralNode("\""+lexicalForm+"\"");
	}

	public static StarNode createSimpleBlankNode(Node blankNode) {
		return createSimpleBlankNode(blankNode.getBlankNodeLabel());
	}
	
	public static StarNode createSimpleLiteralNodeRaw(String raw) {
		return new SimpleLiteralNode(raw);
	}
	
	private static StarNode createSimpleBlankNode(String label) {
		return new SimpleBlankNode(label);
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

	//This is only used for serializing to disk
	public static StarNode createNode(String serializedNodeString) {
		if (serializedNodeString.startsWith("B")) {
			return createSimpleBlankNode(serializedNodeString.substring(1));
		} else if(serializedNodeString.startsWith("L")) {
			return unserialize(serializedNodeString.substring(1));
		} else if(serializedNodeString.startsWith("U")) {
			return createSimpleURINode(serializedNodeString.substring(1));
		} else if (serializedNodeString.startsWith("E")) {
			return createReferenceNode(serializedNodeString.substring(1));
		} else {
			throw new IllegalArgumentException("Unknown node serialization type "+serializedNodeString);
		}
	}

	//This is to simple and will fail in some corner cases.
	private static StarNode unserialize(String string) {
		System.out.println(string);
		return createSimpleLiteralNodeRaw(string);
//		if (string.contains("^^")) {
//			String[] split = string.split("\\^\\^");
//			return createSimpleLiteralNode(removeQuotes(split[0]),XSDDatatype.(removeQuotes(split[1])));
//		} else if (string.contains("\"@")) {
//			String[] split = string.split("@");
//			return createSimpleLiteralNode(removeQuotes(split[0]),split[1]);
//		} else {
//			return createSimpleLiteralNode(removeQuotes(string));
//		}
	}
	
//	private static String removeQuotes(String string) {
//		if (string.startsWith("\"") && string.endsWith("\"")) {
//			return string.substring(1, string.length()-1);
//		} else {
//			return string;
//		}
//		
//	}
	
	//This is only used for serializing to disk
	private static StarNode createReferenceNode(String serializedNodeString) {
		String[] split = serializedNodeString.substring(2, serializedNodeString.length()-2).split(" ");
		Node subject =  createNode(split[0]);
		Node predicate =  createNode(split[1]);
		Node object =  createNode(split[2]);
		return createEmbeddedNode(subject,predicate,object);
	}
}

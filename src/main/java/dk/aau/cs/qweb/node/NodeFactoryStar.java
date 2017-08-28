package dk.aau.cs.qweb.node;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

import dk.aau.cs.qweb.dictionary.PrefixDictionary;
import dk.aau.cs.qweb.main.Config;

/**
 * Factory class for creatig StarNodes
 */
public class NodeFactoryStar extends NodeFactory {

	/**
	 * @param subject
	 * @param predicate
	 * @param object
	 * @return An embeddedNode
	 */
	public static EmbeddedNode createEmbeddedNode(Node subject, Node predicate, Node object) {
		return new EmbeddedNode(subject,predicate,object);
	}

	/**
	 * Create a simple node of approiate type.
	 * If the prefix dictionary is enabled, it will attempt to find prefixes for all URIs starting with "http" or "file:///".
	 * 
	 * @param the string representation of the URI node
	 * @return A StarNode of the .
	 */
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
	
	
	/**
	 * @param The string the represents a numeric literal (not contained in quotes)
	 * @param The XSD datatype, must be XSDDatatype.integer
	 * @return A Literal Simple Node
	 */
	public static SimpleLiteralNode createSimpleLiteralNode(String label, XSDDatatype xsdNumeric) {
		return new SimpleLiteralNode(label+"^^"+xsdNumeric.getURI());
	}

	/**
	 * @param The string the represents a literal (contained in quotes)
	 * @param The datatype
	 * @return A Literal Simple Node
	 * 
	 * If it is a string then the type will be ignored. While this does not follow the RDF standard, then it solve the problem of treating 
	 * untyped literals as string.
	 */
	public static SimpleLiteralNode createSimpleLiteralNode(String lexicalForm, RDFDatatype dType) {
		if (dType.equals(XSDDatatype.XSDstring) || dType.equals(XSDDatatype.XSD)) {
			return new SimpleLiteralNode("\""+lexicalForm+"\"^^"+dType.getURI());
		} else {
			return new SimpleLiteralNode(lexicalForm+"^^"+dType.getURI());
		}
	}

	/**
	 * @param The string the represents a literal (contained in quotes)
	 * @param The language tag, no parsing is done at this point
	 * @return A Literal Simple Node
	 */
	public static SimpleLiteralNode createSimpleLiteralNode(String lexicalForm, String langTag) {
		return new SimpleLiteralNode("\""+lexicalForm+"\"@"+langTag);
	}

	/**
	 * @param The string the represents a literal (contained in quotes)
	 * @return A Literal Simple Node
	 */
	public static SimpleLiteralNode createSimpleLiteralNode(String lexicalForm) {
		return new SimpleLiteralNode("\""+lexicalForm+"\"");
	}

	/**
	 * @param The Jena blank node object
	 * @return A Simple Blank Node
	 */
	public static SimpleBlankNode createSimpleBlankNode(Node blankNode) {
		return createSimpleBlankNode(blankNode.getBlankNodeLabel());
	}
	
	
	/**
	 * @param The string the represnts a blank node (jena is used to create this blank node identifier.)
	 * @return A Simple Blank Node
	 */
	private static SimpleBlankNode createSimpleBlankNode(String label) {
		return new SimpleBlankNode(label);
	}
	
	/**
	 * @param The URI string
	 * @return The URI string
	 */
	private static String normalizeURI(String uri) {
		if (uri.trim().startsWith("<")) {
			uri = uri.trim().substring(1);
		}
		if (uri.trim().endsWith(">")) {
			uri = uri.trim().substring(0, uri.trim().length()-1);
		}
		return uri;
	}

	/**
	 * This is only used for serializing to disk
	 * 
	 * @param A serialized node string
	 * @return A Star Node of the appopiate type.
	 */
	public static StarNode createNode(String serializedNodeString) {
		if (serializedNodeString.startsWith("B")) {
			return createSimpleBlankNode(serializedNodeString.substring(1));
		} else if(serializedNodeString.startsWith("L")) {
			return new SimpleLiteralNode(serializedNodeString.substring(1));
		} else if(serializedNodeString.startsWith("U")) {
			return createSimpleURINode(serializedNodeString.substring(1));
		} else if (serializedNodeString.startsWith("E")) {
			return createEmbeddedNode(serializedNodeString.substring(1));
		} else {
			throw new IllegalArgumentException("Unknown node serialization type "+serializedNodeString);
		}
	}

	/**
	 * This is only used for serializing to disk
	 * This method will call createNode for each element of the emberdded node
	 * 
	 * @param A serialized embedded node
	 * @return A starnode
	 */
	private static StarNode createEmbeddedNode(String serializedNodeString) {
		String[] split = serializedNodeString.substring(2, serializedNodeString.length()-2).split(" ");
		Node subject =  createNode(split[0]);
		Node predicate =  createNode(split[1]);
		Node object =  createNode(split[2]);
		return createEmbeddedNode(subject,predicate,object);
	}
}

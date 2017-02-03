package dk.aau.cs.qweb.dictionary;

import java.util.HashMap;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;

import dk.aau.cs.qweb.helper.BitHelper;
import dk.aau.cs.qweb.model.NodeFactoryStar;
import dk.aau.cs.qweb.model.Node_Triple;
import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triple.KeyFactory;
import dk.aau.cs.qweb.triple.TriplePattern;
import dk.aau.cs.qweb.triple.TriplePatternBuilder;
import dk.aau.cs.qweb.triple.TripleStar;

public class MyDictionary {
	
	private static MyDictionary instance;
	HashMap<Key, Node> id2Node;
	HashMap<Node, Key> node2Id;
	long id;

	private MyDictionary() {
		id2Node = new HashMap<Key, Node>();
		node2Id = new HashMap<Node, Key>();
		id = 1;
	}
	
	public static MyDictionary getInstance() {
		if(instance == null) {
	         instance = new MyDictionary();
	    }
	    return instance;
	}

	// This methods seem over complicated but it is in order to be able to handle triple patterns
	public TriplePattern createTriplePattern(Triple t) {
		TriplePatternBuilder builder = new TriplePatternBuilder();
		
		if (t.getSubject().isConcrete()) {
			if (t.getSubject() instanceof Node_Triple) {
				builder.setSubject(convertEmbeddedTriplePatternNode(t.getSubject()));
			} else {
				builder.setSubject(getKey(t.getSubject()));
			}
		}
		
		if (t.getPredicate().isConcrete()) {
			builder.setPredicate(getKey(t.getPredicate()));
		}
		
		if (t.getObject().isConcrete()) {
			if (t.getObject() instanceof Node_Triple) {
				builder.setObject(convertEmbeddedTriplePatternNode(t.getObject()));
			} else {
				builder.setObject(getKey(t.getObject()));
			}
		}
		return builder.createTriplePatter();
	}

	private TriplePattern convertEmbeddedTriplePatternNode(Node embeddedNode) {
		Node_Triple embedded = (Node_Triple) embeddedNode ;
		Node subjectNode = embedded.getSubject();
		Node predicateNode = embedded.getPredicate();
		Node objectNode = embedded.getObject();
		TriplePatternBuilder builder = new TriplePatternBuilder();
		
		if (subjectNode.isConcrete()) {
			builder.setSubject(getKey(subjectNode));
		} 
		
		if (predicateNode.isConcrete()) {
			builder.setPredicate(getKey(predicateNode));
		} 
		
		if (objectNode.isConcrete()) {
			builder.setObject(getKey(objectNode));
		} 
		return builder.createTriplePatter();
	}

	private Key getKey(Node node) { //Only used in triple patterns 
		if (node2Id.containsKey(node)) {
			return node2Id.get(node);
		} else {
			return  new Key(0); //meaning that a resource does not exist in dict, key 0 is an error code.
		}
	}


	public TripleStar createTriple(Triple t) {
		Key subject = nodeToKey(t.getSubject());
		Key predicate = nodeToKey(t.getPredicate());
		Key object = nodeToKey(t.getObject());
				
		return new TripleStar(subject,predicate,object);
	}

	private Key nodeToKey(Node node) {
		if (node instanceof Node_Triple) {
			Node_Triple embeddedNode = (Node_Triple) node;
			Key s1 = registerOrGetNode(embeddedNode.getSubject());
			Key p1 = registerOrGetNode(embeddedNode.getPredicate());
			Key o1 = registerOrGetNode(embeddedNode.getObject());
			
			return KeyFactory.createKey(s1, p1, o1);
		} else {
			return registerOrGetNode(node);
		}
	}

	private Key registerOrGetNode(Node node) {
		if (node2Id.containsKey(node)) {
			return node2Id.get(node);
		} else {
			return registerNode(node);
		}
	}

	public Node getNode(Key id) {
		if (BitHelper.isIdAnEmbeddedTriple(id)) {
			Key subject = KeyFactory.createKey(BitHelper.getEmbeddedSubject(id));
			Key predicate = KeyFactory.createKey(BitHelper.getEmbeddedPredicate(id));
			Key object = KeyFactory.createKey(BitHelper.getEmbeddedObject(id));
			return NodeFactoryStar.createEmbeddedNode(id2Node.get(subject), id2Node.get(predicate), id2Node.get(object));
		} else {
			return id2Node.get(id);
		}
	}
	
	private Key registerNode(Node node) {
		Key tempId;
		Key key = new Key(id);
		id2Node.put(key, node);
		node2Id.put(node,key);
		tempId = key;
		id++;
		return tempId;
	}
	
	public int size() {
		return id2Node.size();
	}
	

}

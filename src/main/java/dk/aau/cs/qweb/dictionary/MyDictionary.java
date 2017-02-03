package dk.aau.cs.qweb.dictionary;

import java.util.HashMap;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;

import dk.aau.cs.qweb.model.Node_Triple;
import dk.aau.cs.qweb.triple.TripleStar;
import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triple.KeyFactory;
import dk.aau.cs.qweb.triple.TriplePatternBuilder;
import dk.aau.cs.qweb.triple.TriplePattern;

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
		
		if (subjectNode.isConcrete()) {
			builder.setPredicate(getKey(predicateNode));
		} 
		
		if (subjectNode.isConcrete()) {
			builder.setObject(getKey(objectNode));
		} 
		
		return builder.createTriplePatter();
	}

	private Key getKey(Node node) {
		if (node2Id.containsKey(node)) {
			return node2Id.get(node);
		} else {
			return  new Key(0);
		}
	}


	public TripleStar createTriple(Triple t) {
		Key subject;
		Key predicate;
		Key object;
		
		if (node2Id.containsKey(t.getSubject())) {
			subject = node2Id.get(t.getSubject());
		} else {
			subject = registerNode(t.getSubject());
		}
	
		if (node2Id.containsKey(t.getPredicate())) {
			predicate = node2Id.get(t.getPredicate());
		} else {
			predicate = registerNode(t.getPredicate());
		}
	
		if (node2Id.containsKey(t.getObject())) {
			object = node2Id.get(t.getObject());
		} else {
			object = registerNode(t.getObject());
		}
		
		return new TripleStar(subject,predicate,object);
	}


	public Node getNode(Key id) {
		return id2Node.get(id);
	}
	
	private Key registerNode(Node node){
		Key tempId;
		if (node instanceof Node_Triple) {
			tempId = registerEmbeddedKey(node);
		} else {
			tempId = registerKey(node);
		}
		return tempId;
	}

	private Key registerEmbeddedKey(Node node) {
		Node_Triple embeddedNode = (Node_Triple) node;
		
		Key subject = registerKey(embeddedNode.getSubject());
		Key predicate = registerKey(embeddedNode.getPredicate());
		Key object = registerKey(embeddedNode.getObject());
		
		Key embeddedTriple = KeyFactory.createKey(subject.getId(), predicate.getId(), object.getId());
//		id2Node.put(embeddedTriple, node);
//		node2Id.put(node,embeddedTriple);
		return embeddedTriple;
	}

	private Key registerKey(Node node) {
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

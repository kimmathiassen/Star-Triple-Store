package dk.aau.cs.qweb.dictionary;

import java.util.HashMap;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;

import dk.aau.cs.qweb.model.Node_Triple;
import dk.aau.cs.qweb.triple.TripleStar;
import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triple.KeyFactory;
import dk.aau.cs.qweb.triple.TripleBuilder;

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
	public TripleStar createTriple(Triple t) {
		TripleBuilder builder = new TripleBuilder();
		
		if (t.getSubject().isConcrete()) {
			if (node2Id.containsKey(t.getSubject())) {
				builder.setSubject(node2Id.get(t.getSubject()));
			} else {
				builder.setSubject(registerNode(t.getSubject()));
			}
		}
		
		if (t.getPredicate().isConcrete()) {
			if (node2Id.containsKey(t.getPredicate())) {
				builder.setPredicate(node2Id.get(t.getPredicate()));
			} else {
				builder.setPredicate(registerNode(t.getPredicate()));
			}
		}
		
		if (t.getObject().isConcrete()) {
			if (node2Id.containsKey(t.getObject())) {
				builder.setObject(node2Id.get(t.getObject()));
			} else {
				builder.setObject(registerNode(t.getObject()));
			}
		}
		
		return builder.createTriple();
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
		KeyFactory kf = new KeyFactory();
		
		Key subject = registerKey(embeddedNode.getSubject());
		Key predicate = registerKey(embeddedNode.getPredicate());
		Key object = registerKey(embeddedNode.getObject());
		
		Key embeddedTriple = kf.createKey(subject.getId(), predicate.getId(), object.getId());
		id2Node.put(embeddedTriple, node);
		node2Id.put(node,embeddedTriple);
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

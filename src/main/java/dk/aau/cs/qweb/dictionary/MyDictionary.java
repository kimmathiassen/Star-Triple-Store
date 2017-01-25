package dk.aau.cs.qweb.dictionary;

import java.util.HashMap;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;

import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triple.IdTriple;
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
	public IdTriple createTriple(Triple t) {
		TripleBuilder builder = new TripleBuilder();
		
		if (t.getSubject().isConcrete()) {
			builder.setSubject(convertNodeToId(t.getSubject()));
		} else {
			builder.setSubjectIsVariable(t.getSubject());
		}
		
		if (t.getPredicate().isConcrete()) {
			builder.setPredicate(convertNodeToId(t.getPredicate()));
		} else {
			builder.setPredicateIsVariable(t.getPredicate());
		}
		
		if (t.getObject().isConcrete()) {
			builder.setObject(convertNodeToId(t.getObject()));
		} else {
			builder.setObjectIsVariable(t.getObject());
		}
		
		return builder.createTriple();
	}



	public Node getNode(Key id) {
		return id2Node.get(id);
	}
	
	private Key convertNodeToId(Node node){
		Key tempId;
		if (node2Id.containsKey(node)) {
			tempId = node2Id.get(node);
		} else {
			Key key = new Key(id);
			id2Node.put(key, node);
			node2Id.put(node,key);
			tempId = key;
			id++;
		}
		return tempId;
	}
	
	public long size() {
		return id;
	}
	

}

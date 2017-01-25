package dk.aau.cs.qweb.dictionary;

import java.util.HashMap;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;

import dk.aau.cs.qweb.triple.MyTriple;
import dk.aau.cs.qweb.triple.TripleBuilder;

public class MyDictionary {
	
	private static MyDictionary instance;
	HashMap<Integer, Node> id2Node;
	HashMap<Node, Integer> node2Id;
	int key;

	private MyDictionary() {
		id2Node = new HashMap<Integer, Node>();
		node2Id = new HashMap<Node, Integer>();
		//It is important that it starts as non zero, 
		//0 is used a an error key in the TripleBuilder class
		key = 1; 
	}
	
	public static MyDictionary getInstance() {
		if(instance == null) {
	         instance = new MyDictionary();
	    }
	    return instance;
	}

	// This methods seem over complicated but it is in order to be able to handle triple patterns
	public MyTriple createTriple(Triple t) {
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



	public Node getNode(int id) {
		return id2Node.get(id);
	}
	
	private int convertNodeToId(Node node){
		int id;
		if (node2Id.containsKey(node)) {
			id = node2Id.get(node);
		} else {
			id2Node.put(key, node);
			node2Id.put(node,key);
			id = key;
			key++;
		}
		return id;
	}
	
	public int size() {
		return key;
	}
	

}

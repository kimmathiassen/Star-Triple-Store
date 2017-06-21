package dk.aau.cs.qweb.dictionary;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.jena.graph.Node;
import org.apache.jena.reasoner.IllegalParameterException;

import dk.aau.cs.qweb.helper.BitHelper;
import dk.aau.cs.qweb.node.NodeFactoryStar;
import dk.aau.cs.qweb.node.Node_Triple;
import dk.aau.cs.qweb.node.SimpleNode;
import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triple.KeyFactory;

public class NodeDictionary {
	
	private static NodeDictionary instance;
	HashMap<Key, Node> id2Node;
	HashMap<Node, Key> node2Id;
	HashMap<Key,Node_Triple > id2ReferenceTriple;
	HashMap<Node_Triple, Key> referenceTriple2Id;
	private boolean isThereAnySpecialReferenceTripleDistributionConditions;
	private int referenceTripleDistributionPercentage;
	private int numberOfEmbeddedTriples;

	private NodeDictionary() {
		id2Node = new HashMap<Key, Node>();
		node2Id = new HashMap<Node, Key>();
		id2ReferenceTriple = new HashMap<Key, Node_Triple>();
		referenceTriple2Id = new HashMap<Node_Triple, Key>();
		numberOfEmbeddedTriples = 0;
	}
	
	public static NodeDictionary getInstance() {
		if(instance == null) {
	         instance = new NodeDictionary();
	    }
	    return instance;
	}
	
	public int size() {
		return id2Node.size()+id2ReferenceTriple.size();
	}
	
	public int getNumberOfEmbeddedTriples() {
		return numberOfEmbeddedTriples;
	}
	
	private Key nodeToKey(Node node) {
		if (node instanceof Node_Triple) {
			Node_Triple embeddedNode = (Node_Triple) node;
			
			Key s1 = nodeToKey(normalizeNode(embeddedNode.getSubject())); 
			Key p1 = nodeToKey(normalizeNode(embeddedNode.getPredicate()));
			Key o1 = nodeToKey(normalizeNode(embeddedNode.getObject()));
			
			return registerOrGetEmbeddedNode(s1,p1,o1,node);
		} else if (node instanceof SimpleNode) {
			return registerOrGetNode((SimpleNode)node);
		} else {
			throw new IllegalArgumentException("The type of "+node.getClass().getSimpleName()+" is not a instance of SimpleNode or Node_triple. Node.toString() "+node);
		}
	}

	private Key registerOrGetEmbeddedNode(Key subject, Key predicate, Key object, Node node) {
		if (subject.getId() > BitHelper.getLargest20BitNumber() || 
				predicate.getId() > BitHelper.getLargest20BitNumber() ||
				object.getId() > BitHelper.getLargest20BitNumber()) {
			if (referenceTriple2Id.containsKey(node)) {
				return referenceTriple2Id.get(node);
			}  else {
				return registerEmbeddedNode(subject, predicate, object,node);
			}
		} else if (isThereAnySpecialReferenceTripleDistributionConditions) {
			float currentDistribtuion = (float)id2ReferenceTriple.size()/((float)numberOfEmbeddedTriples+1)*(float)100;
			if (referenceTriple2Id.containsKey(node)) {
				return referenceTriple2Id.get(node);
			} else if(currentDistribtuion < referenceTripleDistributionPercentage) {
				Key key = KeyFactory.createReferenceTriple();
				addReferenceTriple(node, key);
				return key;
			} else {
				return registerEmbeddedNode(subject, predicate, object,node);
			}
		} else {
			if (node2Id.containsKey(node)) {
				
				return node2Id.get(node);
			} else {
				return registerEmbeddedNode(subject, predicate, object,node);
			}
		}
	}

	private Key registerEmbeddedNode(Key subject, Key predicate, Key object,Node node) {
		final Key key = KeyFactory.createKey(subject, predicate, object);
		if (BitHelper.isReferenceBitSet(key)) {
			addReferenceTriple(node, key);
		
		} else {
			addNode(node, key);
		}
		return key;
	}

	private void addReferenceTriple(Node node, final Key key) {
		numberOfEmbeddedTriples++;
		id2ReferenceTriple.put(key, (Node_Triple)node);
		referenceTriple2Id.put((Node_Triple)node,key);
	}

	private void addNode(Node node, final Key key) {
		if (node instanceof Node_Triple) {
			numberOfEmbeddedTriples++;
		}
		id2Node.put(key, node);
		node2Id.put(node,key);
	}

	private Node normalizeNode(Node node) {
		if (node.toString().trim().startsWith("<") && node.toString().trim().endsWith(">")) {
			return NodeFactoryStar.createSimpleURINode(node.toString().trim().substring(1, node.toString().trim().length()-1));
		} 
		return node;
	}

	private Key registerOrGetNode(SimpleNode node) {
		if (node2Id.containsKey(node)) {
			return node2Id.get(node);
		} else {
			return registerNode(node);
		}
	}

	public Node getNode(Key id) {
		if (BitHelper.isReferenceBitSet(id)) {
			return id2ReferenceTriple.get(id);
		} else {
			return id2Node.get(id);
		}
	}
	
	public Key getReferernceTripleKey(Key subjectId, Key predicateId, Key objectId) {
		Node subject = getNode(subjectId);
		Node predicate = getNode(predicateId);
		Node object = getNode(objectId);
		Node referenceTriple =  NodeFactoryStar.createEmbeddedNode(subject, predicate, object);
		
		return referenceTriple2Id.get(referenceTriple);
	}
	
	private Key registerNode(SimpleNode node) {
		final Key key = KeyFactory.createKey(node);
		id2Node.put(key, node);
		node2Id.put(node,key);
		return key;
	}
	
	public Key createKey(Node node) {
		return nodeToKey(node);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Node Dict\n");
		
		for (Entry<Key, Node> iterable_element : id2Node.entrySet()) {
			sb.append(iterable_element.getKey()+": "+ iterable_element.getValue()+"\n");
		}
		
		sb.append("\nReference Triple Dict\n");
		for (Entry<Key, Node_Triple> iterable_element : id2ReferenceTriple.entrySet()) {
			sb.append(iterable_element.getKey()+": "+ iterable_element.getValue()+"\n");
		}
		return sb.toString();
	}

	public void setReferenceTripleDistribution(int i) {
		if (node2Id.size() != 0) {
			throw new IllegalStateException("overflow distribution but only be set in an empty dictionary.");
		}
		if (i < 0 && i > 100) {
			throw new IllegalParameterException("overflow distrubtion is a percentage number, it must be between [0 and 100]");
		}
		if (i != 0) {
			isThereAnySpecialReferenceTripleDistributionConditions = true;
			referenceTripleDistributionPercentage = i;
		}
	}

	public void clear() {
		id2Node.clear();
		node2Id.clear();
		referenceTriple2Id.clear();
		id2ReferenceTriple.clear();
		KeyFactory.reset();
		numberOfEmbeddedTriples = 0;
		isThereAnySpecialReferenceTripleDistributionConditions = false;
		setReferenceTripleDistribution(0);
	}

	public int getNumberOfReferenceTriples() {
		return id2ReferenceTriple.size();
	}
}



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
	HashMap<Key, Node> id2SimpleNode;
	HashMap<Node, Key> simpleNode2Id;
	HashMap<Key,Node_Triple > id2ReferenceTriple;
	HashMap<Node_Triple, Key> referenceTriple2Id;
	private boolean isThereAnySpecialReferenceTripleDistributionConditions;
	private int referenceTripleDistributionPercentage;
	int numberOfEmbeddedTriples;

	private NodeDictionary() {
		id2SimpleNode = new HashMap<Key, Node>();
		simpleNode2Id = new HashMap<Node, Key>();
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
		return id2SimpleNode.size()+id2ReferenceTriple.size();
	}
	
	public int getNumberOfEmbeddedTriples() {
		return numberOfEmbeddedTriples;
	}
	
	private Key nodeToKey(Node node) {
		if (node instanceof Node_Triple) {
			Node_Triple embeddedNode = (Node_Triple) node;
			if (newEmbeddedTriple(embeddedNode)) {
				numberOfEmbeddedTriples++;
			}
			
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
		} else if (isThereAnySpecialReferenceTripleDistributionConditions && numberOfEmbeddedTriples != 0) {
			float currentDistribtuion = (float)id2ReferenceTriple.size()/(float)numberOfEmbeddedTriples*(float)100;
			if (referenceTriple2Id.containsKey(node)) {
				return referenceTriple2Id.get(node);
			} else if(currentDistribtuion <= referenceTripleDistributionPercentage) {
				Key key = KeyFactory.createReferenceTriple();
				addReferenceTriple(node, key);
				return key;
			} else {
				return registerEmbeddedNode(subject, predicate, object,node);
			}
		} else {
			if (simpleNode2Id.containsKey(node)) {
				return simpleNode2Id.get(node);
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
			addSimpleNode(node, key);
		}
		return key;
	}

	private void addReferenceTriple(Node node, final Key key) {
		id2ReferenceTriple.put(key, (Node_Triple)node);
		referenceTriple2Id.put((Node_Triple)node,key);
	}

	private void addSimpleNode(Node node, final Key key) {
		id2SimpleNode.put(key, node);
		simpleNode2Id.put(node,key);
	}

	private Node normalizeNode(Node node) {
		if (node.toString().trim().startsWith("<") && node.toString().trim().endsWith(">")) {
			return NodeFactoryStar.createSimpleURINode(node.toString().trim().substring(1, node.toString().trim().length()-1));
		} 
		return node;
	}

	private boolean newEmbeddedTriple(Node_Triple embeddedNode) {
		boolean subject = !simpleNode2Id.containsKey(embeddedNode.getSubject());
		boolean predicate = !simpleNode2Id.containsKey(embeddedNode.getPredicate());
		boolean object = !simpleNode2Id.containsKey(embeddedNode.getObject());
		return (subject || predicate || object);
	}

	private Key registerOrGetNode(SimpleNode node) {
		if (simpleNode2Id.containsKey(node)) {
			return simpleNode2Id.get(node);
		} else {
			return registerNode(node);
		}
	}

	public Node getNode(Key id) {
		if (BitHelper.isIdAnEmbeddedTriple(id)) {
			if (BitHelper.isReferenceBitSet(id)) {
				return id2ReferenceTriple.get(id);
			} else {
				Key subject = KeyFactory.createKey(BitHelper.getEmbeddedSubject(id));
				Key predicate = KeyFactory.createKey(BitHelper.getEmbeddedPredicate(id));
				Key object = KeyFactory.createKey(BitHelper.getEmbeddedObject(id));
				return NodeFactoryStar.createEmbeddedNode(id2SimpleNode.get(subject), id2SimpleNode.get(predicate), id2SimpleNode.get(object));
			}
		} else {
			return id2SimpleNode.get(id);
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
		id2SimpleNode.put(key, node);
		simpleNode2Id.put(node,key);
		return key;
	}
	
	public Key createKey(Node node) {
		return nodeToKey(node);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Node Dict\n");
		
		for (Entry<Key, Node> iterable_element : id2SimpleNode.entrySet()) {
			sb.append(iterable_element.getKey()+": "+ iterable_element.getValue()+"\n");
		}
		
		sb.append("\nReference Triple Dict\n");
		for (Entry<Key, Node_Triple> iterable_element : id2ReferenceTriple.entrySet()) {
			sb.append(iterable_element.getKey()+": "+ iterable_element.getValue()+"\n");
		}
		return sb.toString();
	}

	public void setReferenceTripleDistribution(int i) {
		if (simpleNode2Id.size() != 0) {
			throw new IllegalStateException("overflow distribution but only be set in an empty dictionary.");
		}
		if (i < 0 && i > 100) {
			throw new IllegalParameterException("overflow distrubtion is a percentage number, it must be between [0 and 100]");
		}
		isThereAnySpecialReferenceTripleDistributionConditions = true;
		referenceTripleDistributionPercentage = i;
	}

	public void clear() {
		id2SimpleNode.clear();
		simpleNode2Id.clear();
		referenceTriple2Id.clear();
		id2ReferenceTriple.clear();
		KeyFactory.reset();
		numberOfEmbeddedTriples = 0;
	}

	public int getNumberOfReferenceTriples() {
		return id2ReferenceTriple.size();
	}
}



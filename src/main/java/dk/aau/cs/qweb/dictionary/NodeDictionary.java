package dk.aau.cs.qweb.dictionary;

import java.util.Map.Entry;

import org.apache.jena.graph.Node;
import org.apache.jena.reasoner.IllegalParameterException;

import com.google.common.collect.HashBiMap;

import dk.aau.cs.qweb.helper.BitHelper;
import dk.aau.cs.qweb.main.Config;
import dk.aau.cs.qweb.node.NodeFactoryStar;
import dk.aau.cs.qweb.node.Node_Triple;
import dk.aau.cs.qweb.node.SimpleNode;
import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triple.KeyFactory;

public class NodeDictionary {
	
	private static NodeDictionary instance;
	HashBiMap<Key,Node> nodeDictionary;
	HashBiMap<Key,Node> referenceNodeDictionary;
	private boolean isThereAnySpecialReferenceTripleDistributionConditions;
	private int referenceTripleDistributionPercentage;
	private int numberOfEmbeddedTriples;

	public boolean isThereAnySpecialReferenceTripleDistributionConditions() {
		return isThereAnySpecialReferenceTripleDistributionConditions ;
	}
	
	private boolean shouldNextNodeBeAnReference() {
		float currentDistribtuion = (float)referenceNodeDictionary.size()/((float)numberOfEmbeddedTriples+1)*(float)100;
		return (currentDistribtuion < referenceTripleDistributionPercentage);
	}
	
	private NodeDictionary() {
		
		nodeDictionary = HashBiMap.create(Config.getNodeDictionaryInitialSize());
		referenceNodeDictionary = HashBiMap.create(Config.getReferenceNodeDictionaryInitialSize());
		numberOfEmbeddedTriples = 0;
	}
	
	public static NodeDictionary getInstance() {
		if(instance == null) {
	         instance = new NodeDictionary();
	    }
	    return instance;
	}
	
	public int size() {
		return nodeDictionary.size()+referenceNodeDictionary.size();
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
		if (subject.getId() > Config.getLargestSubjectId() || 
				predicate.getId() > Config.getLargestSubjectId() ||
				object.getId() > Config.getLargestSubjectId()) {
			if (referenceNodeDictionary.containsValue(node)) {
				return referenceNodeDictionary.inverse().get(node);
			}  else {
				return registerEmbeddedNode(subject, predicate, object,node);
			}
		} else if (isThereAnySpecialReferenceTripleDistributionConditions()) {
			if (referenceNodeDictionary.containsValue(node)) {
				return referenceNodeDictionary.inverse().get(node);
			} else if (shouldNextNodeBeAnReference()){
				Key key = KeyFactory.createReferenceTriple();
				addReferenceTriple(node, key);
				return key;
			} else {
				return registerEmbeddedNode(subject, predicate, object,node);
			}
		} else {
			if (nodeDictionary.containsValue(node)) {
				return nodeDictionary.inverse().get(node);
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
		referenceNodeDictionary.put(key, node);
	}

	private void addNode(Node node, final Key key) {
		if (node instanceof Node_Triple) {
			numberOfEmbeddedTriples++;
		}
		nodeDictionary.put(key, node);
	}

	private Node normalizeNode(Node node) {
		if (node.toString().trim().startsWith("<") && node.toString().trim().endsWith(">")) {
			return NodeFactoryStar.createSimpleURINode(node.toString().trim().substring(1, node.toString().trim().length()-1));
		} 
		return node;
	}

	private Key registerOrGetNode(SimpleNode node) {
		if (nodeDictionary.containsValue(node)) {
			return nodeDictionary.inverse().get(node);
//		} if (isThereAnySpecialReferenceTripleDistributionConditions()) {
//			if (referenceNode2Id.containsKey(node)) {
//				return referenceNode2Id.get(node);
//			} else if (shouldNextNodeBeAnReference()){
//				Key key = KeyFactory.createReferenceTriple();
//				addReferenceTriple(node, key);
//				return key;
//			} else {
//				return registerNode(node);
//			}
		} else {
			return registerNode(node);
		}
	}

	public Node getNode(Key id) {
		if (BitHelper.isReferenceBitSet(id)) {
			return referenceNodeDictionary.get(id);
		} else {
			return nodeDictionary.get(id);
		}
	}
	
	public Key getReferernceTripleKey(Key subjectId, Key predicateId, Key objectId) {
		Node subject = getNode(subjectId);
		Node predicate = getNode(predicateId);
		Node object = getNode(objectId);
		Node referenceTriple =  NodeFactoryStar.createEmbeddedNode(subject, predicate, object);
		
		return referenceNodeDictionary.inverse().get(referenceTriple);
	}
	
	private Key registerNode(SimpleNode node) {
		final Key key = KeyFactory.createKey(node);
		nodeDictionary.put(key, node);
		return key;
	}
	
	public Key createKey(Node node) {
		return nodeToKey(node);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Node Dict\n");
		
		for (Entry<Key, Node> iterable_element : nodeDictionary.entrySet()) {
			sb.append(iterable_element.getKey()+": "+ iterable_element.getValue()+"\n");
		}
		
		sb.append("\nReference Triple Dict\n");
		for (Entry<Key, Node> iterable_element : referenceNodeDictionary.entrySet()) {
			sb.append(iterable_element.getKey()+": "+ iterable_element.getValue()+"\n");
		}
		return sb.toString();
	}

	public void setReferenceTripleDistribution(int i) {
		if (nodeDictionary.size() != 0) {
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
		nodeDictionary.clear();
		referenceNodeDictionary.clear();
		KeyFactory.reset();
		numberOfEmbeddedTriples = 0;
		isThereAnySpecialReferenceTripleDistributionConditions = false;
		setReferenceTripleDistribution(0);
	}

	public int getNumberOfReferenceTriples() {
		return referenceNodeDictionary.size();
	}

	public boolean containsReferernceTripleKey(Key subjectId, Key predicateId, Key objectId) {
		Node subject = getNode(subjectId);
		Node predicate = getNode(predicateId);
		Node object = getNode(objectId);
		Node referenceTriple =  NodeFactoryStar.createEmbeddedNode(subject, predicate, object);
		
		return referenceNodeDictionary.containsValue(referenceTriple);
	}
}

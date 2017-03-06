package dk.aau.cs.qweb.dictionary;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.jena.graph.Node;
import org.apache.jena.reasoner.IllegalParameterException;

import dk.aau.cs.qweb.helper.BitHelper;
import dk.aau.cs.qweb.model.NodeFactoryStar;
import dk.aau.cs.qweb.model.Node_Triple;
import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triple.KeyFactory;

public class NodeDictionary {
	
	private static NodeDictionary instance;
	HashMap<Key, Node> id2Node;
	HashMap<Node, Key> node2Id;
	HashMap<Key,Node_Triple > id2Overflow;
	HashMap<Node_Triple, Key> overflow2Id;
	long overflowId;
	long id;
	private boolean isThereAnySpecialOverflowDistributionConditions;
	private int overflowDistributionPercentage;
	int numberOfEmbeddedTriples;

	private NodeDictionary() {
		id2Node = new HashMap<Key, Node>();
		node2Id = new HashMap<Node, Key>();
		id2Overflow = new HashMap<Key, Node_Triple>();
		overflow2Id = new HashMap<Node_Triple, Key>();
		id = 1;
		overflowId = 1;
		numberOfEmbeddedTriples = 0;
	}
	
	public static NodeDictionary getInstance() {
		if(instance == null) {
	         instance = new NodeDictionary();
	    }
	    return instance;
	}
	
	public int getNumberOfEmbeddedTriples() {
		return numberOfEmbeddedTriples;
	}
	
	// This methods seem over complicated but it is in order to be able to handle triple patterns
//	public TripleStarPattern createTriplePattern(Triple t) {
//		StarNode subject = nodeToKey(t.getSubject());
//		StarNode predicate = nodeToKey(t.getPredicate());
//		StarNode object = nodeToKey(t.getObject());
//		
//		return new TripleStarPattern(subject, predicate, object);
//	}
//	

//	private TripleStarPattern convertEmbeddedTriplePatternNode(Node embeddedNode) {
//		Node_Triple embedded = (Node_Triple) embeddedNode ;
//		Node subjectNode = embedded.getSubject();
//		Node predicateNode = embedded.getPredicate();
//		Node objectNode = embedded.getObject();
//		TriplePatternBuilder builder = new TriplePatternBuilder();
//		
//		if (subjectNode.isConcrete()) {
//			builder.setSubject(lookupKeyOrCreateNew(subjectNode));
//		} 
//		
//		if (predicateNode.isConcrete()) {
//			builder.setPredicate(lookupKeyOrCreateNew(predicateNode));
//		} 
//		
//		if (objectNode.isConcrete()) {
//			builder.setObject(lookupKeyOrCreateNew(objectNode));
//		} 
//		return builder.createTriplePatter();
//	}

//	private Key lookupKeyOrCreateNew(Node node) { //Only used in triple patterns 
//		if (node2Id.containsKey(node)) {
//			return node2Id.get(node);
//		} else {
//			return  new Key(0); //meaning that a resource does not exist in dict, key 0 is an error code.
//		}
//	}

	

	private Key nodeToKey(Node node) {
		if (node instanceof Node_Triple) {
			Node_Triple embeddedNode = (Node_Triple) node;
			if (newEmbeddedTriple(embeddedNode)) {
				numberOfEmbeddedTriples++;
			}
			
			if (doesNodeContainOverflowKey(embeddedNode)) {
				registerOrGetNode(embeddedNode.getSubject());
				registerOrGetNode(embeddedNode.getPredicate());
				registerOrGetNode(embeddedNode.getObject());
				return registerOverflowNode(embeddedNode);
			} else {
				Key s1 = registerOrGetNode(embeddedNode.getSubject());
				Key p1 = registerOrGetNode(embeddedNode.getPredicate());
				Key o1 = registerOrGetNode(embeddedNode.getObject());
				return KeyFactory.createKey(s1, p1, o1);
			}
		} else {
			return registerOrGetNode(node);
		}
	}

	private boolean newEmbeddedTriple(Node_Triple embeddedNode) {
		boolean subject = !node2Id.containsKey(embeddedNode.getSubject());
		boolean predicate = !node2Id.containsKey(embeddedNode.getPredicate());
		boolean object = !node2Id.containsKey(embeddedNode.getObject());
		return (subject || predicate || object);
	}

	private boolean doesNodeContainOverflowKey(Node_Triple embeddedNode) {
		if (isThereAnySpecialOverflowDistributionConditions) {
			if (numberOfEmbeddedTriples != 0) {
				System.out.println(id2Overflow.size());
				float currentDistribtuion = (float)id2Overflow.size()/(float)numberOfEmbeddedTriples*100;
				System.out.println(currentDistribtuion);
				if (currentDistribtuion < overflowDistributionPercentage) {
					System.out.println("count as overflow");
				} else {
					System.out.println("count as normal embedded");
				}
				
				return currentDistribtuion < overflowDistributionPercentage ? true : false;
			}
			return false;
		} else {
			return id + 3 > KeyFactory.getOverflowLimit() ? true : false; 
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
			if (BitHelper.isOverflownEmbeddedTriple(id)) {
				return id2Overflow.get(id);
			} else {
				Key subject = KeyFactory.createKey(BitHelper.getEmbeddedSubject(id));
				Key predicate = KeyFactory.createKey(BitHelper.getEmbeddedPredicate(id));
				Key object = KeyFactory.createKey(BitHelper.getEmbeddedObject(id));
				return NodeFactoryStar.createEmbeddedNode(id2Node.get(subject), id2Node.get(predicate), id2Node.get(object));
			}
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
	
	private Key registerOverflowNode(Node_Triple triple) {
		Key tempId;
		Key key = BitHelper.createOverflowKey(overflowId);
		id2Overflow.put(key, triple);
		overflow2Id.put(triple,key);
		tempId = key;
		overflowId++;
		return tempId;
	}
	
	public Key createKey(Node node) {
		return nodeToKey(node);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Entry<Key, Node> iterable_element : id2Node.entrySet()) {
			sb.append(iterable_element.getKey()+": "+ iterable_element.getValue()+"\n");
		}
		return sb.toString();
	}

	public void setOverflowDistribution(int i) {
		if (node2Id.size() != 0) {
			throw new IllegalStateException("overflow distribution but only be set in an empty dictionary.");
		}
		if (i < 0 && i > 100) {
			throw new IllegalParameterException("overflow distrubtion is a percentage number, it must be between [0 and 100]");
		}
		isThereAnySpecialOverflowDistributionConditions = true;
		overflowDistributionPercentage = i;
	}

	public void clear() {
		id2Node.clear();
		node2Id.clear();
		overflow2Id.clear();
		id2Overflow.clear();
		id = 1;
		overflowId = 1;
		numberOfEmbeddedTriples = 0;
	}

	public int getNumberOfOverflowNodes() {
		return id2Overflow.size();
	}
}

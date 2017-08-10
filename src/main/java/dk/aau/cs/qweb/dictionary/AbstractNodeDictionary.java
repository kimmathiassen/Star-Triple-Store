package dk.aau.cs.qweb.dictionary;

import org.apache.jena.graph.Node;
import org.apache.jena.reasoner.IllegalParameterException;

import dk.aau.cs.qweb.helper.BitHelper;
import dk.aau.cs.qweb.main.Config;
import dk.aau.cs.qweb.node.NodeFactoryStar;
import dk.aau.cs.qweb.node.EmbeddedNode;
import dk.aau.cs.qweb.node.SimpleNode;
import dk.aau.cs.qweb.node.StarNode;
import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triple.KeyFactory;

public abstract class AbstractNodeDictionary implements NodeDictionary {
	protected boolean isThereAnySpecialReferenceTripleDistributionConditions;
	protected int referenceTripleDistributionPercentage;
	protected int numberOfEmbeddedTriples;

	@Override
	public boolean isThereAnySpecialReferenceTripleDistributionConditions() {
		return isThereAnySpecialReferenceTripleDistributionConditions ;
	}
	
	@Override
	public int getNumberOfEmbeddedTriples() {
		return numberOfEmbeddedTriples;
	}
	
	protected Key nodeToKey(Node node) {
		if (node instanceof EmbeddedNode) {
			EmbeddedNode embeddedNode = (EmbeddedNode) node;
			
			Key s1 = nodeToKey(normalizeNode(embeddedNode.getSubject())); 
			Key p1 = nodeToKey(normalizeNode(embeddedNode.getPredicate()));
			Key o1 = nodeToKey(normalizeNode(embeddedNode.getObject()));
			
			return registerOrGetEmbeddedNode(s1,p1,o1,embeddedNode);
		} else if (node instanceof SimpleNode) {
			return registerOrGetNode((SimpleNode)node);
		} else {
			throw new IllegalArgumentException("The type of "+node.getClass().getSimpleName()+" is not a instance of SimpleNode or Node_triple. Node.toString() "+node);
		}
	}
	
	protected Node normalizeNode(Node node) {
		if (node.toString().trim().startsWith("<") && node.toString().trim().endsWith(">")) {
			return NodeFactoryStar.createSimpleURINode(node.toString().trim().substring(1, node.toString().trim().length()-1));
		} 
		return node;
	}
	
	protected Key registerReferenceNode(Key subject, Key predicate, Key object,StarNode node) {
		final Key key = KeyFactory.createKey(subject, predicate, object);
		addReferenceTriple(node, key);
		return key;
	}
	
	protected Key registerEmbeddedNode(Key subject, Key predicate, Key object,EmbeddedNode node) {
		final Key key = KeyFactory.createKey(subject, predicate, object);
		
		addNode((StarNode)node.getSubject(), subject);
		addNode((StarNode)node.getPredicate(), predicate);
		addNode((StarNode)node.getObject(), object);
		numberOfEmbeddedTriples++;
		return key;
	}
	
	protected Key registerOrGetEmbeddedNode(Key subject, Key predicate, Key object, EmbeddedNode node) {
		//If reference triple
		if (subject.getId() > Config.getLargestSubjectId() || 
				predicate.getId() > Config.getLargestPredicateId() ||
				object.getId() > Config.getLargestObjectId()) {
			if (containsReferenceNode(node)) {
				return getReferenceDictionaryKey(node);
			}  else {
				return registerReferenceNode(subject, predicate, object,node);
			}
		//If special reference triple distribution is enabled
		} else if (isThereAnySpecialReferenceTripleDistributionConditions()) {
			if (containsReferenceNode(node)) {
				return getReferenceDictionaryKey(node);
			} else if (shouldNextNodeBeAnReference()){
				Key key = KeyFactory.createReferenceTriple();
				addReferenceTriple(node, key);
				return key;
			} else {
				return registerEmbeddedNode(subject, predicate, object,node);
			}
		//If embedded triple
		} else {
			if (containsSimpleKey(subject) && containsSimpleKey(predicate) && containsSimpleKey(object) ) {
				numberOfEmbeddedTriples++;
				return KeyFactory.createKey(subject, predicate, object);
			} else {
				return registerEmbeddedNode(subject, predicate, object,node);
			}
		}
	}
	
	protected void addNode(StarNode node, final Key key) {
		if (node instanceof EmbeddedNode) {
			numberOfEmbeddedTriples++;
		}
		addToNodeDictionary(node, key);
	}
	
	protected boolean shouldNextNodeBeAnReference() {
		float currentDistribtuion = (float)referenceDictionarySize()/((float)numberOfEmbeddedTriples+1)*(float)100;
		return (currentDistribtuion < referenceTripleDistributionPercentage);
	}
	
	protected void addReferenceTriple(StarNode node, final Key key) {
		numberOfEmbeddedTriples++;
		addToReferenceDictionary(node, key);
	}
	
	@Override
	public Key getReferernceTripleKey(Key subjectId, Key predicateId, Key objectId) {
		Node subject = getNode(subjectId);
		Node predicate = getNode(predicateId);
		Node object = getNode(objectId);
		StarNode referenceTriple =  NodeFactoryStar.createEmbeddedNode(subject, predicate, object);
		return getReferenceDictionaryKey(referenceTriple);
	}
	
	@Override
	public Node getNode(Key id) {
		if (BitHelper.isReferenceBitSet(id)) {
			return getReferenceDictionaryNode(id);
		} else if (BitHelper.isIdAnEmbeddedTriple(id)) {
			Node subject = getNodeDictionaryNode(new Key(BitHelper.getEmbeddedSubject(id)));
			Node predicate = getNodeDictionaryNode(new Key(BitHelper.getEmbeddedPredicate(id)));
			Node object = getNodeDictionaryNode(new Key(BitHelper.getEmbeddedObject(id)));
			return NodeFactoryStar.createEmbeddedNode(subject, predicate, object);
		} else {
			return getNodeDictionaryNode(id);
		}
	}
	
	protected Key registerOrGetNode(SimpleNode node) {
		if (containsSimpleNode(node)) {
			return getNodeDictionaryKey(node);
		} else {
			return registerNode(node);
		}
	}
	
	protected Key registerNode(SimpleNode node) {
		final Key key = KeyFactory.createKey(node);
		addToNodeDictionary(node,key);
		return key;
	}
	
	@Override
	public void setReferenceTripleDistribution(int i) {
		if (i == 0) {
			isThereAnySpecialReferenceTripleDistributionConditions = false;
			referenceTripleDistributionPercentage = i;
		} else {
			if (nodeDirectorySize() != 0) {
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
	}
	
	@Override
	public int size() {
		return nodeDirectorySize()+referenceDictionarySize();
	}

	@Override
	public Key createKey(Node node) {
		return nodeToKey(node);
	}
	
	@Override
	public int getNumberOfReferenceTriples() {
		return referenceDictionarySize();
	}
	
	@Override
	public boolean containsReferernceTripleKey(Key subjectId, Key predicateId, Key objectId) {
		if (containsSimpleKey(subjectId) && containsSimpleKey(predicateId) && containsSimpleKey(objectId)) {
			Node subject = getNode(subjectId);
			Node predicate = getNode(predicateId);
			Node object = getNode(objectId);
			StarNode referenceTriple =  NodeFactoryStar.createEmbeddedNode(subject, predicate, object);
			
			return containsReferenceNode(referenceTriple);
		} else {
			return false;
		}
	}
	
	@Override
	public void clear() {
		clearNodeDirectory();
		clearReferenceNodeDirectory();
		KeyFactory.reset();
		numberOfEmbeddedTriples = 0;
		isThereAnySpecialReferenceTripleDistributionConditions = false;
		setReferenceTripleDistribution(0);
	}
	
	protected abstract void clearNodeDirectory();
	protected abstract void clearReferenceNodeDirectory();
	
	protected abstract int nodeDirectorySize();
	protected abstract int referenceDictionarySize();
	
	protected abstract boolean containsSimpleNode(StarNode node);
	protected abstract boolean containsSimpleKey(Key subject) ;
	protected abstract boolean containsReferenceNode(StarNode node) ;
	
	protected abstract void addToNodeDictionary(StarNode node,Key key);
	protected abstract void addToReferenceDictionary(StarNode node,Key key);
	
	protected abstract Key getNodeDictionaryKey(StarNode node);
	protected abstract StarNode getNodeDictionaryNode(Key id);
	protected abstract StarNode getReferenceDictionaryNode(Key id);
	protected abstract Key getReferenceDictionaryKey(StarNode node);
}

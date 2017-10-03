package dk.aau.cs.qweb.dictionary;

import java.io.IOException;
import java.util.HashMap;

import dk.aau.cs.qweb.node.NodeFactoryStar;
import dk.aau.cs.qweb.node.StarNode;
import dk.aau.cs.qweb.triple.Key;

public class BTreeHybridDictionary extends BTreeDiskDictionary {
	private static BTreeHybridDictionary instance;
	private int bufferSize = 10000;
	private HashMap<String, Long> node2IdDictionaryBuffer;
	private HashMap<Long, String> id2NodeDictionaryBuffer;
	private HashMap<String, Long> referenceNode2IdDictionaryBuffer;
	private HashMap<Long, String> id2ReferenceNodeDictionaryBuffer;
	private int referenceBufferWrittenToDiskTimes = 0;
	private int nodeBufferWrittenToDiskTimes = 0;

	public static BTreeHybridDictionary getBTreeHybridDictionaryInstance()  {
		if(instance == null) {
	         instance = new BTreeHybridDictionary();
	    }
	    return instance;
	}
	
	public BTreeHybridDictionary() {
		super();
		
		node2IdDictionaryBuffer =  new HashMap<String,Long>(bufferSize);
		id2NodeDictionaryBuffer = new HashMap<Long, String>(bufferSize);
		referenceNode2IdDictionaryBuffer =  new HashMap<String,Long>(bufferSize);
		id2ReferenceNodeDictionaryBuffer = new HashMap<Long, String>(bufferSize);
	}
	
	@Override
	public void clear() throws IOException {
		super.clear();
		node2IdDictionaryBuffer.clear();
		id2NodeDictionaryBuffer.clear();
		referenceNode2IdDictionaryBuffer.clear();
		id2ReferenceNodeDictionaryBuffer.clear();
	}
	
	@Override
	protected boolean containsSimpleKey(Key key) {
		if (id2NodeDictionaryBuffer.containsKey(key.getId())) {
			return true;
		} else {
			return super.containsSimpleKey(key);
		}
	}
	
	@Override
	protected boolean containsSimpleNode(StarNode node) {
		if (node2IdDictionaryBuffer.containsKey(node.serialize())) {
			return true;
		} else {
			return super.containsSimpleNode(node);
		}
	}
	
	@Override
	protected boolean containsReferenceNode(StarNode node) {
		if (referenceNode2IdDictionaryBuffer.containsKey(node.serialize())) {
			return true;
		} else {
			return super.containsReferenceNode(node);
		}
	}
	
	@Override
	protected boolean containsReferenceKey(Key key) {
		if (id2ReferenceNodeDictionaryBuffer.containsKey(key.getId())) {
			return true;
		} else {
			return super.containsReferenceKey(key);
		}
	}
	
	@Override
	protected Key getNodeDictionaryKey(StarNode node) {
		if (node2IdDictionaryBuffer.containsKey(node.serialize())) {
			Long id = node2IdDictionaryBuffer.get(node.serialize());
			return new Key(id);
		} else {
			return super.getNodeDictionaryKey(node);
		}
	}

	@Override
	protected Key getReferenceDictionaryKey(StarNode node) {
		if (referenceNode2IdDictionaryBuffer.containsKey(node.serialize())) {
			Long id = referenceNode2IdDictionaryBuffer.get(node.serialize());
			return new Key(id);
		} else {
			return super.getReferenceDictionaryKey(node);
		}
	}

	@Override
	protected StarNode getReferenceDictionaryNode(Key key) {
		if (id2ReferenceNodeDictionaryBuffer.containsKey(key.getId())) {
			String serializedNodeString = id2ReferenceNodeDictionaryBuffer.get(key.getId());
			return NodeFactoryStar.createNode(serializedNodeString);
		} else {
			return super.getReferenceDictionaryNode(key);
		}
	}
	
	@Override
	protected StarNode getNodeDictionaryNode(Key key) {
		if (id2NodeDictionaryBuffer.containsKey(key.getId())) {
			String serializedNodeString = id2NodeDictionaryBuffer.get(key.getId());
			return NodeFactoryStar.createNode(serializedNodeString);
		} else {
			return super.getNodeDictionaryNode(key);
		}
	}
	
	@Override
	protected void addToReferenceDictionary(StarNode node, Key key) {
		if (referenceNode2IdDictionaryBuffer.size() >= bufferSize) {
			
			id2ReferenceNodeDictionary.putAll(id2ReferenceNodeDictionaryBuffer);
			referenceNode2IdDictionary.putAll(referenceNode2IdDictionaryBuffer);
			
			id2ReferenceNodeDictionaryBuffer.clear();
			referenceNode2IdDictionaryBuffer.clear();
			referenceBufferWrittenToDiskTimes++;
		}
		
		id2ReferenceNodeDictionaryBuffer.put(key.getId(), node.serialize());
		referenceNode2IdDictionaryBuffer.put(node.serialize(), key.getId());
		
		referenceDictionarySize++;
	}
	
	@Override
	protected void addToNodeDictionary(StarNode node, Key key) {
		if (node2IdDictionaryBuffer.size() >= bufferSize) {
			
			id2NodeDictionary.putAll(id2NodeDictionaryBuffer);
			node2IdDictionary.putAll(node2IdDictionaryBuffer);
			
			id2NodeDictionaryBuffer.clear();
			node2IdDictionaryBuffer.clear();
			nodeBufferWrittenToDiskTimes++;
		}
		
		id2NodeDictionaryBuffer.put(key.getId(), node.serialize());
		node2IdDictionaryBuffer.put(node.serialize(), key.getId());
		
		nodeDictionarySize++;
	}
	
	@Override
	public void logStatistics() {
		super.logStatistics();
		log.debug("BTreeHybridDictionary");
		log.debug("Buffer size: " +bufferSize);
		log.debug("Reference buffer written to disk "+referenceBufferWrittenToDiskTimes +" Times");
		log.debug("Node buffer written to disk "+nodeBufferWrittenToDiskTimes +" Times");
		log.debug("Reference buffer contains "+referenceNode2IdDictionaryBuffer.size()+" triples");
		log.debug("Node buffer contains "+referenceNode2IdDictionaryBuffer.size()+" triples");
		log.debug("");
	}
}

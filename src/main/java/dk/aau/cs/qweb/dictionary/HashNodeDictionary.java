package dk.aau.cs.qweb.dictionary;

import java.util.Map.Entry;

import com.google.common.collect.HashBiMap;

import dk.aau.cs.qweb.main.Config;
import dk.aau.cs.qweb.node.StarNode;
import dk.aau.cs.qweb.triple.Key;

public class HashNodeDictionary extends AbstractNodeDictionary {
	private static HashNodeDictionary instance;
	HashBiMap<Key,StarNode> nodeDictionary;
	HashBiMap<Key,StarNode> referenceNodeDictionary;
	
	private HashNodeDictionary() {
		
		nodeDictionary = HashBiMap.create(Config.getNodeDictionaryInitialSize());
		referenceNodeDictionary = HashBiMap.create(Config.getReferenceNodeDictionaryInitialSize());
		numberOfEmbeddedTriples = 0;
	}
	
	protected static HashNodeDictionary getInstance() {
		if(instance == null) {
	         instance = new HashNodeDictionary();
	    }
	    return instance;
	}
	
	@Override
	protected int referenceDictionarySize() {
		return referenceNodeDictionary.size();
	}
	
	@Override
	protected Key getNodeDictionaryKey(StarNode node) {
		return nodeDictionary.inverse().get(node);
	}

	@Override
	protected boolean containsSimpleNode(StarNode node) {
		return nodeDictionary.containsValue(node);
	}

	@Override
	protected Key getReferenceDictionaryKey(StarNode node) {
		return referenceNodeDictionary.inverse().get(node);
	}

	@Override
	protected boolean containsReferenceNode(StarNode node) {
		return referenceNodeDictionary.containsValue(node);
	}

	@Override
	protected void addToNodeDictionary(StarNode node, final Key key) {
		nodeDictionary.put(key, node);
	}

	@Override
	protected void addToReferenceDictionary(StarNode node, final Key key) {
		referenceNodeDictionary.put(key, node);
	}

	@Override
	protected StarNode getNodeDictionaryNode(Key id) {
		return nodeDictionary.get(id);
	}

	@Override
	protected StarNode getReferenceDictionaryNode(Key id) {
		return referenceNodeDictionary.get(id);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Node Dict\n");
		
		for (Entry<Key, StarNode> iterable_element : nodeDictionary.entrySet()) {
			sb.append(iterable_element.getKey()+": "+ iterable_element.getValue()+"\n");
		}
		
		sb.append("\nReference Triple Dict\n");
		for (Entry<Key, StarNode> iterable_element : referenceNodeDictionary.entrySet()) {
			sb.append(iterable_element.getKey()+": "+ iterable_element.getValue()+"\n");
		}
		return sb.toString();
	}

	 @Override
	 protected int nodeDirectorySize() {
		 return nodeDictionary.size();
	 }

	@Override
	public void close() {}

	@Override
	public void open() {}

	@Override
	protected void clearNodeDirectory() {
		nodeDictionary.clear();
	}

	@Override
	protected void clearReferenceNodeDirectory() {
		referenceNodeDictionary.clear();
	}

	@Override
	protected boolean containsSimpleKey(Key key) {
		return nodeDictionary.containsKey(key);
	}
	
	@Override
	protected boolean containsReferenceKey(Key key) {
		return referenceNodeDictionary.containsKey(key);
	}

	@Override
	protected void logStatistics() {
	}
}

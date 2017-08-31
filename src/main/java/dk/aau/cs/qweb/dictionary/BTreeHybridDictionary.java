package dk.aau.cs.qweb.dictionary;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import dk.aau.cs.qweb.node.NodeFactoryStar;
import dk.aau.cs.qweb.node.SimpleNode;
import dk.aau.cs.qweb.node.StarNode;
import dk.aau.cs.qweb.triple.Key;

public class BTreeHybridDictionary extends AbstractNodeDictionary  {

	private static BTreeHybridDictionary instance;
	private BTreeMap<String, Long> node2IdDictionary;
	private BTreeMap<Long, String> id2NodeDictionary;
	private BTreeMap<String, Long> referenceNode2IdDictionary;
	private BTreeMap<Long, String> id2ReferenceNodeDictionary;
	private int nodeDictionarySize;
	private int referenceDictionarySize;
	private DB id2ReferenceNodeDB;
	private DB id2NodeDB;
	private DB node2IdDB;
	private DB referenceNode2IdDB;

	private BTreeHybridDictionary() {
	}

	@Override
	public void open() {
		openId2Node();
		openNode2Id();
		openId2ReferenceNode();
		openReferenceNode2Id();
	}
	
	private void openId2ReferenceNode() {
		id2ReferenceNodeDB = DBMaker
		        .fileDB("openId2ReferenceNode.db")
		        .fileMmapEnable()
		        .fileMmapPreclearDisable()   // Make mmap file faster

		        // Unmap (release resources) file when its closed.
		        // That can cause JVM crash if file is accessed after it was unmapped
		        // (there is possible race condition).
		        .cleanerHackEnable()
		        .make();
		
		id2ReferenceNodeDictionary = id2ReferenceNodeDB.treeMap("id2ReferenceNodeDictionary")
		        .keySerializer(Serializer.LONG)
		        .valueSerializer(Serializer.STRING)
		        .createOrOpen();
	}
	
	private void openId2Node() {
		id2NodeDB = DBMaker
		        .fileDB("openId2Node.db")
		        .fileMmapEnable()
		        .fileMmapPreclearDisable()   // Make mmap file faster

		        // Unmap (release resources) file when its closed.
		        // That can cause JVM crash if file is accessed after it was unmapped
		        // (there is possible race condition).
		        .cleanerHackEnable()
		        .make();
		
		id2NodeDictionary = id2NodeDB.treeMap("id2NodeDictionary")
		        .keySerializer(Serializer.LONG)
		        .valueSerializer(Serializer.STRING)
		        .createOrOpen();
	}
	
	private void openNode2Id() {
		node2IdDB = DBMaker
		        .fileDB("openNode2Id.db")
		        .fileMmapEnable()
		        .fileMmapPreclearDisable()   // Make mmap file faster

		        // Unmap (release resources) file when its closed.
		        // That can cause JVM crash if file is accessed after it was unmapped
		        // (there is possible race condition).
		        .cleanerHackEnable()
		        .make();
		
		node2IdDictionary = node2IdDB.treeMap("node2Id")
		        .keySerializer(Serializer.STRING)
		        .valueSerializer(Serializer.LONG)
		        .createOrOpen();
	}
	
	private void openReferenceNode2Id() {
		referenceNode2IdDB = DBMaker
		        .fileDB("openReferenceNode2Id.db")
		        .fileMmapEnable()
		        .fileMmapPreclearDisable()   // Make mmap file faster

		        // Unmap (release resources) file when its closed.
		        // That can cause JVM crash if file is accessed after it was unmapped
		        // (there is possible race condition).
		        .cleanerHackEnable()
		        .make();
		
		referenceNode2IdDictionary = referenceNode2IdDB.treeMap("map")
		        .keySerializer(Serializer.STRING)
		        .valueSerializer(Serializer.LONG)
		        .createOrOpen();
	}
	
	@Override
	public void close() {
		id2ReferenceNodeDB.close();
		id2NodeDB.close();
		node2IdDB.close();
		referenceNode2IdDB.close();
	}
	
	protected static BTreeHybridDictionary getInstance() {
		if(instance == null) {
	         instance = new BTreeHybridDictionary();
	    }
	    return instance;
	}

	@Override
	protected int nodeDirectorySize() {
		return nodeDictionarySize;
	}

	@Override
	protected Key getNodeDictionaryKey(StarNode node) {
		return new Key(node2IdDictionary.get(((SimpleNode)node).serialize()));
	}

	@Override
	protected boolean containsSimpleNode(StarNode node) {
		return node2IdDictionary.containsKey(((SimpleNode)node).serialize());
	}

	@Override
	protected Key getReferenceDictionaryKey(StarNode node) {
		return new Key(referenceNode2IdDictionary.get(node.serialize()));
	}

	@Override
	protected boolean containsReferenceNode(StarNode node) {
		return referenceNode2IdDictionary.containsKey((node).serialize());
	}

	@Override
	protected void addToNodeDictionary(StarNode node, Key key) {
		id2NodeDictionary.put(key.getId(), ((SimpleNode)node).serialize());
		node2IdDictionary.put(((SimpleNode)node).serialize(),key.getId());
		nodeDictionarySize++;
	}

	@Override
	protected void addToReferenceDictionary(StarNode node, Key key) {
		id2ReferenceNodeDictionary.put(key.getId(), node.serialize());
		referenceNode2IdDictionary.put(node.serialize(),key.getId());
		referenceDictionarySize++;
	}

	@Override
	protected int referenceDictionarySize() {
		return referenceDictionarySize;
	}

	@Override
	protected StarNode getNodeDictionaryNode(Key id) {
		String serializedNodeString = id2NodeDictionary.get(id.getId());
		return NodeFactoryStar.createNode(serializedNodeString);
	}

	@Override
	protected StarNode getReferenceDictionaryNode(Key id) {
		String serializedNodeString = id2ReferenceNodeDictionary.get(id.getId());
		return NodeFactoryStar.createNode(serializedNodeString);
	}
	
	@Override
	public void clear() {
		super.clear();
		nodeDictionarySize = 0;
		referenceDictionarySize = 0;
	}

	@Override
	protected void clearNodeDirectory() {
		node2IdDictionary.clear();
		id2NodeDictionary.clear();
	}

	@Override
	protected void clearReferenceNodeDirectory() {
		referenceNode2IdDictionary.clear();
		id2ReferenceNodeDictionary.clear();
	}

	@Override
	protected boolean containsSimpleKey(Key key) {
		return id2NodeDictionary.containsKey(key.getId());
	}
}

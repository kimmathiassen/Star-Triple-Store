package dk.aau.cs.qweb.dictionary;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import dk.aau.cs.qweb.main.Config;
import dk.aau.cs.qweb.node.NodeFactoryStar;
import dk.aau.cs.qweb.node.SimpleNode;
import dk.aau.cs.qweb.node.StarNode;
import dk.aau.cs.qweb.triple.Key;

public class BTreeDiskDictionary extends AbstractNodeDictionary  {

	private static BTreeDiskDictionary instance = null;
	protected BTreeMap<String, Long> node2IdDictionary;
	protected BTreeMap<Long, String> id2NodeDictionary;
	protected BTreeMap<String, Long> referenceNode2IdDictionary;
	protected BTreeMap<Long, String> id2ReferenceNodeDictionary;
	private DB database;
	protected boolean open = false;

	protected BTreeDiskDictionary() {
		open();
	}

	@Override
	public void open() {
		if (!open) {
			open = true;
			database = DBMaker
			        .fileDB(dbName())
			        .fileMmapEnable()
			        .fileMmapPreclearDisable()   // Make mmap file faster
			        .cleanerHackEnable()
			        .make();
			
			openId2Node();
			openNode2Id();
			openId2ReferenceNode();
			openReferenceNode2Id();
		}
		
	}
	
	private String dbName() {
		String name = Config.getLocationFileName()+
				Config.getEmbeddedHeaderSize()+
				Config.getSubjectSizeInBits()+
				Config.getPredicateSizeInBits()+
				Config.getObjectSizeInBits()+".db";
		return name; 
	}
	

	
	private void openId2ReferenceNode() {
		id2ReferenceNodeDictionary = database.treeMap("id2Reference")
		        .keySerializer(Serializer.LONG)
		        .valueSerializer(Serializer.STRING)
		        .createOrOpen();
	}
	
	private void openId2Node() {
		id2NodeDictionary = database.treeMap("id2Node")
		        .keySerializer(Serializer.LONG)
		        .valueSerializer(Serializer.STRING)
		        .createOrOpen();
	}
	
	private void openNode2Id() {
		node2IdDictionary = database.treeMap("node2Id")
		        .keySerializer(Serializer.STRING)
		        .valueSerializer(Serializer.LONG)
		        .createOrOpen();
	}
	
	private void openReferenceNode2Id() {
		referenceNode2IdDictionary = database.treeMap("reference2Id")
		        .keySerializer(Serializer.STRING)
		        .valueSerializer(Serializer.LONG)
		        .createOrOpen();
	}
	
	@Override
	public void close() {
		database.close();
		open = false;
	}

	@Override
	protected int nodeDirectorySize() {
		return node2IdDictionary.size();
	}

	@Override
	protected boolean containsSimpleNode(SimpleNode node) {
		return node2IdDictionary.containsKey(node.serialize());
	}

	@Override
	protected Key getReferenceDictionaryKey(StarNode node) {
		return new Key(referenceNode2IdDictionary.get(node.serialize()));
	}
	
	@Override
	protected Key getNodeDictionaryKey(SimpleNode node) {
		return new Key(node2IdDictionary.get(node.serialize()));
	}

	@Override
	protected boolean containsReferenceNode(StarNode node) {
		return referenceNode2IdDictionary.containsKey((node).serialize());
	}

	@Override
	protected void addToNodeDictionary(SimpleNode node, Key key) {
		id2NodeDictionary.put(key.getId(), ((SimpleNode)node).serialize());
		node2IdDictionary.put(((SimpleNode)node).serialize(),key.getId());
	}

	@Override
	protected void addToReferenceDictionary(StarNode node, Key key) {
		id2ReferenceNodeDictionary.put(key.getId(), node.serialize());
		referenceNode2IdDictionary.put(node.serialize(),key.getId());
	}

	@Override
	protected int referenceDictionarySize() {
		return referenceNode2IdDictionary.size();
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
	}

	@Override
	protected void clearNodeDirectory() {
		if (node2IdDictionary != null) {
			node2IdDictionary.clear();
		}
		if (id2NodeDictionary != null) {
			id2NodeDictionary.clear();
		}
	}

	@Override
	protected void clearReferenceNodeDirectory() {
		if (referenceNode2IdDictionary != null) {
			referenceNode2IdDictionary.clear();
		}
		if (id2ReferenceNodeDictionary != null) {
			id2ReferenceNodeDictionary.clear();
		}
	}

	@Override
	protected boolean containsSimpleKey(Key key) {
		return id2NodeDictionary.containsKey(key.getId());
	}

	@Override
	protected boolean containsReferenceKey(Key key) {
		return id2ReferenceNodeDictionary.containsKey(key);
	}

	@Override
	public void logStatistics() {
		log.debug("BTreeDiskDictionary");
		log.debug("Node Dictionary Size: "+nodeDirectorySize());
		log.debug("Reference Dictionary Size: "+referenceDictionarySize());
		log.debug("");
	}

	public static BTreeDiskDictionary getBTreeDiskDictionaryInstance() {
		if(instance == null) {
	         instance = new BTreeDiskDictionary();
	    }
	    return instance;
	}


}

package dk.aau.cs.qweb.dictionary;

import java.io.IOException;
import java.util.Iterator;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import dk.aau.cs.qweb.helper.FileHelper;
import dk.aau.cs.qweb.main.Config;
import dk.aau.cs.qweb.node.SimpleNode;
import dk.aau.cs.qweb.triple.Key;


 /*
  * Class that makes loading the data faster by using a bloom filter to answer the containsSimpleKey query in a cheeper way.
  * This Class use memory to store the bloomfilter. 
  */
public class BTreeDiskBloomfilterDictionary extends BTreeDiskDictionary {
	
	private static BTreeDiskBloomfilterDictionary instance;
	
	public static BTreeDiskBloomfilterDictionary getBTreeDiskBloomfilterDictionaryInstance()  {
		if(instance == null) {
	         instance = new BTreeDiskBloomfilterDictionary();
	    }
	    return instance;
	}
	
	//Contains the simple  keys
	private BloomFilter<String> filter;
	private int falsePositive;
	private int negative;
	private int truePositive;

	private BTreeDiskBloomfilterDictionary()  {
		super();
		initilizeBloomfilter();
		initilizeStatistics();
	}

	private void initilizeStatistics() {
		falsePositive = 0;
		negative = 0;
		truePositive = 0;
	}

	private void initilizeBloomfilter() {
		int lines;
		try {
			lines = FileHelper.countLines(Config.getLocation());
		} catch (IOException e) {
			lines = 100000;
			log.warn("lines could not be counted, default value used instead");
		}
		
		//Here we estimate the number of unique elements.
		//the reasoning is that each line contains at least one unique object in average.
		//We also estimate that every 5th subject is unique (this is an over estimate),
		//we rather have a size that is a bit to large then to small.
		int numberOfUniqueElementsEstimate = (int) Math.round(lines * 1.2);
		log.debug("Initilizing bloomfilter with size "+numberOfUniqueElementsEstimate);
		filter = BloomFilter.create(
				  Funnels.unencodedCharsFunnel(),
				  numberOfUniqueElementsEstimate,
				  0.01);
		
		//If the dictionary already contains values, we need to populate the bloomfilter
		if (node2IdDictionary.getSize() != 0) {
			log.info("Dictionary found, populating bloomfilter");
			Iterator<String> iterator = node2IdDictionary.keyIterator();
			while (iterator.hasNext()) {
				filter.put(iterator.next());
			}
		}
	}
	
	@Override
	public void clear() {
		super.clear();
		initilizeBloomfilter(); //this clears the bloomfilter
	}
	
	@Override
	protected boolean containsSimpleNode(SimpleNode node) {
		if (filter.mightContain(node.serialize())) {
			//To avoid the scenario where a nonexisting node is requiested and null is returned.
			//We call contains on the disk store to ensure that there are no false positives returned by this method.
//			if (node2IdDictionary.containsKey(node.serialize())) {
//				truePositive++;
//				return true;
//			} else {
//				falsePositive++;
//				return false;
//			}
			return true;
		} else {
			negative++;
			return false;
		}
	}
	
	@Override
	protected Key getNodeDictionaryKey(SimpleNode node) {
		Long id = node2IdDictionary.get(node.serialize());
		if (id == null) {
			falsePositive++;
			//The null case can occure if the bloomfilter returnes a falsePositive, in this case the node does not exist and should be registered.
			return registerNode(node);
		}
		
		truePositive++;
		return new Key(id);
	}
	
	@Override
	protected void addToNodeDictionary(SimpleNode node, Key key) {
		filter.put(node.serialize());
		id2NodeDictionary.put(key.getId(), node.serialize());
		node2IdDictionary.put(node.serialize(), key.getId());
	}
	
	@Override
	public void logStatistics() {
		super.logStatistics();
		log.debug("BTreeDiskBloomfilterDictionary");
		log.debug("Total calls on bloomfilter: " + (falsePositive+ truePositive + negative));
		log.debug("Negative hits: " + (negative));
		log.debug("Positive hits: " + (falsePositive+ truePositive));
		log.debug("True positive hits: " + (truePositive));
		log.debug("False positive hits: " + (falsePositive));
		log.debug("");
	}
}

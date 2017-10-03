package dk.aau.cs.qweb.dictionary;

import java.io.IOException;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import dk.aau.cs.qweb.helper.FileHelper;
import dk.aau.cs.qweb.main.Config;
import dk.aau.cs.qweb.node.StarNode;
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
	private BloomFilter<Long> filter;
	private int falsePositive;
	private int negative;
	private int truePositive;

	private BTreeDiskBloomfilterDictionary()  {
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
				  Funnels.longFunnel(),
				  numberOfUniqueElementsEstimate,
				  0.01);
	}
	
	@Override
	public void clear() throws IOException {
		super.clear();
		initilizeBloomfilter(); //this clears the bloomfilter
	}
	
	@Override
	protected boolean containsSimpleKey(Key key) {
		if (filter.mightContain(key.getId())) {
			
			//To avoid the scenario where a nonexisting key is requiested and null is returned.
			//We call contains on the disk store to ensure that there are no false positives returned by this method.
			
			if (id2NodeDictionary.containsKey(key.getId())) {
				truePositive++;
				return true;
			} else {
				falsePositive++;
				return false;
			}
			
		} else {
			negative++;
			return false;
		}
	}
	
	@Override
	protected void addToNodeDictionary(StarNode node, Key key) {
		filter.put(key.getId());
		id2NodeDictionary.put(key.getId(), node.serialize());
		node2IdDictionary.put(node.serialize(), key.getId());
		
		nodeDictionarySize++;
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

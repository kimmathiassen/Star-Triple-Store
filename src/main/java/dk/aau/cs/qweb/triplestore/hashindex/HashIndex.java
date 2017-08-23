package dk.aau.cs.qweb.triplestore.hashindex;

import java.util.HashMap;

import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triple.TripleStar;
import dk.aau.cs.qweb.triplestore.treeindex.TreeIndex;

/**
 * The hash index use a nested hashmap structure.
 * The first level is a hashmap containing a hashmap at each value.
 * The inner hash map contains the MapTripleBunch {@link MapTripleBunch} structure.
 * 
 * This data structure seems to perform the best in terms for lookups and inserts, 
 * the memory footprint is large then the TreeIndex {@link TreeIndex}. 
 *
 */
public class HashIndex extends MapIndex  {
	public HashIndex(Field field1,Field field2,Field field3) {
		this.field1 = field1;
		this.field2 = field2;
		this.field3 = field3;
		indexMap = new HashMap<Key,MapTripleBunch>();
		size = 0;
	}

	public void add(final TripleStar t) {
		Key firstKey = getFieldKey(field1,t);
		if (indexMap.containsKey(firstKey)) {
			indexMap.get(firstKey).put(field2,t);
		} else {
			MapTripleBunch tripleBunch = new HashTripleBunch();
			tripleBunch.put(field2,t);
			indexMap.put(firstKey, tripleBunch);
		}
		size++;
	}
}

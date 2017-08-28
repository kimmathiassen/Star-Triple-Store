package dk.aau.cs.qweb.triplestore.treeindex;

import java.util.TreeMap;

import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triple.TripleStar;
import dk.aau.cs.qweb.triplestore.hashindex.MapIndex;
import dk.aau.cs.qweb.triplestore.hashindex.MapTripleBunch;

/**
 * A map based index using tree maps both as the outer and inner map.
 */
public class TreeIndex extends MapIndex  {
	
	public TreeIndex(Field field1,Field field2,Field field3) {
		this.field1 = field1;
		this.field2 = field2;
		this.field3 = field3;
		indexMap = new TreeMap<Key,MapTripleBunch>();
		size = 0;
	}
	
	public void add(final TripleStar triple) {
		Key firstKey = getFieldKey(field1,triple);
		if (indexMap.containsKey(firstKey)) {
			indexMap.get(firstKey).put(field2,triple);
		} else {
			TreeTripleBunch tripleBunch = new TreeTripleBunch();
			tripleBunch.put(field2,triple);
			indexMap.put(firstKey, tripleBunch);
		}
		size++;
	}
}
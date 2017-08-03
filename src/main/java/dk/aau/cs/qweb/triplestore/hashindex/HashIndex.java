package dk.aau.cs.qweb.triplestore.hashindex;

import java.util.TreeMap;

import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triple.TripleStar;
import dk.aau.cs.qweb.triplestore.hashindex.MapIndex.Field;

public class HashIndex extends MapIndex  {
	public HashIndex(Field field1,Field field2,Field field3) {
		this.field1 = field1;
		this.field2 = field2;
		this.field3 = field3;
		indexMap = new TreeMap<Key,MapTripleBunch>();
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

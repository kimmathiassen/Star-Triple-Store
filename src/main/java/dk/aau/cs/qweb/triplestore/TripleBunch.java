package dk.aau.cs.qweb.triplestore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import dk.aau.cs.qweb.triple.TripleStar;
import dk.aau.cs.qweb.triple.Key;

public class TripleBunch  {
	Map<Key,HashSet<TripleStar>> innerMap;
	
	public TripleBunch() {
		innerMap = new HashMap<Key,HashSet<TripleStar>>();
	}

	public void put(Key field2, TripleStar triple) {
		if (innerMap.containsKey(field2)) {
			innerMap.get(field2).add(triple);
		} else {
			HashSet<TripleStar> array = new HashSet<TripleStar>();
			array.add(triple);
			innerMap.put(field2, array);
		}
	}

	public Iterator<TripleStar> iterator(Key key) {
		if (innerMap.containsKey(key)) {
			return innerMap.get(key).iterator();
		} else {
			//return an iterator of all arrayLists
			return new IteratorOfIterators(innerMap.values());
		}
	}
	
	public Iterator<TripleStar> iterator() {
		return new IteratorOfIterators(innerMap.values());
	}
}

package dk.aau.cs.qweb.triplestore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import dk.aau.cs.qweb.triple.IdTriple;
import dk.aau.cs.qweb.triple.Key;

public class TripleBunch  {
	Map<Key,HashSet<IdTriple>> innerMap;
	
	public TripleBunch() {
		innerMap = new HashMap<Key,HashSet<IdTriple>>();
	}

	public void put(Key field2, IdTriple triple) {
		if (innerMap.containsKey(field2)) {
			innerMap.get(field2).add(triple);
		} else {
			HashSet<IdTriple> array = new HashSet<IdTriple>();
			array.add(triple);
			innerMap.put(field2, array);
		}
	}

	public Iterator<IdTriple> iterator(Key key) {
		if (innerMap.containsKey(key)) {
			return innerMap.get(key).iterator();
		} else {
			//return an iterator of all arrayLists
			return new IteratorOfIterators(innerMap.values());
		}
	}
	
	public Iterator<IdTriple> iterator() {
		return new IteratorOfIterators(innerMap.values());
	}
}

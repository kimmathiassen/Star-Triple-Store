package dk.aau.cs.qweb.triplestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import dk.aau.cs.qweb.triple.IdTriple;
import dk.aau.cs.qweb.triple.Key;

public class TripleBunch  {
	Map<Key,ArrayList<IdTriple>> innerMap;
	
	public TripleBunch() {
		innerMap = new HashMap<Key,ArrayList<IdTriple>>();
	}

	public void put(Key field2, IdTriple triple) {
		if (innerMap.containsKey(field2)) {
			innerMap.get(field2).add(triple);
		} else {
			ArrayList<IdTriple> array = new ArrayList<IdTriple>();
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

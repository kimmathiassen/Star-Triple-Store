package dk.aau.cs.qweb.triplestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triple.TripleStar;
import dk.aau.cs.qweb.triple.TripleStarPattern;

public class TripleBunch  {
	Map<Key, ArrayList<TripleStar>> innerMap;
	
	public TripleBunch() {
		innerMap = new HashMap<Key,ArrayList<TripleStar>>();
	}

	public void put(Key field2, TripleStar triple) {
		if (innerMap.containsKey(field2)) {
			innerMap.get(field2).add(triple);
		} else {
			ArrayList<TripleStar> array = new ArrayList<TripleStar>();
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

	public Iterator<TripleStar> iterator(Key key, TripleStarPattern triple) {
		TripleStar ts = new TripleStar(triple.getSubject().getKey(),triple.getPredicate().getKey(),triple.getObject().getKey());
		if (innerMap.get(key).contains(ts)) {
			HashSet<TripleStar> set = new HashSet<TripleStar>();
			set.add(ts);
			return set.iterator();
		}
		return Collections.emptyIterator();
	}

	public void eliminateDuplicates() {
		for (Entry<Key, ArrayList<TripleStar>> list : innerMap.entrySet()) {
			ArrayList<Integer> duplicates = new ArrayList<Integer>();
			Collections.sort(list.getValue());
			TripleStar previous = null;
			for (TripleStar tripleStar : list.getValue()) {
				if (tripleStar.equals(previous)) {
					duplicates.add(list.getValue().indexOf(tripleStar));
				}
				previous = tripleStar;
			}
			
			for (Integer integer : duplicates) {
				innerMap.get(list.getKey()).remove((int)integer);
			}
		}
	}
}

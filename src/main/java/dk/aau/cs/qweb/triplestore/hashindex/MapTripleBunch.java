package dk.aau.cs.qweb.triplestore.hashindex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.collections4.iterators.IteratorChain;
import org.apache.jena.ext.com.google.common.collect.Lists;

import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triple.TripleStar;
import dk.aau.cs.qweb.triplestore.KeyContainer;
import dk.aau.cs.qweb.triplestore.hashindex.MapIndex.Field;

public abstract class MapTripleBunch  {
	protected Map<Key, ArrayList<KeyContainer>> innerMap;
	
	public MapTripleBunch() {
		innerMap = new TreeMap<Key,ArrayList<KeyContainer>>();
	}

	public void put(Field field2, final TripleStar triple) {
		KeyContainer key = extractThirdField(triple,field2);
		if (innerMap.containsKey(getFieldKey(field2,triple))) {
			innerMap.get(getFieldKey(field2,triple)).add(key);
		} else {
			ArrayList<KeyContainer> array = new ArrayList<KeyContainer>();
			array.add(key);
			innerMap.put(getFieldKey(field2,triple), array);
		}
	}
	
	private Key getFieldKey(Field field,TripleStar t) {
		if (field == Field.S) {
			return t.subjectId;
		} else if (field == Field.P) {
			return t.predicateId;
		} else {
			return t.objectId;
		}
	}

	private KeyContainer extractThirdField(TripleStar triple, Field field) {
		if (field  == Field.S) {
			return new KeyContainer(triple.predicateId,Field.P);
		} else if (field == Field.P) {
			return new KeyContainer(triple.objectId,Field.O);
		} else {
			return new KeyContainer(triple.subjectId,Field.S);
		}
	}

	//One variable
	public Iterator<KeyContainer> iterator(Key key, Field f) {
		if (innerMap.containsKey(key)) {
			return new IteratorWrapper(innerMap.get(key).iterator(),key,f);
		}
		return Collections.emptyIterator();
	}
	
	//Two variables
	public Iterator<KeyContainer> iterator(Field f) {
		IteratorChain<KeyContainer> chain = new IteratorChain<KeyContainer>();
		for (Entry<Key, ArrayList<KeyContainer>> iterable_element : innerMap.entrySet()) {
			chain.addIterator(new IteratorWrapper(iterable_element.getValue().iterator(),iterable_element.getKey(),f));
		}
		return chain;
	}

	public void eliminateDuplicates() {
		for (Entry<Key, ArrayList<KeyContainer>> list : innerMap.entrySet()) {
			ArrayList<Integer> duplicates = new ArrayList<Integer>();
			ArrayList<KeyContainer> values = list.getValue();
			Collections.sort(values);
			KeyContainer previous = null;
			for (KeyContainer tripleStar : list.getValue()) {
				if (tripleStar.equals(previous)) {
					duplicates.add(values.indexOf(tripleStar));
				}
				previous = tripleStar;
			}
			//Remove dup in reverse order to avoid changeing the indexs
			for (Integer integer : Lists.reverse(duplicates)) {
				list.getValue().remove((int)integer);
			}
		}
	}
	
	@Override
	public String toString() {
		return "TripleBunch( "+innerMap+")";
	}
}

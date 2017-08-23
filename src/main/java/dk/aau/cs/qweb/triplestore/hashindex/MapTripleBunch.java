package dk.aau.cs.qweb.triplestore.hashindex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.iterators.IteratorChain;
import org.apache.jena.ext.com.google.common.collect.Lists;

import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triple.TripleStar;
import dk.aau.cs.qweb.triplestore.KeyContainer;
import dk.aau.cs.qweb.triplestore.hashindex.MapIndex.Field;

/**
 * The inner class used by map type indexes. 
 * Children of this call decide how the "innerMap" is instantiated.
 */
public abstract class MapTripleBunch  {
	protected Map<Key, ArrayList<KeyContainer>> innerMap;
	
	
	/**
	 * Method for adding a triple to the inner map. 
	 * The field is used to extract the key from the triple.
	 * Only the last element of the triple is saved in the value field of the map.
	 * 
	 * @param The field, in the case of a SPO index it would be the P
	 * @param The triple being added.
	 */
	public void put(Field field, final TripleStar triple) {
		KeyContainer key = extractThirdField(triple,field);
		if (innerMap.containsKey(getFieldKey(field,triple))) {
			innerMap.get(getFieldKey(field,triple)).add(key);
		} else {
			ArrayList<KeyContainer> array = new ArrayList<KeyContainer>();
			array.add(key);
			innerMap.put(getFieldKey(field,triple), array);
		}
	}
	
	private Key getFieldKey(Field field,TripleStar triple) {
		if (field == Field.S) {
			return triple.subjectId;
		} else if (field == Field.P) {
			return triple.predicateId;
		} else {
			return triple.objectId;
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

	/**
	 * Returns an iterator, use this when you have a triple pattern with only one variable e.g. SP*
	 * 
	 * @param The key of the inner map.
	 * @param The field of the key
	 * @return An iterator of all triples matching the key
	 */
	public Iterator<KeyContainer> iterator(Key key, Field field) {
		if (innerMap.containsKey(key)) {
			return new IteratorWrapper(innerMap.get(key).iterator(),key,field);
		}
		return Collections.emptyIterator();
	}
	
	/**
	 * Returns an iterator of triples, use this iterator when you have a triple pattern with two variables e.g. S**
	 * @param The position of the second element. This is used to reconstruct the triple in the IteratorWrapper {@link IteratorWrapper}.
	 * @return An iterator of all triples in the TripleBunch.
	 */
	public Iterator<KeyContainer> iterator(Field f) {
		IteratorChain<KeyContainer> chain = new IteratorChain<KeyContainer>();
		for (Entry<Key, ArrayList<KeyContainer>> iterable_element : innerMap.entrySet()) {
			chain.addIterator(new IteratorWrapper(iterable_element.getValue().iterator(),iterable_element.getKey(),f));
		}
		return chain;
	}

	/**
	 * Remove duplicates.
	 * The inner maps contains ArrayLists of keyContainers.
	 * For each arrayList the keycontainers are sorted.
	 * Then the sorted keycontainers are traversed, if two keycontainers are identical, the index key of the latter is added to a list.
	 * In reverse order these are removed from the arrayLists.
	 */
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

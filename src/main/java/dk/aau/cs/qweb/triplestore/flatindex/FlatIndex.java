package dk.aau.cs.qweb.triplestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.iterators.IteratorChain;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.ext.com.google.common.collect.Lists;

import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triple.TripleStar;
import dk.aau.cs.qweb.triple.TripleStarPattern;
import dk.aau.cs.qweb.triplestore.MapIndex.Field;

public class FlatIndex implements Index {

	protected Map<Pair<Key,Key>, ArrayList<KeyContainer>> indexMap;
	protected long size;
	protected Field field1;
	protected Field field2;
	protected Field field3;
	
	protected Key getFieldKey(Field field,TripleStar t) {
		if (field == Field.S) {
			return t.subjectId;
		} else if (field == Field.P) {
			return t.predicateId;
		} else {
			return t.objectId;
		}
	}

	public boolean remove(TripleStar t) {
		throw new NotImplementedException("Index.remove");
	}

	public void clear() {
		indexMap.clear();
		size = 0;
	}

	public long size() {
		return size;
	}

	public boolean isEmpty() {
		return (size == 0 ? true : false);
	}

	public boolean contains(TripleStarPattern t) {
		Pair<Key,Key> key ;
		Key firstKey = t.getField(field1).getKey();
		
		if (t.isFieldConcrete(field2)) {
			key = new ImmutablePair<Key,Key>(firstKey,t.getField(field2).getKey());
		} else {
			key = new ImmutablePair<Key,Key>(firstKey,null);
		}
		
		if (indexMap.containsKey(key)) {
			return indexMap.get(key).contains(new KeyContainer(t.getObject().getKey(),Field.O));
		}
		return false;
	}

	public Iterator<KeyContainer> iterator(TripleStarPattern triple) {
		Pair<Key,Key> key ;
		Key firstKey = triple.getField(field1).getKey();
		
		if (triple.isFieldConcrete(field2)) {
			key = new ImmutablePair<Key,Key>(firstKey,triple.getField(field2).getKey());
		} else {
			key = new ImmutablePair<Key,Key>(firstKey,null);
		}
		
		
		if (indexMap.containsKey(key)) {
			//Zero variables
			if (triple.isFieldConcrete(field3)) {
				throw new IllegalArgumentException("No varialbes found in triple "+triple+", use graph.contains instead");
				//return new AddKeyToIteratorWrapper(indexMap.get(firstKey).iterator(triple.getField(field2).getKey(),triple),firstKey,field1);
			} 
			else {
				if (triple.isFieldConcrete(field2)) {
					Key secondKey = triple.getField(field2).getKey();
					return new IteratorWrapper(indexMap.get(key).iterator(), firstKey, field1, secondKey, field2);
				} else {
					return new IteratorWrapper(indexMap.get(key).iterator(), firstKey, field1);
				}
			}
		}
		return Collections.emptyIterator();
	}

	public void removedOneViaIterator() {
		throw new NotImplementedException("Index.removedOneViaIterator");
	}

	public Iterator<KeyContainer> iterateAll() {
		IteratorChain<KeyContainer> chain = new IteratorChain<KeyContainer>();
		for (Entry<Pair<Key, Key>, ArrayList<KeyContainer>> iterable_element : indexMap.entrySet()) {
			Key firstKey = iterable_element.getKey().getLeft();
			Iterator<KeyContainer> iterator = iterable_element.getValue().iterator();
			if (iterable_element.getKey().getRight() == null) {
				
				chain.addIterator(new IteratorWrapper(iterator,firstKey,field1));
			} else {
				Key secondKey = iterable_element.getKey().getRight();
				chain.addIterator(new IteratorWrapper(iterator,firstKey,field1,secondKey,field2));
			}
		}
		return chain;
	}

	public void eliminateDuplicates() {
		for (Entry<Pair<Key, Key>, ArrayList<KeyContainer>> list : indexMap.entrySet()) {
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
		return indexMap.toString();
	}

	@Override
	public void add(final TripleStar t) {
		Key firstKey = getFieldKey(field1,t);
		Key secondKey = getFieldKey(field2,t);
		Key thirdKey = getFieldKey(field3,t);
		Pair<Key,Key> key = new ImmutablePair<Key,Key>(firstKey,secondKey);
		
		if (indexMap.containsKey(key)) {
			indexMap.get(key).add(new KeyContainer(thirdKey,field3));
		} else {
			ArrayList<KeyContainer> array = new ArrayList<KeyContainer>();
			array.add(new KeyContainer(thirdKey,field3));
			indexMap.put(key, array);
		}
		size++;
	}
}

package dk.aau.cs.qweb.triplestore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.iterators.IteratorChain;
import org.apache.commons.lang3.NotImplementedException;

import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triple.TripleStar;
import dk.aau.cs.qweb.triple.TripleStarPattern;

public class Index   {
	public enum Field {
		 S, P, O, 
		}
	
	private Map<Key,TripleBunch> indexMap;
	private long size;
	private Field field1;
	private Field field2;
	private Field field3;
	
	public Index(Field field1,Field field2,Field field3) {
		this.field1 = field1;
		this.field2 = field2;
		this.field3 = field3;
		indexMap = new HashMap<Key,TripleBunch>();
		size = 0;
	}

	public void add(final TripleStar t) {
		Key firstKey = getFieldKey(field1,t);
		if (indexMap.containsKey(firstKey)) {
			indexMap.get(firstKey).put(getFieldKey(field2,t),t);
		} else {
			TripleBunch tripleBunch = new TripleBunch();
			tripleBunch.put(getFieldKey(field2,t),t);
			indexMap.put(firstKey, tripleBunch);
		}
		size++;
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
		if (indexMap.containsKey(t.getSubject())) {
			TripleBunch map = indexMap.get(t.getSubject());
			if (map.innerMap.containsKey(t.getPredicate())) {
				return map.innerMap.get(t.getPredicate()).contains(new KeyContainer(t.getObject().getKey(),Field.O));
			}
		}
		return false;
	}

	public Iterator<KeyContainer> iterator(TripleStarPattern triple) {
		Key firstKey = triple.getField(field1).getKey();
		if (indexMap.containsKey(firstKey)) {
			//Zero variables
			if (triple.isFieldConcrete(field2) && triple.isFieldConcrete(field3)) {
				return new AddKeyToIteratorWrapper(indexMap.get(firstKey).iterator(triple.getField(field2).getKey(),triple),firstKey,field1);
			} 
			// One variable
			else if (triple.isFieldConcrete(field2)) {
				return new AddKeyToIteratorWrapper(indexMap.get(firstKey).iterator(triple.getField(field2).getKey(),field2),firstKey,field1);
			}
			// Two variables
			else {
				return new AddKeyToIteratorWrapper(indexMap.get(firstKey).iterator(field2),firstKey,field1);
			}
		}
		return Collections.emptyIterator();
	}

	public void removedOneViaIterator() {
		throw new NotImplementedException("Index.removedOneViaIterator");
	}

	public Iterator<KeyContainer> iterateAll() {
		IteratorChain<KeyContainer> chain = new IteratorChain<KeyContainer>();
		for (Entry<Key, TripleBunch> iterable_element : indexMap.entrySet()) {
			chain.addIterator(new AddKeyToIteratorWrapper(iterable_element.getValue().iterator(field2),iterable_element.getKey(),field1));
		}
		return chain;
	}

	public void eliminateDuplicates() {
		for (TripleBunch tripleBunch : indexMap.values()) {
			tripleBunch.eliminateDuplicates();
		}
	}
}

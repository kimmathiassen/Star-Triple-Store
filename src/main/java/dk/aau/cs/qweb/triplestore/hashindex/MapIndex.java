package dk.aau.cs.qweb.triplestore.hashindex;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.iterators.IteratorChain;
import org.apache.commons.lang3.NotImplementedException;

import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triple.TripleStar;
import dk.aau.cs.qweb.triplepattern.TripleStarPattern;
import dk.aau.cs.qweb.triplestore.Index;
import dk.aau.cs.qweb.triplestore.KeyContainer;

/**
 * Class with methods used by index that use implementations of Java Map.
 */
public abstract class MapIndex  implements Index {
	/**
	 * A field is used to denote the position of a element in a triple.
	 */
	public enum Field {
		 S, P, O, 
		}
	
	protected Map<Key, MapTripleBunch> indexMap;
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
		if (indexMap.containsKey(t.getField(field1))) {
			MapTripleBunch map = indexMap.get(t.getField(field1));
			if (map.innerMap.containsKey(t.getField(field2))) {
				return map.innerMap.get(t.getField(field2)).contains(new KeyContainer(t.getField(field3).getKey(),field3));
			}
		}
		return false;
	}

	public Iterator<KeyContainer> iterator(TripleStarPattern triple) {
		Key firstKey = triple.getField(field1).getKey();
		if (indexMap.containsKey(firstKey)) {
			//Zero variables
			if (triple.isFieldConcrete(field2) && triple.isFieldConcrete(field3)) {
				throw new IllegalArgumentException("No varialbes found in triple "+triple+", use graph.contains instead");
				//return new AddKeyToIteratorWrapper(indexMap.get(firstKey).iterator(triple.getField(field2).getKey(),triple),firstKey,field1);
			} 
			// One variable
			else if (triple.isFieldConcrete(field2)) {
				return new IteratorWrapper(indexMap.get(firstKey).iterator(triple.getField(field2).getKey(),field2),firstKey,field1);
			}
			// Two variables
			else {
				return new IteratorWrapper(indexMap.get(firstKey).iterator(field2),firstKey,field1);
			}
		}
		return Collections.emptyIterator();
	}

	public Iterator<KeyContainer> iterateAll() {
		IteratorChain<KeyContainer> chain = new IteratorChain<KeyContainer>();
		for (Entry<Key, MapTripleBunch> iterable_element : indexMap.entrySet()) {
			chain.addIterator(new IteratorWrapper(iterable_element.getValue().iterator(field2),iterable_element.getKey(),field1));
		}
		return chain;
	}

	public int eliminateDuplicates() {
		int count = 0;
		for (MapTripleBunch tripleBunch : indexMap.values()) {
			tripleBunch.eliminateDuplicates();
			count++;
		}
		return count;
	}
	
	@Override
	public String toString() {
		return indexMap.toString();
	}
}

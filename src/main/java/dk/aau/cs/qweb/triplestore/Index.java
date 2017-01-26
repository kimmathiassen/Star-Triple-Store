package dk.aau.cs.qweb.triplestore;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.NotImplementedException;

import dk.aau.cs.qweb.triple.IdTriple;
import dk.aau.cs.qweb.triple.Key;

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

	public void add(IdTriple t) {
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

	private Key getFieldKey(Field field,IdTriple t) {
		if (field == Field.S) {
			return t.getSubject();
		} else if (field == Field.P) {
			return t.getPredicate();
		} else {
			return t.getObject();
		}
	}

	public boolean remove(IdTriple t) {
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

	public boolean containsBySameValueAs(IdTriple t) {
		throw new NotImplementedException("Index.containsBySameValueAs");
	}

	public boolean contains(IdTriple t) {
		throw new NotImplementedException("Index.contains");
	}

	public Iterator<IdTriple> iterator(IdTriple triple) {
		Key firstKey = getFieldKey(field1,triple);
		if (indexMap.containsKey(firstKey)) {
			return indexMap.get(firstKey).iterator(getFieldKey(field2,triple));
		}
		throw new NotImplementedException("no match for "+triple+" an iterator over the empty set should have been returned");
	}

	public void removedOneViaIterator() {
		throw new NotImplementedException("Index.removedOneViaIterator");
	}

	public Iterator<IdTriple> iterateAll() {
		throw new NotImplementedException("Index.iterateAll");
	}

}

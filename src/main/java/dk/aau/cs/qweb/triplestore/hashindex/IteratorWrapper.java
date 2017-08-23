package dk.aau.cs.qweb.triplestore.hashindex;

import java.util.Iterator;

import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triplestore.KeyContainer;
import dk.aau.cs.qweb.triplestore.hashindex.MapIndex.Field;

/**
 * A wrapper class for the iterator returned by the index.
 * Its purpose is to construct the triple when the next method is called.
 * It is used by the mapIndex implementation because of the way triples are stored. 
 * MapIndex {@link MapIndex} contains more details about this.
 */
public class IteratorWrapper implements Iterator<KeyContainer> {
	private Iterator<KeyContainer> iterator;
	private Key key1;
	private Field f1;
	private Key key2;
	private Field f2;

	/**
	 * @param the iterator with the two inner most fields, e.g. P and O.
	 * @param the value of the missing field. e.g 0-0001 
	 * @param the missing field e.g. S.
	 */
	public IteratorWrapper(Iterator<KeyContainer> iterator, Key key,Field f) {
		this.iterator = iterator;
		this.key1 = key;
		this.f1 = f;
	}
	
	/**
	 * @param the iterator with the the inner most field, e.g. O.
	 * @param the value of the first missing field. e.g 0-0002 
	 * @param the first missing field e.g. P.
	 * @param the value of the second missing field. e.g 0-0001 
	 * @param the second missing field e.g. S.
	 */
	public IteratorWrapper(Iterator<KeyContainer> iterator, Key key1,Field f1, Key key2,Field f2) {
		this.iterator = iterator;
		this.key1 = key1;
		this.f1 = f1;
		this.key2 = key2;
		this.f2 = f2;
	}
	
	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public KeyContainer next() {
		KeyContainer kc = new KeyContainer(iterator.next());
		kc.addKey(key1,f1);
		if (key2 != null) {
			kc.addKey(key2,f2);
		}
		return kc;
	}
}

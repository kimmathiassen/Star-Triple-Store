package dk.aau.cs.qweb.triplestore;

import java.util.Iterator;

import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triplestore.hashindex.MapIndex.Field;

public class IteratorWrapper implements Iterator<KeyContainer> {

	private Iterator<KeyContainer> iterator;
	private Key key1;
	private Field f1;
	private Key key2;
	private Field f2;

	public IteratorWrapper(Iterator<KeyContainer> iterator, Key key,Field f) {
		this.iterator = iterator;
		this.key1 = key;
		this.f1 = f;
	}
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

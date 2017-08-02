package dk.aau.cs.qweb.triplestore;

import java.util.Iterator;

import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triplestore.MapIndex.Field;

public class AddKeyToIteratorWrapper implements Iterator<KeyContainer> {

	private Iterator<KeyContainer> iterator;
	private Key key;
	private Field f;

	public AddKeyToIteratorWrapper(Iterator<KeyContainer> iterator, Key key,Field f) {
		this.iterator = iterator;
		this.key = key;
		this.f = f;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public KeyContainer next() {
		KeyContainer kc = new KeyContainer(iterator.next());
		kc.addKey(key,f);
		return kc;
	}
}

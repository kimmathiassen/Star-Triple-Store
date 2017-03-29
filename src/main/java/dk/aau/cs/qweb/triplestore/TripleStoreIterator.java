package dk.aau.cs.qweb.triplestore;

import java.util.Iterator;

import org.apache.jena.graph.Graph;
import org.apache.jena.util.iterator.WrappedIterator;

public class TripleStoreIterator extends WrappedIterator<KeyContainer> {
	
    protected Graph toNotify;
    protected KeyContainer current;

	public TripleStoreIterator(
			Graph toNotify, 
			Iterator<KeyContainer> it
	        ) {
		super( it ); 
        this.toNotify = toNotify;
	}
	
	@Override
    public KeyContainer next() {
        return current = super.next();       
    }
}

package dk.aau.cs.qweb.triplestore;

import java.util.Iterator;

import org.apache.jena.graph.Graph;
import org.apache.jena.util.iterator.WrappedIterator;

import dk.aau.cs.qweb.triple.TripleStar;

public class TripleStoreIterator extends WrappedIterator<TripleStar> {
	
    protected Graph toNotify;
    protected TripleStar current;

	public TripleStoreIterator(
			Graph toNotify, 
			Iterator<TripleStar> it
	        ) {
		super( it ); 
        this.toNotify = toNotify;
	}
	
	@Override
    public TripleStar next() {
        return current = super.next();       
    }
}

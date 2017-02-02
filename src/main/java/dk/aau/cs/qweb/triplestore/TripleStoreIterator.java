package dk.aau.cs.qweb.triplestore;

import java.util.Iterator;

import org.apache.jena.graph.Graph;
import org.apache.jena.util.iterator.WrappedIterator;

import dk.aau.cs.qweb.triple.TripleStar;

public class TripleStoreIterator extends WrappedIterator<TripleStar> {
	
	protected Index X;
    protected Index A;
    protected Index B;
    protected Graph toNotify;
    protected TripleStar current;

	public TripleStoreIterator(
			Graph toNotify, 
			Iterator<TripleStar> it, 
	        Index X, 
	        Index A, 
	        Index B ) {
		super( it ); 
        this.X = X;
        this.A = A; 
        this.B = B; 
        this.toNotify = toNotify;
	}
	

//	@Override 
//	public void remove() {
//	    super.remove();
//	    X.removedOneViaIterator();
//	    A.remove( current );
//	    B.remove( current );
//	    toNotify.getEventManager().notifyDeleteTriple( toNotify, current);
//    }
	
	@Override
    public TripleStar next() {
        return current = super.next();       
    }

}

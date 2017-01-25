package dk.aau.cs.qweb.triplestore;

import java.util.Iterator;

import org.apache.jena.graph.Graph;
import org.apache.jena.util.iterator.WrappedIterator;

import dk.aau.cs.qweb.triple.MyTriple;

public class MyTripleStoreIterator extends WrappedIterator<MyTriple> {
	
	protected Index X;
    protected Index A;
    protected Index B;
    protected Graph toNotify;
    protected MyTriple current;

	public MyTripleStoreIterator(
			Graph toNotify, 
			Iterator<MyTriple> it, 
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
    public MyTriple next() {
        return current = super.next();       
    }

}

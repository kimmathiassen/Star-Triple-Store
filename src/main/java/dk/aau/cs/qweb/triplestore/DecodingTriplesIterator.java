package dk.aau.cs.qweb.triplestore;

import java.util.Iterator;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.util.iterator.NiceIterator;

import dk.aau.cs.qweb.dictionary.MyDictionary;
import dk.aau.cs.qweb.triple.MyTriple;



public class DecodingTriplesIterator extends NiceIterator<Triple>
implements ExtendedIterator<Triple>
{
	// members
	
	final protected MyDictionary nodeDict;
	final protected Iterator<MyTriple> inputIterator;
	
	
	// initialization
	
	public DecodingTriplesIterator (Iterator<MyTriple> inputIterator )
	{
		this.nodeDict = MyDictionary.getInstance();
		this.inputIterator = inputIterator;
	}
	
	
	// implementation of the Iterator interface
	
	@Override
	final public boolean hasNext ()
	{
		return inputIterator.hasNext();
	}
	
	@Override
	final public Triple next ()
	{
		return decode( inputIterator.next() );
	}
	
	@Override
	final public void remove ()
	{
	inputIterator.remove();
	}
	
	
	// operations
	
	public Triple decode ( MyTriple t )
	{
		Node subject = this.nodeDict.getNode(t.getSubject());
		Node predicate = this.nodeDict.getNode(t.getPredicate());
		Node object = this.nodeDict.getNode(t.getObject());
		
		return Triple.create( subject, predicate, object );
	}

}


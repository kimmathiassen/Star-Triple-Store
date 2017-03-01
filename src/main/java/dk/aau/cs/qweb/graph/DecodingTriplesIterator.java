package dk.aau.cs.qweb.graph;

import java.util.Iterator;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.util.iterator.NiceIterator;

import dk.aau.cs.qweb.dictionary.NodeDictionary;
import dk.aau.cs.qweb.triple.TripleStar;



public class DecodingTriplesIterator extends NiceIterator<Triple>
implements ExtendedIterator<Triple>
{
	// members
	
	final protected NodeDictionary nodeDict;
	final protected Iterator<TripleStar> inputIterator;
	
	
	// initialization
	
	public DecodingTriplesIterator (Iterator<TripleStar> inputIterator )
	{
		this.nodeDict = NodeDictionary.getInstance();
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
	
	public Triple decode ( TripleStar t )
	{
		Node subject = this.nodeDict.getNode(t.subjectId);
		Node predicate = this.nodeDict.getNode(t.predicateId);
		Node object = this.nodeDict.getNode(t.objectId);
		
		return Triple.create( subject, predicate, object );
	}

}


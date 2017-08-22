package dk.aau.cs.qweb.graph;

import java.util.Iterator;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.util.iterator.NiceIterator;

import dk.aau.cs.qweb.dictionary.NodeDictionary;
import dk.aau.cs.qweb.dictionary.NodeDictionaryFactory;
import dk.aau.cs.qweb.triplepattern.TripleStarPattern;
import dk.aau.cs.qweb.triplestore.KeyContainer;

public class DecodingTriplesIterator extends NiceIterator<Triple>
implements ExtendedIterator<Triple>
{
	final protected NodeDictionary nodeDict;
	final protected Iterator<KeyContainer> inputIterator;
	final protected TripleStarPattern tripleStarPattern;
	
//	public DecodingTriplesIterator (Iterator<TripleStar> inputIterator )
//	{
//		this.nodeDict = NodeDictionary.getInstance();
//		this.inputIterator = inputIterator;
//	}
	
	public DecodingTriplesIterator(ExtendedIterator<KeyContainer> inputIterator, TripleStarPattern tripleStarPattern) {
		this.tripleStarPattern = tripleStarPattern;
		this.nodeDict = NodeDictionaryFactory.getDictionary();
		this.inputIterator = inputIterator;
	}

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
	
	public Triple decode ( KeyContainer t )
	{
		Node subject;
		Node predicate;
		Node object;
		if (t.containsSubject()) {
			subject = this.nodeDict.getNode(t.getSubject());
		} else {
			subject = this.nodeDict.getNode(tripleStarPattern.getSubject().getKey());
		}

		if (t.containsPredicate()) {
			predicate = this.nodeDict.getNode(t.getPredicate());
		} else {
			predicate = this.nodeDict.getNode(tripleStarPattern.getPredicate().getKey());
		}
		
		if (t.containsObject()) {
			object = this.nodeDict.getNode(t.getObject());
		} else {
			object = this.nodeDict.getNode(tripleStarPattern.getObject().getKey());
		}
		
		return Triple.create( subject, predicate, object );
	}
}


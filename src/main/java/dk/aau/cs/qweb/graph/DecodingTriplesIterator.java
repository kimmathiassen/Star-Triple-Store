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

/**
 * Class for making a Jena ExtendedIterator\<Jena triple\> from a ExtendedIterator\<KeyContainer\>
 *
 */
public class DecodingTriplesIterator extends NiceIterator<Triple>
implements ExtendedIterator<Triple>
{
	final protected NodeDictionary nodeDict;
	final protected Iterator<KeyContainer> inputIterator;
	final protected TripleStarPattern tripleStarPattern;
	
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
	
	/**
	 * Transform a KeyContainer to a Jena Triple (with containing nodes as oppose to keys)
	 * 
	 * @param keyContainer
	 * @return Triple
	 */
	public Triple decode ( KeyContainer keyContainer )
	{
		Node subject;
		Node predicate;
		Node object;
		if (keyContainer.containsSubject()) {
			subject = this.nodeDict.getNode(keyContainer.getSubject());
		} else {
			subject = this.nodeDict.getNode(tripleStarPattern.getSubject().getKey());
		}

		if (keyContainer.containsPredicate()) {
			predicate = this.nodeDict.getNode(keyContainer.getPredicate());
		} else {
			predicate = this.nodeDict.getNode(tripleStarPattern.getPredicate().getKey());
		}
		
		if (keyContainer.containsObject()) {
			object = this.nodeDict.getNode(keyContainer.getObject());
		} else {
			object = this.nodeDict.getNode(tripleStarPattern.getObject().getKey());
		}
		
		return Triple.create( subject, predicate, object );
	}
}


package dk.aau.cs.qweb.graph;
//package dk.aau.cs.qweb.triplestore;
//
//import java.util.Iterator;
//
//import org.apache.jena.graph.Triple;
//import org.apache.jena.util.iterator.ExtendedIterator;
//import org.apache.jena.util.iterator.NiceIterator;
//
//import dk.aau.cs.qweb.dictionary.MyDictionary;
//import dk.aau.cs.qweb.triple.MyTriple;
//
//
//
//public class EncodingTriplesIterator extends NiceIterator<Triple>
//implements ExtendedIterator<Triple>
//{
//	// members
//
//	final protected MyDictionary nodeDict;
//	final protected Iterator<Triple> inputIterator;
//
//
//	// initialization
//
//	public EncodingTriplesIterator ( MyDictionary nodeDict, Iterator<Triple> inputIterator )
//	{
//		this.nodeDict = nodeDict;
//		this.inputIterator = inputIterator;
//	}
//
//
//	// implementation of the Iterator interface
//
//	@Override
//	final public boolean hasNext ()
//	{
//		return inputIterator.hasNext();
//	}
//
//	@Override
//	final public Triple next ()
//	{
//		return encode( nodeDict, inputIterator.next() );
//	}
//
//	@Override
//	final public void remove ()
//	{
//		inputIterator.remove();
//	}
//
//
//	// operations
//
//	static public MyTriple encode ( MyDictionary nodeDict, Triple jenaTriple )
//	{
//		return new MyTriple( nodeDict.createId(jenaTriple.getSubject()),
//		                   nodeDict.createId(jenaTriple.getPredicate()),
//		                   nodeDict.createId(jenaTriple.getObject()) );
//	}
//
//}

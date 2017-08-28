package dk.aau.cs.qweb.queryengine;

import java.util.Iterator;

import org.apache.jena.atlas.lib.Closeable;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.binding.BindingHashMap;
import org.apache.jena.sparql.engine.iterator.QueryIter;

import dk.aau.cs.qweb.dictionary.NodeDictionary;
import dk.aau.cs.qweb.dictionary.NodeDictionaryFactory;
import dk.aau.cs.qweb.dictionary.VarDictionary;
import dk.aau.cs.qweb.triple.Key;



/**
 * Iterator for decoding the solution mappings (bindings)
 *
 */
public class DecodeBindingsIterator extends QueryIter
{
	// members

	/** the input iterator consumed by this one */
	final protected Iterator<SolutionMapping> input;


	// initialization

	public DecodeBindingsIterator ( Iterator<SolutionMapping> input, ExecutionContext execCxt )
	{
		super( execCxt );
		this.input = input;
	}

	// implementation of the QueryIteratorBase abstract methods

	protected boolean hasNextBinding ()
	{
		return input.hasNext();
	}

	protected Binding moveToNextBinding () {
		SolutionMapping curInput = input.next();
		NodeDictionary nodeDict = NodeDictionaryFactory.getDictionary();
		VarDictionary varDict = VarDictionary.getInstance();
		
		BindingHashMap curOutput = new BindingHashMap();
		for ( int i = curInput.size() - 1; i >= 0; i-- )
		{
			if ( curInput.contains(i) ) {
				curOutput.add( varDict.getVar(i),
				               nodeDict.getNode(new Key(curInput.get(i))));
			}
		}
		//System.out.println("decode (solutionMappings -> bindings): "+curOutput);
		return curOutput;
	}

	protected void requestCancel ()
	{
		// Do we have to cancel the (chain of) input iterator(s) ?
		throw new UnsupportedOperationException( "TODO (DecodeBindingsIterator.requestCancel)" );
	}

	protected void closeIterator ()
	{
		if ( input instanceof Closeable ) {
			( (Closeable) input ).close();
		}
	}

}
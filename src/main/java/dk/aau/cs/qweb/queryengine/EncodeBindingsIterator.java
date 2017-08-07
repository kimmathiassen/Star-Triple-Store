package dk.aau.cs.qweb.queryengine;


import java.util.Iterator;

import org.apache.jena.atlas.lib.Closeable;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.binding.Binding;

import dk.aau.cs.qweb.dictionary.HashNodeDictionary;
import dk.aau.cs.qweb.dictionary.VarDictionary;


public class EncodeBindingsIterator implements Iterator<SolutionMapping>, Closeable
{
	/** the input iterator consumed by this one */
	final protected QueryIterator input;

	public EncodeBindingsIterator ( QueryIterator input, ExecutionContext execCxt )
	{
		this.input = input;
	}

	public boolean hasNext ()
	{
		return input.hasNext();
	}

	public SolutionMapping next ()
	{
		Binding curInput = input.next();
		HashNodeDictionary nodeDict = HashNodeDictionary.getInstance();
		VarDictionary varDict = VarDictionary.getInstance();

		SolutionMapping curOutput = new SolutionMapping( varDict.size() );
		Iterator<Var> itVar = curInput.vars();
		while ( itVar.hasNext() )
		{
			Var var = itVar.next();
			curOutput.set( varDict.getId(var),nodeDict.createKey(curInput.get(var)));
		}
		//System.out.println("encode (bindings -> solutionMappings): "+curOutput);
		return curOutput;
	}

	public void remove ()
	{
		throw new UnsupportedOperationException();
	}

	public void close ()
	{
		input.close();
	}
}

package dk.aau.cs.qweb.queryengine;

import java.util.Iterator;

import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.iterator.QueryIterAssign;
import org.apache.jena.sparql.engine.iterator.QueryIterProcessBinding;

import dk.aau.cs.qweb.dictionary.MyDictionary;


public class QueryIterAssignWrapper extends QueryIterProcessBinding
{
	public QueryIterAssignWrapper( QueryIterAssign input, ExecutionContext execCxt )
	{
		super( input, execCxt );
	}

	@Override
	public Binding accept ( Binding b )
	{
		//HEre we ensure that variables of a binding are in the dict
		//Based on olaf code, dont see why this is necessary.
		Iterator<Var> itVar = b.vars();
		while ( itVar.hasNext() ) {
			MyDictionary dict = MyDictionary.getInstance();
			dict.createKey( b.get(itVar.next()) );
		}
		return b;
	}
}

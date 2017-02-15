package dk.aau.cs.qweb.queryengine;

import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.iterator.QueryIterAssign;
import org.apache.jena.sparql.engine.iterator.QueryIterProcessBinding;

@Deprecated
public class QueryIterAssignWrapper extends QueryIterProcessBinding
{
	public QueryIterAssignWrapper( QueryIterAssign input, ExecutionContext execCxt )
	{
		super( input, execCxt );
	}

	@Override
	public Binding accept(Binding binding) {
		return binding;
	}
	
}

package dk.aau.cs.qweb.queryengine;

import java.util.Iterator;

import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.main.OpExecutor;
import org.apache.jena.sparql.engine.main.OpExecutorFactory;

import dk.aau.cs.qweb.graph.Graph;
import dk.aau.cs.qweb.triple.TriplePatternBuilder;
import dk.aau.cs.qweb.triple.TripleStarPattern;


public class OpStarExecutor extends OpExecutor{

	static final public OpExecutorFactory factory = new OpExecutorFactory()
	{
		public OpExecutor create( ExecutionContext execCxt )
		{
			return new OpStarExecutor(execCxt );
		}
	};

	/**
	 * Creates an operator compiler.
	 */
	public OpStarExecutor ( ExecutionContext execCxt )
	{
		super( execCxt );
	}

	// operations
	@Override
	public QueryIterator execute ( OpBGP opBGP, QueryIterator input )
	{
		if (    opBGP.getPattern().isEmpty()
		     || ! (execCxt.getDataset().getDefaultGraph() instanceof Graph) )
		{
			return super.execute( opBGP, input );
		}


		Iterator<SolutionMapping> qIt = new EncodeBindingsIterator( input, execCxt );
		for ( Triple t : opBGP.getPattern().getList() ) {
			qIt = new TriplePatternQueryIter( encode(t), qIt, execCxt );
		}

		return new DecodeBindingsIterator( qIt, execCxt );
	}
	
	// helper methods

	final protected TripleStarPattern encode ( Triple tp) {
		TriplePatternBuilder builder = new TriplePatternBuilder();
		builder.setSubject(tp.getSubject());
		builder.setPredicate(tp.getPredicate());
		builder.setObject(tp.getObject());
		
		return builder.createTriplePatter();
	}
}

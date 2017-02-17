package dk.aau.cs.qweb.queryengine;

import java.util.Iterator;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.op.OpAssign;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpJoin;
import org.apache.jena.sparql.algebra.op.OpTriple;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.join.Join;
import org.apache.jena.sparql.engine.main.OpExecutor;
import org.apache.jena.sparql.engine.main.OpExecutorFactory;

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
		throw new IllegalArgumentException("This iterator should never be called! All BGPs should have been rewritten. opBGP: "+opBGP.toString());
		//TODO this should never happen
//		if (    opBGP.getPattern().isEmpty()
//		     || ! (execCxt.getDataset().getDefaultGraph() instanceof Graph) )
//		{
//			return super.execute( opBGP, input );
//		}
//
//
//		Iterator<SolutionMapping> qIt = new EncodeBindingsIterator( input, execCxt );
//		for ( Triple t : opBGP.getPattern().getList() ) {
//			qIt = new TriplePatternQueryIter( encode(t), qIt, execCxt );
//		}
//
//		return new DecodeBindingsIterator( qIt, execCxt );
	}
	
	@Override
	public QueryIterator execute ( OpTriple opTriple, QueryIterator input )
	{
		Iterator<SolutionMapping> qIt = new EncodeBindingsIterator( input, execCxt );
		qIt = new TriplePatternQueryIter( encode(opTriple.getTriple()), qIt, execCxt );

		return new DecodeBindingsIterator( qIt, execCxt );
	}
	
	@Override
	public QueryIterator execute ( OpAssign opAssign, QueryIterator input )
	{
		throw new NotImplementedException("OpStarExecutor.execute(OpAssign) not implemented ");
	}
	
	@Override
	public QueryIterator execute ( OpJoin opJoin, QueryIterator input )
	{
        // Need to clone input into left and right.
        // Do by evaling for each input case, the left and right and concat'ing
        // the results.

        QueryIterator left = exec(opJoin.getLeft(), input) ;
        QueryIterator right = exec(opJoin.getRight(), root()) ;
        // Join key.
        QueryIterator qIter = Join.join(left, right, execCxt) ;
        return qIter ;
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

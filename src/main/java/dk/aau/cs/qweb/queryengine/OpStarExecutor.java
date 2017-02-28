package dk.aau.cs.qweb.queryengine;

import java.util.Iterator;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpExtend;
import org.apache.jena.sparql.algebra.op.OpJoin;
import org.apache.jena.sparql.algebra.op.OpTriple;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.main.OpExecutor;
import org.apache.jena.sparql.engine.main.OpExecutorFactory;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.nodevalue.NodeValueNode;

import dk.aau.cs.qweb.dictionary.VarDictionary;
import dk.aau.cs.qweb.model.Node_Triple;
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
	protected QueryIterator execute(OpExtend opExtend, QueryIterator input) {
		return new DecodeBindingsIterator(execute((OpExtend)opExtend, new EncodeBindingsIterator( input, execCxt )),execCxt);
    }
	
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
	public QueryIterator execute ( OpTriple opTriple, QueryIterator input )	{
		return new DecodeBindingsIterator(execute((OpTriple)opTriple, new EncodeBindingsIterator( input, execCxt )),execCxt);
	}
	
	@Override
	protected QueryIterator execute(OpJoin opJoin, QueryIterator input) {
		return new DecodeBindingsIterator(execute((OpJoin)opJoin, new EncodeBindingsIterator( input, execCxt )),execCxt);
		
//		Op opLeft = opJoin.getLeft();
//		Op opRight = opJoin.getRight();
//		QueryIterator jenaIterator = input;
//		Iterator<SolutionMapping> leftIter = null;
//		QueryIterator leftBindings = null;
//		
//		if (opLeft instanceof OpTriple) {
//			leftIter = execute((OpTriple)opLeft,new EncodeBindingsIterator( input, execCxt ));
//		} else if (opLeft instanceof OpJoin) {
//			leftIter = execute((OpJoin)opLeft,new EncodeBindingsIterator( input, execCxt ));
//		} else if (opLeft instanceof OpExtend) {
//			leftIter = execute((OpExtend)opLeft,new EncodeBindingsIterator( input, execCxt ));
//		} else {
//			leftBindings = exec(opLeft, input);
//		}
//			
//		if (leftIter != null) {
//			if (opRight instanceof OpTriple) {
//				jenaIterator = new DecodeBindingsIterator(execute((OpTriple)opRight,leftIter), execCxt);
//			} else if (opRight instanceof OpJoin) {
//				jenaIterator = new DecodeBindingsIterator(execute((OpJoin)opRight,leftIter), execCxt);
//			} else if (opRight instanceof OpExtend) {
//				jenaIterator = new DecodeBindingsIterator(execute((OpExtend)opRight,leftIter), execCxt);
//			} 
//		} else {
//			jenaIterator = exec(opRight, leftBindings) ;
//		}
//			
//		
//        return jenaIterator ;
    }
	
	
	private Iterator<SolutionMapping> execute(OpJoin opJoin, Iterator<SolutionMapping> solutionMappingIter) {
		Op opLeft = opJoin.getLeft();
		Op opRight = opJoin.getRight();
		Iterator<SolutionMapping> leftIter = null;
		
		if (opLeft instanceof OpTriple) {
			leftIter = execute((OpTriple)opLeft,solutionMappingIter);
		} else if (opLeft instanceof OpJoin) {
			leftIter = execute((OpJoin)opLeft,solutionMappingIter);
		} else if (opLeft instanceof OpExtend) {
			leftIter = execute((OpExtend)opLeft,solutionMappingIter);
		}  else {
			throw new NotImplementedException("There is no id-based iterator implemented for "+opLeft);
		}
			
		if (opRight instanceof OpTriple) {
			return execute((OpTriple)opRight,leftIter);
		} else if (opRight instanceof OpJoin) {
			return execute((OpJoin)opRight,leftIter);
		} else if (opRight instanceof OpExtend) {
			return execute((OpExtend)opRight,leftIter);
		} else {
			throw new NotImplementedException("There is no id-based iterator implemented for "+opRight);
		}
	}
	
	private Iterator<SolutionMapping> execute(OpExtend opExtend, Iterator<SolutionMapping> solutionMappingIter) {
	
		if (opExtend.getVarExprList().getExprs().values().size() != 1) {
			throw new IllegalStateException("Did not expect "+opExtend+ " to have multiple expressions, this state is not handled");
		}
		
		//This for loop can only run once.
		for (Expr iterable_element : opExtend.getVarExprList().getExprs().values()) {
			if (iterable_element instanceof NodeValueNode) {
				NodeValueNode temp = (NodeValueNode)iterable_element;
				Node_Triple node = (Node_Triple)temp.asNode();
				Var var = opExtend.getVarExprList().getVars().get(0);

				//Note that children of OpExtend are not expected and are not handled
				
				return new ExtendWithEmbeddedTriplePatternQueryIter(encode(var),encode(node) ,solutionMappingIter, execCxt) ;
			} else {
				throw new NotImplementedException("only embedded triple patterns are currently supported. OpStarExecutor.execute()");
				//handle normal bind
			}
		}
		
		throw new IllegalStateException("The opExtend is in an illegalState, it does not seem to contain any expression: "+opExtend.getVarExprList());
	}
	
	private Iterator<SolutionMapping> execute(OpTriple opTriple, Iterator<SolutionMapping> solutionMappingIter) {
		return new TriplePatternQueryIter( encode(opTriple.getTriple()), solutionMappingIter, execCxt );
	}

	// helper methods

	final protected TripleStarPattern encode ( Triple tp) {
		TriplePatternBuilder builder = new TriplePatternBuilder();
		builder.setSubject(tp.getSubject());
		builder.setPredicate(tp.getPredicate());
		builder.setObject(tp.getObject());
		
		return builder.createTriplePatter();
	}
	
	final protected TripleStarPattern encode (Node_Triple tp) {
		TriplePatternBuilder builder = new TriplePatternBuilder();
		builder.setSubject(tp.getSubject());
		builder.setPredicate(tp.getPredicate());
		builder.setObject(tp.getObject());
		
		return builder.createTriplePatter();
	}
	
	final protected int encode (Var var) {
		VarDictionary varDict = VarDictionary.getInstance();
		return varDict.createId(var);
	}
}

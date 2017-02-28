package dk.aau.cs.qweb.queryengine;

import java.util.Iterator;

import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpExtend;
import org.apache.jena.sparql.algebra.op.OpJoin;
import org.apache.jena.sparql.algebra.op.OpTriple;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.iterator.QueryIterAssign;
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
		if (opExtend.getVarExprList().getExprs().values().size() != 1) {
			throw new IllegalStateException("Did not expect "+opExtend+ " to have multiple expressions, this state is not handled");
		}
		
		//This for loop can only run once.
		for (Expr iterable_element : opExtend.getVarExprList().getExprs().values()) {
			if (iterable_element instanceof NodeValueNode) {
				NodeValueNode temp = (NodeValueNode)iterable_element;
				Node_Triple node = (Node_Triple)temp.asNode();
				Var var = opExtend.getVarExprList().getVars().get(0);
//				
//				QueryIterator qIter = exec(opExtend.getSubOp(), input) ;
//				System.out.println(opExtend.getSubOp());
//				if (qIter instanceof DecodeBindingsIterator) {
//					DecodeBindingsIterator qItasdaser = (DecodeBindingsIterator)qIter;
//				}
				
				Iterator<SolutionMapping> qIt = new EncodeBindingsIterator( input, execCxt );
				qIt = new ExtendWithEmbeddedTriplePatternQueryIter(encode(var),encode(node) ,qIt, execCxt) ;
				
				//missing call to children.
				//exec(opExtend.getSubOp(), input) ;
				
				
				return new DecodeBindingsIterator(qIt,execCxt);
			}
		}
		//If contain embedded create custom queryiterator
		//else use defualt.
		
        // We know (parse time checking) the variable is unused so far in
        // the query so we can use QueryIterAssign knowing that it behaves
        // the same as extend. The boolean should only be a check.
        QueryIterator qIter = exec(opExtend.getSubOp(), input) ;
        qIter = new QueryIterAssign(qIter, opExtend.getVarExprList(), execCxt, true) ;
        return qIter ;
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
		Iterator<SolutionMapping> qIt = new EncodeBindingsIterator( input, execCxt );
		qIt = new TriplePatternQueryIter( encode(opTriple.getTriple()), qIt, execCxt );

		return new DecodeBindingsIterator( qIt, execCxt );
	}
	
	@Override
	protected QueryIterator execute(OpJoin opJoin, QueryIterator input) {

	        QueryIterator left = exec(opJoin.getLeft(), input) ;
	        
	        QueryIterator right = exec(opJoin.getRight(), left) ;
	        
	        return right ;
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

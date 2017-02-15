package dk.aau.cs.qweb.queryengine;


import org.apache.jena.query.Query;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpVars;
import org.apache.jena.sparql.algebra.op.OpModifier;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Substitute;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.Plan;
import org.apache.jena.sparql.engine.QueryEngineFactory;
import org.apache.jena.sparql.engine.QueryEngineRegistry;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.iterator.QueryIterRoot;
import org.apache.jena.sparql.engine.iterator.QueryIteratorCheck;
import org.apache.jena.sparql.engine.main.QC;
import org.apache.jena.sparql.engine.main.QueryEngineMain;
import org.apache.jena.sparql.util.Context ;

import dk.aau.cs.qweb.dictionary.VarDictionary;
import dk.aau.cs.qweb.graph.Graph;


public class QueryEngineStar extends QueryEngineMain {
    // Do nothing template for a query engine.  
	final public boolean RECORD_PROVENANCE = false;

	/**
	 * The factory object that creates an {@link QueryEngine}.
	 */
	static final private QueryEngineFactory factory = new QueryEngineFactory()
	{
		public boolean accept ( Query query, DatasetGraph ds, Context cxt ) { return isIdBased( ds ); }

		public boolean accept ( Op op, DatasetGraph ds, Context cxt ) { return isIdBased( ds ); }

		public Plan create ( Query query, DatasetGraph dataset, Binding initialBinding, Context context ) {
			QueryEngineStar engine = new QueryEngineStar( query, dataset, initialBinding, context );
			return engine.getPlan();
		}

		public Plan create ( Op op, DatasetGraph dataset, Binding initialBinding, Context context ) {
			QueryEngineStar engine = new QueryEngineStar( op, dataset, initialBinding, context );
			return engine.getPlan();
		}

		private boolean isIdBased ( DatasetGraph ds ) { return ( ds.getDefaultGraph() instanceof Graph ); }
	};

	/**
	 * Returns a factory that creates an {@link QueryEngine}.
	 */
	static public QueryEngineFactory getFactory () { return factory; }

	/**
	 * Registers this engine so that it can be selected for query execution.
	 */
	static public void register () { QueryEngineRegistry.addFactory( factory ); }

	/**
	 * Unregisters this engine.
	 */
	static public void unregister () { QueryEngineRegistry.removeFactory( factory ); }


	// initialization methods

	public QueryEngineStar ( Op op, DatasetGraph dataset, Binding input, Context context )
	{
		super( op, dataset, input, context );
		registerOpExecutor();
	}

	public QueryEngineStar( Query query, DatasetGraph dataset, Binding input, Context context )
	{
		super( query, dataset, input, context );
		registerOpExecutor();
	}

	private void registerOpExecutor ()
	{
		QC.setFactory( context, OpStarExecutor.factory );
	}


	// operations

	@Override
	public QueryIterator eval ( Op op, DatasetGraph dsg, Binding input, Context context )
	{
		//TODO when is SUBSTITUDE suppose to be true 
		boolean SUBSTITUE = true;
		if ( SUBSTITUE && ! input.isEmpty() ) {
			op = Substitute.substitute( op, input );
		}

		ExecutionContext execCxt = createExecutionContext ( op, dsg, context );
		return createIteratorChain( op, input, execCxt );
	}


	// helpers

	protected ExecutionContext createExecutionContext ( Op op, DatasetGraph dsg, Context contextP )
	{
		initializeVarDictionary( op );
		return new ExecutionContext(        contextP,
		                                    dsg.getDefaultGraph(),
		                                    dsg,
		                                    QC.getFactory(contextP) ) ;
	}

	protected QueryIterator createIteratorChain ( Op op, Binding input, ExecutionContext execCxt )
	{
		QueryIterator qIter1 = QueryIterRoot.create( input, execCxt );
		QueryIterator qIter = QC.execute( op, qIter1, execCxt );
		qIter = QueryIteratorCheck.check( qIter, execCxt ); // check for closed iterators
		return qIter;
	}

	/**
	 * Creates a dictionary of query variables that knows all variables in the
	 * operator tree of which the given operator is root.
	 */
	final protected void initializeVarDictionary ( Op op )
	{
		// We cannot call OpVars.allVars(op) directly because it does not
		// consider all variables in sub-operators of OpProject. Hence,
		// we simply strip the solution modifiers and, thus, call the
		// method for the first operator that is not a solution modifier.
		Op tmp = op;
		while ( tmp instanceof OpModifier ) {
			tmp = ( (OpModifier) tmp ).getSubOp();
		}

		VarDictionary varDict = VarDictionary.getInstance();
		for ( Var v : OpVars.visibleVars(tmp) ) { //This call might need to be changed to FixedVars
			varDict.createId( v );
		}
	}
}

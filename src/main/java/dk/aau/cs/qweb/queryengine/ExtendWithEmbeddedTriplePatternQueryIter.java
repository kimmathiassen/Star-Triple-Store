package dk.aau.cs.qweb.queryengine;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.jena.atlas.lib.Closeable;
import org.apache.jena.sparql.engine.ExecutionContext;

import dk.aau.cs.qweb.graph.Graph;
import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triple.KeyFactory;
import dk.aau.cs.qweb.triple.StarNode;
import dk.aau.cs.qweb.triple.TripleStar;
import dk.aau.cs.qweb.triple.TripleStarPattern;
import dk.aau.cs.qweb.triple.Variable;


public class ExtendWithEmbeddedTriplePatternQueryIter implements Iterator<SolutionMapping>, Closeable
{
	// members

	final protected ExecutionContext execCxt;

	/** the input iterator consumed by this one */
	final protected Iterator<SolutionMapping> input;

	/** the triple pattern matched by this iterator */
	final protected TripleStarPattern tp;

	/** the solution mapping currently consumed from the input iterator */
	protected SolutionMapping currentInputMapping = null;

	/**
	 * The current query pattern is the triple pattern of this iterator
	 * (see {@link #tp} substituted with the bindings provided by the
	 * current solution mapping consumed from the input iterator (ie by
	 * {@link #currentInputMapping}).
	 */
	protected TripleStarPattern currentQueryPattern = null;

	/**
	 * an iterator over all triples that match the current query pattern
	 * (see {@link #currentQueryPattern}) in the queried dataset
	 */
	protected Iterator<? extends TripleStar> currentMatches = null;

	private int var;


	// initialization

	public ExtendWithEmbeddedTriplePatternQueryIter (
			int var, 
			TripleStarPattern tp, 
			Iterator<SolutionMapping> input, 
			ExecutionContext execCxt )
	{
		this.var  = var;
		this.tp = tp;
		this.input = input;
		this.execCxt = execCxt;
	}


	// implementation of the Iterator interface

	public boolean hasNext ()
	{
		while ( currentMatches == null || ! currentMatches.hasNext() )
		{
			if ( ! input.hasNext() ) {
				return false;
			}
			Graph graph = ( (Graph) execCxt.getActiveGraph() );

			currentInputMapping = input.next();
			currentQueryPattern = substitute(var, tp, currentInputMapping );

			currentMatches = (currentQueryPattern == null) ? null : graph.graphBaseFind( currentQueryPattern);
		}
		return true;
	}

	public SolutionMapping next ()
	{
		if ( ! hasNext() ) {
			throw new NoSuchElementException();
		}

		// Create the next solution mapping by i) copying the mapping currently
		// consumed from the input iterator and ii) by binding the variables in
		// the copy corresponding to the currently matching triple (currentMatch).
		TripleStar currentMatch = currentMatches.next();
		//BindingProvenance currentMatchProvenance = execCxt.recordProvenance ? new BindingProvenanceImpl( (TraceableTriple) currentMatch, tp ) : null;
		SolutionMapping result = new SolutionMapping( currentInputMapping );

		if ( !currentQueryPattern.getSubject().isConcreate() ) {
			result.set( currentQueryPattern.getSubject().getVariable().getId(), currentMatch.subjectId );
		}

		if ( !currentQueryPattern.getPredicate().isConcreate() ) {
			result.set( currentQueryPattern.getPredicate().getVariable().getId(), currentMatch.predicateId );
		}

		if ( !currentQueryPattern.getObject().isConcreate() ) {
			result.set( currentQueryPattern.getObject().getVariable().getId(), currentMatch.objectId );
		}
		
		result.set(var,KeyFactory.createKey(currentMatch.subjectId, currentMatch.predicateId ,currentMatch.objectId ));

		return result;
	}

	public void remove ()
	{
		throw new UnsupportedOperationException();
	}


	// implementation of the Closable interface

	public void close ()
	{
		if ( input instanceof Closeable ) {
			( (Closeable) input ).close();
		}
	}


	// helper methods

	/**
	 * Replaces each query variable in the given triple pattern that is bound to
	 * a value in the given solution mapping by this value.
	 */
	static public TripleStarPattern substitute (int var, TripleStarPattern triplePattern, SolutionMapping solutionMapping )
	{
		StarNode sNew, pNew, oNew;
		
		if (!triplePattern.getSubject().isConcreate() )
		{
			int variable = triplePattern.getSubject().getVariable().getId();
			sNew = solutionMapping.contains(variable) ? new Key(solutionMapping.get(variable)) : new Variable(variable);
		} else {
			sNew = triplePattern.getSubject();
		}

		if ( !triplePattern.getPredicate().isConcreate()){
			int variable = triplePattern.getPredicate().getVariable().getId();
			pNew = solutionMapping.contains(variable) ? new Key(solutionMapping.get(variable)) : new Variable(variable);
		} else {
			pNew = triplePattern.getPredicate();
		}

		if ( !triplePattern.getObject().isConcreate()) {
			int variable = triplePattern.getObject().getVariable().getId();
			oNew = solutionMapping.contains(variable) ? new Key(solutionMapping.get(variable)) : new Variable(variable);
		} else {
			oNew = triplePattern.getObject();
		}
		
		TripleStarPattern tp = new TripleStarPattern( sNew, pNew, oNew );
		
		//If there exist bindings for var 
		if (solutionMapping.contains(var)) {
			if (tp.getKey().getId() != solutionMapping.get(var)) {
				return null;
			}
		}

		return tp;
	}
}
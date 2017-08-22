package dk.aau.cs.qweb.queryengine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.jena.atlas.lib.Closeable;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.util.iterator.WrappedIterator;

import dk.aau.cs.qweb.dictionary.NodeDictionary;
import dk.aau.cs.qweb.dictionary.NodeDictionaryFactory;
import dk.aau.cs.qweb.graph.Graph;
import dk.aau.cs.qweb.main.Config;
import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triple.KeyFactory;
import dk.aau.cs.qweb.triple.TripleStar;
import dk.aau.cs.qweb.triplepattern.Element;
import dk.aau.cs.qweb.triplepattern.TripleStarPattern;
import dk.aau.cs.qweb.triplepattern.Variable;
import dk.aau.cs.qweb.triplestore.KeyContainer;


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
	protected Iterator<? extends KeyContainer> currentMatches = null;

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

			if (currentQueryPattern == null) {
				currentMatches = null;
			} else if (currentQueryPattern.isConcrete()) {
				boolean match = graph.graphBaseContains(currentQueryPattern);
				if (match) {
					ArrayList<KeyContainer> list = new ArrayList<KeyContainer>();
					list.add(new KeyContainer(	currentQueryPattern.getSubject().getKey(),
												currentQueryPattern.getPredicate().getKey(),
												currentQueryPattern.getObject().getKey()));
					
					currentMatches = WrappedIterator.create(list.iterator());
				} else {
					currentMatches = WrappedIterator.create(Collections.<KeyContainer>emptyList().iterator());
				}
			}  else {
				currentMatches = graph.graphBaseFind( currentQueryPattern);
			}
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
		KeyContainer keyContainer = currentMatches.next();
		TripleStar currentMatch = createTriple(keyContainer);
		//BindingProvenance currentMatchProvenance = execCxt.recordProvenance ? new BindingProvenanceImpl( (TraceableTriple) currentMatch, tp ) : null;
		SolutionMapping result = new SolutionMapping( currentInputMapping );

		if ( !currentQueryPattern.getSubject().isConcrete() ) {
			result.set( currentQueryPattern.getSubject().getVariable().getId(), currentMatch.subjectId );
		}

		if ( !currentQueryPattern.getPredicate().isConcrete() ) {
			result.set( currentQueryPattern.getPredicate().getVariable().getId(), currentMatch.predicateId );
		}

		if ( !currentQueryPattern.getObject().isConcrete() ) {
			result.set( currentQueryPattern.getObject().getVariable().getId(), currentMatch.objectId );
		}

		result.set(var,createBindKey(currentMatch.subjectId, currentMatch.predicateId, currentMatch.objectId));
		return result;
	}

	private TripleStar createTriple(KeyContainer keyContainer) {
		Key subject;
		if (keyContainer.containsSubject()) {
			subject = keyContainer.getSubject();
		} else {
			subject = tp.getSubject().getKey();
		}
		
		Key predicate;
		if (keyContainer.containsPredicate()) {
			predicate = keyContainer.getPredicate();
		} else {
			predicate = tp.getPredicate().getKey();
		}
		
		Key object;
		if (keyContainer.containsObject()) {
			object = keyContainer.getObject();
		} else {
			object = tp.getObject().getKey();
		}
		return new TripleStar(subject,predicate,object);
	}
	
	public void remove ()
	{
		throw new UnsupportedOperationException();
	}

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
		Element sNew, pNew, oNew;
		
		if (!triplePattern.getSubject().isConcrete() )
		{
			int variable = triplePattern.getSubject().getVariable().getId();
			sNew = solutionMapping.contains(variable) ? new Key(solutionMapping.get(variable)) : new Variable(variable);
		} else {
			sNew = triplePattern.getSubject();
		}

		if ( !triplePattern.getPredicate().isConcrete()){
			int variable = triplePattern.getPredicate().getVariable().getId();
			pNew = solutionMapping.contains(variable) ? new Key(solutionMapping.get(variable)) : new Variable(variable);
		} else {
			pNew = triplePattern.getPredicate();
		}

		if ( !triplePattern.getObject().isConcrete()) {
			int variable = triplePattern.getObject().getVariable().getId();
			oNew = solutionMapping.contains(variable) ? new Key(solutionMapping.get(variable)) : new Variable(variable);
		} else {
			oNew = triplePattern.getObject();
		}
		
		TripleStarPattern tp = new TripleStarPattern( sNew, pNew, oNew );
		
		//If there exist bindings for var 
		if (solutionMapping.contains(var)) {
			if (triplePatternIsConcrete(tp)) {
				Key tpKey = KeyFactory.createKey(tp.getSubject().getKey(), tp.getPredicate().getKey(), tp.getObject().getKey());
				if (tpKey.getId() != solutionMapping.get(var)) {
					//Handles the case where the key of the substituted embedded key does not match the one from the solutionMappings.
					//If these keys does not match then the substitution is not valid, null is interpreted later as no matching triples.
					return null;
				}
			}
			
		} else if (sNew.isConcrete() && pNew.isConcrete() && oNew.isConcrete()) {
			
			solutionMapping.set(var, createBindKey(sNew.getKey(), pNew.getKey(), oNew.getKey()));
		}
		return tp;
	}


	private static Key createBindKey(Key sNew, Key pNew, Key oNew) {
		NodeDictionary nd = NodeDictionaryFactory.getDictionary();
		if (isReferenceTriple(sNew,pNew,oNew)) {
			return nd.getReferernceTripleKey(sNew.getKey(), pNew.getKey(), oNew.getKey());
		} else if (nd.isThereAnySpecialReferenceTripleDistributionConditions()) {
			if (nd.containsReferernceTripleKey(sNew.getKey(), pNew.getKey(), oNew.getKey())) {
				return nd.getReferernceTripleKey(sNew.getKey(), pNew.getKey(), oNew.getKey());
			} else {
				return  KeyFactory.createKey(sNew.getKey(), pNew.getKey(), oNew.getKey());
			}
		} else {
			return KeyFactory.createKey(sNew.getKey(), pNew.getKey(), oNew.getKey());
		}
	}
	
	private static boolean isReferenceTriple(Element s, Element p , Element o) {
		if (s.getKey().getId() > Config.getLargestSubjectId() ||
				p.getKey().getId() > Config.getLargestPredicateId() ||
				o.getKey().getId() > Config.getLargestObjectId()) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean triplePatternIsConcrete(TripleStarPattern tp2) {
		return tp2.getSubject().isConcrete() && tp2.getPredicate().isConcrete() && tp2.getObject().isConcrete();
	}
}
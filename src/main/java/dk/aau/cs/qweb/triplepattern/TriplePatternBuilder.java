package dk.aau.cs.qweb.triplepattern;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.Var;

import dk.aau.cs.qweb.dictionary.NodeDictionary;
import dk.aau.cs.qweb.dictionary.NodeDictionaryFactory;
import dk.aau.cs.qweb.dictionary.VarDictionary;
import dk.aau.cs.qweb.node.EmbeddedNode;

/**
 * Class for creating triple patterns.
 * It is able handle multiple levels of nested triple patterns.
 * 
 * if a subject, predicate, or object is not set, then it considered a variable.
 */
public class TriplePatternBuilder {

	private Element subject;
	private Element predicate;
	private Element object;

	
	public TripleStarPattern createTriplePatter() {
		return new TripleStarPattern(subject, predicate, object);
	}

	public void setSubject(final Node node) {
		if (node.isConcrete()) {
			if (node instanceof EmbeddedNode) {
				this.subject = createEmbeddedTriplePattern((EmbeddedNode)node);
			} else {
				NodeDictionary dict = NodeDictionaryFactory.getDictionary();;
				this.subject = dict.createKey(node);
			}
		} else {
			VarDictionary varDict = VarDictionary.getInstance();
			this.subject = varDict.createVariable((Var)node);
		}
	}

	private TripleStarPattern createEmbeddedTriplePattern(final EmbeddedNode node) {
		
		TriplePatternBuilder builder = new TriplePatternBuilder();
		builder.setSubject(node.getSubject());
		builder.setPredicate(node.getPredicate());
		builder.setObject(node.getObject());
		return builder.createTriplePatter();
	}

	public void setPredicate(final Node node) {
		assert !(node instanceof EmbeddedNode);
		if (node.isConcrete()) {
			NodeDictionary dict = NodeDictionaryFactory.getDictionary();;
			this.predicate = dict.createKey(node);
		} else {
			VarDictionary varDict = VarDictionary.getInstance();
			this.predicate = varDict.createVariable((Var)node);
		}
	}

	public void setObject(final Node node) {
		if (node.isConcrete()) {
			if (node instanceof EmbeddedNode) {
				this.object = createEmbeddedTriplePattern((EmbeddedNode)node);
			} else {
				NodeDictionary dict = NodeDictionaryFactory.getDictionary();;
				this.object = dict.createKey(node);
			}
		} else {
			VarDictionary varDict = VarDictionary.getInstance();
			this.object = varDict.createVariable((Var)node);
		}
	}
}

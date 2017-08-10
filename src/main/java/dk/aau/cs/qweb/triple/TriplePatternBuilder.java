package dk.aau.cs.qweb.triple;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.Var;

import dk.aau.cs.qweb.dictionary.NodeDictionary;
import dk.aau.cs.qweb.dictionary.NodeDictionaryFactory;
import dk.aau.cs.qweb.dictionary.VarDictionary;
import dk.aau.cs.qweb.node.EmbeddedNode;

public class TriplePatternBuilder {

	private StarElement subject;
	private StarElement predicate;
	private StarElement object;

	public TripleStarPattern createTriplePatter() {
		return new TripleStarPattern(subject, predicate, object);
	}

	public void setSubject(final Node node) {
		if (node.isConcrete()) {
			if (node instanceof EmbeddedNode) {
				this.subject = createEmbeddedTriplePattern(node);
			} else {
				NodeDictionary dict = NodeDictionaryFactory.getDictionary();;
				this.subject = dict.createKey(node);
			}
		} else {
			VarDictionary varDict = VarDictionary.getInstance();
			this.subject = varDict.createVariable((Var)node);
		}
	}

	private TripleStarPattern createEmbeddedTriplePattern(final Node node) {
		EmbeddedNode embeddedNode = (EmbeddedNode) node;
		
		TriplePatternBuilder builder = new TriplePatternBuilder();
		builder.setSubject(embeddedNode.getSubject());
		builder.setPredicate(embeddedNode.getPredicate());
		builder.setObject(embeddedNode.getObject());
		return builder.createTriplePatter();
	}

	public void setPredicate(final Node node) {
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
				this.object = createEmbeddedTriplePattern(node);
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

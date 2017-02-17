package dk.aau.cs.qweb.triple;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.Var;

import dk.aau.cs.qweb.dictionary.MyDictionary;
import dk.aau.cs.qweb.dictionary.VarDictionary;
import dk.aau.cs.qweb.model.Node_Triple;

public class TriplePatternBuilder {

	private StarNode subject;
	private StarNode predicate;
	private StarNode object;

	public TripleStarPattern createTriplePatter() {
		return new TripleStarPattern(subject, predicate, object);
	}

	public void setSubject(final Node node) {
		if (node.isConcrete()) {
			if (node instanceof Node_Triple) {
				this.subject = createEmbeddedTriplePattern(node);
			} else {
				MyDictionary dict = MyDictionary.getInstance();
				this.subject = dict.createKey(node);
			}
		} else {
			VarDictionary varDict = VarDictionary.getInstance();
			this.subject = varDict.createVariable((Var)node);
		}
	}

	private TripleStarPattern createEmbeddedTriplePattern(final Node node) {
		Node_Triple embeddedNode = (Node_Triple) node;
		
		TriplePatternBuilder builder = new TriplePatternBuilder();
		builder.setSubject(embeddedNode.getSubject());
		builder.setPredicate(embeddedNode.getPredicate());
		builder.setObject(embeddedNode.getObject());
		return builder.createTriplePatter();
	}

	public void setPredicate(final Node node) {
		if (node.isConcrete()) {
			MyDictionary dict = MyDictionary.getInstance();
			this.predicate = dict.createKey(node);
		} else {
			VarDictionary varDict = VarDictionary.getInstance();
			this.predicate = varDict.createVariable((Var)node);
		}
	}

	public void setObject(final Node node) {
		if (node.isConcrete()) {
			if (node instanceof Node_Triple) {
				this.object = createEmbeddedTriplePattern(node);
			} else {
				MyDictionary dict = MyDictionary.getInstance();
				this.object = dict.createKey(node);
			}
		} else {
			VarDictionary varDict = VarDictionary.getInstance();
			this.object = varDict.createVariable((Var)node);
		}
	}
}

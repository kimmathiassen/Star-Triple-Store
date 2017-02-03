package dk.aau.cs.qweb;

import static org.junit.Assert.assertEquals;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.junit.Test;

import dk.aau.cs.qweb.dictionary.MyDictionary;
import dk.aau.cs.qweb.triple.TripleStar;

public class DictionaryTest {

	@Test
	public void writeAndReadTriple() {
		Node subject = NodeFactory.createURI("http://example.com/product/1");
		Node predicate = NodeFactory.createURI("http://product.com/name");
		Node object = NodeFactory.createLiteral("Eclipse");
		Triple original = Triple.create(subject, predicate, object);
		
		MyDictionary dict = MyDictionary.getInstance();
		TripleStar idTriple = dict.createTriple(original);
		
		Node subjectNode = dict.getNode(idTriple.subjectId);
		Node predicateNode = dict.getNode(idTriple.predicateId);
		Node objectNode = dict.getNode(idTriple.objectId);
		
		assertEquals(subject, subjectNode);
		assertEquals(predicate, predicateNode);
		assertEquals(object, objectNode);
	}

}

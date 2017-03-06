package dk.aau.cs.qweb.queryengine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.junit.Test;

import dk.aau.cs.qweb.dictionary.NodeDictionary;
import dk.aau.cs.qweb.helper.BitHelper;
import dk.aau.cs.qweb.model.NodeFactoryStar;
import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triple.TripleStar;
import dk.aau.cs.qweb.triple.TripleStarBuilder;

public class DictionaryTest {

	@Test
	public void writeAndReadTriple() {
		Node subject = NodeFactory.createURI("http://example.com/product/1");
		Node predicate = NodeFactory.createURI("http://product.com/name");
		Node object = NodeFactory.createLiteral("Eclipse");
		Triple original = Triple.create(subject, predicate, object);
		
		TripleStarBuilder builder = new TripleStarBuilder();
		List<TripleStar> idTriples = builder.createTriple(original);
		TripleStar idTriple = idTriples.get(0);
		
		NodeDictionary dict = NodeDictionary.getInstance();
		Node subjectNode = dict.getNode(idTriple.subjectId);
		Node predicateNode = dict.getNode(idTriple.predicateId);
		Node objectNode = dict.getNode(idTriple.objectId);
		
		assertEquals(subject, subjectNode);
		assertEquals(predicate, predicateNode);
		assertEquals(object, objectNode);
	}
	
	@Test
	public void overflowBitIsSet() {
		Node subject = NodeFactory.createURI("http://example.com/product/1");
		Node predicate = NodeFactory.createURI("http://product.com/name");
		Node object = NodeFactory.createLiteral("Eclipse");
		Node embeddedNode = NodeFactoryStar.createEmbeddedNode(subject, predicate, object);
		
	
		NodeDictionary dict = NodeDictionary.getInstance();
		dict.clear();
		dict.setOverflowDistribution(100);
		
		Key embeddedKey = dict.createKey(embeddedNode);
		
		assertTrue(BitHelper.isOverflownEmbeddedTriple(embeddedKey));
	}
	
	@Test
	public void embeddedTripleBitIsSet() {
		Node subject = NodeFactory.createURI("http://example.com/product/1");
		Node predicate = NodeFactory.createURI("http://product.com/name");
		Node object = NodeFactory.createLiteral("Eclipse");
		Node embeddedNode = NodeFactoryStar.createEmbeddedNode(subject, predicate, object);
		
		NodeDictionary dict = NodeDictionary.getInstance();
		dict.clear();
		dict.setOverflowDistribution(0);
		
		Key embeddedKey = dict.createKey(embeddedNode);
		
		assertTrue(BitHelper.isIdAnEmbeddedTriple(embeddedKey));
	}
	
	@Test
	public void countUniqueEmbeddedNodes() {
		Node subject = NodeFactory.createURI("http://example.com/product/1");
		Node predicate = NodeFactory.createURI("http://product.com/name");
		Node object = NodeFactory.createLiteral("Eclipse");
		Node embeddedNode = NodeFactoryStar.createEmbeddedNode(subject, predicate, object);
		
		Node subject1 = NodeFactory.createURI("http://example.com/product/2");
		Node embeddedNode1 = NodeFactoryStar.createEmbeddedNode(subject1, predicate, object);
		
		Node subject2 = NodeFactory.createURI("http://example.com/product/3");
		Node embeddedNode2 = NodeFactoryStar.createEmbeddedNode(subject2, predicate, object);
		
		Node subject3 = NodeFactory.createURI("http://example.com/product/1");
		Node embeddedNode3 = NodeFactoryStar.createEmbeddedNode(subject3, predicate, object);
		
	
		NodeDictionary dict = NodeDictionary.getInstance();
		dict.clear();
		dict.setOverflowDistribution(100);
		
		dict.createKey(embeddedNode);
		dict.createKey(embeddedNode1);
		dict.createKey(embeddedNode2);
		dict.createKey(embeddedNode3);
		
		
		assertEquals(3, dict.getNumberOfEmbeddedTriples());
	}
	
	@Test
	public void customDistribution() {
		Node subject = NodeFactory.createURI("http://example.com/product/1");
		Node predicate = NodeFactory.createURI("http://product.com/name");
		Node object = NodeFactory.createLiteral("Eclipse");
		Node embeddedNode = NodeFactoryStar.createEmbeddedNode(subject, predicate, object);
		
		Node subject1 = NodeFactory.createURI("http://example.com/product/2");
		Node embeddedNode1 = NodeFactoryStar.createEmbeddedNode(subject1, predicate, object);
		
		Node subject2 = NodeFactory.createURI("http://example.com/product/3");
		Node embeddedNode2 = NodeFactoryStar.createEmbeddedNode(subject2, predicate, object);
		
		Node subject3 = NodeFactory.createURI("http://example.com/product/1");
		Node embeddedNode3 = NodeFactoryStar.createEmbeddedNode(subject3, predicate, object);
		
		Node subject4 = NodeFactory.createURI("http://example.com/product/41");
		Node embeddedNode4 = NodeFactoryStar.createEmbeddedNode(subject4, predicate, object);
		
		
		NodeDictionary dict = NodeDictionary.getInstance();
		dict.clear();
		dict.setOverflowDistribution(50);
		
		dict.createKey(embeddedNode);
		dict.createKey(embeddedNode1);
		dict.createKey(embeddedNode2);
		dict.createKey(embeddedNode3);
		dict.createKey(embeddedNode4);
		
		assertEquals(2, dict.getNumberOfOverflowNodes());
	}
}

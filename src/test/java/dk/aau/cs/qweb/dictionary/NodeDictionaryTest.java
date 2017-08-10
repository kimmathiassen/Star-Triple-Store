package dk.aau.cs.qweb.dictionary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dk.aau.cs.qweb.helper.BitHelper;
import dk.aau.cs.qweb.node.NodeFactoryStar;
import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triple.TripleStar;
import dk.aau.cs.qweb.triple.TripleStarBuilder;

public class NodeDictionaryTest {

	@Before
	public void setUp() {
		NodeDictionaryFactory.getDictionary().open();
	}
	
	@After
	public void tearDown() {
		PrefixDictionary.getInstance().clear();
		NodeDictionaryFactory.getDictionary().clear();
		NodeDictionaryFactory.getDictionary().close();
	}
	
	@Test
	public void writeAndReadTriple() {
		Node subject = NodeFactoryStar.createSimpleURINode("http://example.com/product/1");
		Node predicate = NodeFactoryStar.createSimpleURINode("http://product.com/name");
		Node object = NodeFactoryStar.createSimpleLiteralNode("Eclipse");
		Triple original = Triple.create(subject, predicate, object);
		
		TripleStarBuilder builder = new TripleStarBuilder();
		List<TripleStar> idTriples = builder.createTriple(original);
		TripleStar idTriple = idTriples.get(0);
		
		NodeDictionary dict = NodeDictionaryFactory.getDictionary();
		Node subjectNode = dict.getNode(idTriple.subjectId);
		Node predicateNode = dict.getNode(idTriple.predicateId);
		Node objectNode = dict.getNode(idTriple.objectId);
		
		assertEquals(subject, subjectNode);
		assertEquals(predicate, predicateNode);
		assertEquals(object, objectNode);
	}
	
	@Test
	public void overflowBitIsSet() {
		Node subject = NodeFactoryStar.createSimpleURINode("http://example.com/product/1");
		Node predicate = NodeFactoryStar.createSimpleURINode("http://product.com/name");
		Node object = NodeFactoryStar.createSimpleLiteralNode("Eclipse");
		Node embeddedNode = NodeFactoryStar.createEmbeddedNode(subject, predicate, object);
	
		NodeDictionary dict = NodeDictionaryFactory.getDictionary();
		dict.clear();
		dict.setReferenceTripleDistribution(100);
		
		Key embeddedKey = dict.createKey(embeddedNode);
		
		assertTrue(BitHelper.isReferenceBitSet(embeddedKey));
	}
	
	@Test
	public void embeddedTripleBitIsSet() {
		Node subject = NodeFactoryStar.createSimpleURINode("http://example.com/product/1");
		Node predicate = NodeFactoryStar.createSimpleURINode("http://product.com/name");
		Node object = NodeFactoryStar.createSimpleLiteralNode("Eclipse");
		Node embeddedNode = NodeFactoryStar.createEmbeddedNode(subject, predicate, object);
		
		NodeDictionary dict = NodeDictionaryFactory.getDictionary();
		dict.clear();
		dict.setReferenceTripleDistribution(0);
		
		Key embeddedKey = dict.createKey(embeddedNode);
		
		assertTrue(BitHelper.isIdAnEmbeddedTriple(embeddedKey));
	}
	
	@Test
	public void countUniqueEmbeddedReferenceNodes() {
		Node subject = NodeFactoryStar.createSimpleURINode("http://example.com/product/1");
		Node predicate = NodeFactoryStar.createSimpleURINode("http://product.com/name");
		Node object = NodeFactoryStar.createSimpleLiteralNode("Eclipse");
		Node embeddedNode = NodeFactoryStar.createEmbeddedNode(subject, predicate, object);
		
		Node subject1 = NodeFactoryStar.createSimpleURINode("http://example.com/product/2");
		Node embeddedNode1 = NodeFactoryStar.createEmbeddedNode(subject1, predicate, object);
		
		Node subject2 = NodeFactoryStar.createSimpleURINode("http://example.com/product/3");
		Node embeddedNode2 = NodeFactoryStar.createEmbeddedNode(subject2, predicate, object);
		
		Node subject3 = NodeFactoryStar.createSimpleURINode("http://example.com/product/1");
		Node embeddedNode3 = NodeFactoryStar.createEmbeddedNode(subject3, predicate, object);
		
	
		NodeDictionary dict = NodeDictionaryFactory.getDictionary();
		dict.clear();
		dict.setReferenceTripleDistribution(100);
		
		dict.createKey(embeddedNode);
		dict.createKey(embeddedNode1);
		dict.createKey(embeddedNode2);
		dict.createKey(embeddedNode3);
		
		assertEquals(3, dict.getNumberOfReferenceTriples());
		assertEquals(3, dict.getNumberOfEmbeddedTriples());
	}
	
	@Test
	public void countEmbeddedNodes() {
		Node subject = NodeFactoryStar.createSimpleURINode("http://example.com/product/1");
		Node predicate = NodeFactoryStar.createSimpleURINode("http://product.com/name");
		Node object = NodeFactoryStar.createSimpleLiteralNode("Eclipse");
		Node embeddedNode = NodeFactoryStar.createEmbeddedNode(subject, predicate, object);
		
		Node subject1 = NodeFactoryStar.createSimpleURINode("http://example.com/product/2");
		Node embeddedNode1 = NodeFactoryStar.createEmbeddedNode(subject1, predicate, object);
		
		Node subject2 = NodeFactoryStar.createSimpleURINode("http://example.com/product/3");
		Node embeddedNode2 = NodeFactoryStar.createEmbeddedNode(subject2, predicate, object);
		
		Node subject3 = NodeFactoryStar.createSimpleURINode("http://example.com/product/1");
		Node embeddedNode3 = NodeFactoryStar.createEmbeddedNode(subject3, predicate, object);
		
	
		NodeDictionary dict = NodeDictionaryFactory.getDictionary();
		dict.clear();
		dict.setReferenceTripleDistribution(0);
		
		dict.createKey(embeddedNode);
		dict.createKey(embeddedNode1);
		dict.createKey(embeddedNode2);
		dict.createKey(embeddedNode3);
		
		//Duplicates are counted
		assertEquals(0, dict.getNumberOfReferenceTriples());
		assertEquals(4, dict.getNumberOfEmbeddedTriples());
	}
	
	@Test
	public void customDistribution() {
		Node subject = NodeFactoryStar.createSimpleURINode("http://example.com/product/1");
		Node predicate = NodeFactoryStar.createSimpleURINode("http://product.com/name");
		Node object = NodeFactoryStar.createSimpleLiteralNode("Eclipse");
		Node embeddedNode = NodeFactoryStar.createEmbeddedNode(subject, predicate, object);
		
		Node subject1 = NodeFactoryStar.createSimpleURINode("http://example.com/product/2");
		Node embeddedNode1 = NodeFactoryStar.createEmbeddedNode(subject1, predicate, object);
		
		Node subject2 = NodeFactoryStar.createSimpleURINode("http://example.com/product/3");
		Node embeddedNode2 = NodeFactoryStar.createEmbeddedNode(subject2, predicate, object);
		
		Node subject3 = NodeFactoryStar.createSimpleURINode("http://example.com/product/661");
		Node embeddedNode3 = NodeFactoryStar.createEmbeddedNode(subject3, predicate, object);
		
		Node subject4 = NodeFactoryStar.createSimpleURINode("http://example.com/product/41");
		Node embeddedNode4 = NodeFactoryStar.createEmbeddedNode(subject4, predicate, object);
		
		Node embeddedNode5 = NodeFactoryStar.createEmbeddedNode(subject4, predicate, object);
		
		Node subject6 = NodeFactoryStar.createSimpleURINode("http://example.com/product/33");
		Node embeddedNode6 = NodeFactoryStar.createEmbeddedNode(subject6, predicate, object);
		
		
		NodeDictionary dict = NodeDictionaryFactory.getDictionary();
		dict.clear();
		dict.setReferenceTripleDistribution(50);
		
		dict.createKey(embeddedNode);
		dict.createKey(embeddedNode1);
		dict.createKey(embeddedNode2);
		dict.createKey(embeddedNode3);
		dict.createKey(embeddedNode4);
		dict.createKey(embeddedNode5);
		dict.createKey(embeddedNode6);
		
		assertEquals(3, dict.getNumberOfReferenceTriples());
	}
}

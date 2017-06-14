package dk.aau.cs.qweb.queryengine;

import static org.junit.Assert.assertEquals;

import org.apache.jena.graph.Node;
import org.junit.Test;

import dk.aau.cs.qweb.dictionary.NodeDictionary;
import dk.aau.cs.qweb.dictionary.PrefixDictionary;
import dk.aau.cs.qweb.node.NodeFactoryStar;

public class PrefixDictionaryTest {

	
	
	@Test
	public void multipleCommonPrefixes() {
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
		
		
		NodeDictionary dict = NodeDictionary.getInstance();
		dict.clear();
		
		dict.createKey(embeddedNode);
		dict.createKey(embeddedNode1);
		dict.createKey(embeddedNode2);
		dict.createKey(embeddedNode3);
		dict.createKey(embeddedNode4);
		

		
		assertEquals(2, PrefixDictionary.getInstance().size());
	}
}

package dk.aau.cs.qweb.dictionary;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.jena.graph.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dk.aau.cs.qweb.main.Config;
import dk.aau.cs.qweb.node.NodeFactoryStar;
import dk.aau.cs.qweb.node.SimpleURINode;
import dk.aau.cs.qweb.triple.Key;

public class PrefixDictionaryTest {

	@Before
	public void setup() {
		NodeDictionaryFactory.getDictionary().open();
		
	}
	
	
	@After
	public void tearDown() throws IOException {
		PrefixDictionary.getInstance().clear();
		HashNodeDictionary.getInstance().clear();
		NodeDictionaryFactory.getDictionary().close();
		Config.enablePrefixDictionary(true);
	}
	
	@Test
	public void multipleCommonPrefixes() throws IOException {
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
		
		
		HashNodeDictionary dict = HashNodeDictionary.getInstance();
		dict.clear();
		
		dict.createKey(embeddedNode);
		dict.createKey(embeddedNode1);
		dict.createKey(embeddedNode2);
		dict.createKey(embeddedNode3);
		dict.createKey(embeddedNode4);
		

		
		assertEquals(2, PrefixDictionary.getInstance().size());
	}
	
	@Test
	public void onePrefix() throws IOException {
		Node subject = NodeFactoryStar.createSimpleURINode("http://example.com/product1");
		Node predicate = NodeFactoryStar.createSimpleURINode("http://example.com/name");
		Node object = NodeFactoryStar.createSimpleLiteralNode("Eclipse");
		Node embeddedNode = NodeFactoryStar.createEmbeddedNode(subject, predicate, object);
		
		HashNodeDictionary dict = HashNodeDictionary.getInstance();
		dict.clear();
		
		dict.createKey(embeddedNode);
		
		assertEquals(1, PrefixDictionary.getInstance().size());
	}
	
	@Test
	public void multipleDistinctPrefixes() throws IOException {
		Node subject = NodeFactoryStar.createSimpleURINode("http://example.com/product/1");
		Node predicate = NodeFactoryStar.createSimpleURINode("http://product.com/name");
		Node object = NodeFactoryStar.createSimpleLiteralNode("Eclipse");
		Node embeddedNode = NodeFactoryStar.createEmbeddedNode(subject, predicate, object);
		
		Node subject1 = NodeFactoryStar.createSimpleURINode("http://example.dk/product/2");
		Node embeddedNode1 = NodeFactoryStar.createEmbeddedNode(subject1, predicate, object);
		
		Node subject2 = NodeFactoryStar.createSimpleURINode("http://example.uk/product/3");
		Node embeddedNode2 = NodeFactoryStar.createEmbeddedNode(subject2, predicate, object);
		
		Node subject3 = NodeFactoryStar.createSimpleURINode("http://example.ad/product/661");
		Node embeddedNode3 = NodeFactoryStar.createEmbeddedNode(subject3, predicate, object);
		
		Node subject4 = NodeFactoryStar.createSimpleURINode("http://example.fgg/product/41");
		Node embeddedNode4 = NodeFactoryStar.createEmbeddedNode(subject4, predicate, object);
		
		HashNodeDictionary dict = HashNodeDictionary.getInstance();
		dict.clear();
		
		dict.createKey(embeddedNode);
		dict.createKey(embeddedNode1);
		dict.createKey(embeddedNode2);
		dict.createKey(embeddedNode3);
		dict.createKey(embeddedNode4);
		
		assertEquals(6, PrefixDictionary.getInstance().size());
	}
	
	@Test
	public void simpleURIs() throws IOException {
		Node subject = NodeFactoryStar.createSimpleURINode("http://example.com/product/1");
		
		HashNodeDictionary dict = HashNodeDictionary.getInstance();
		dict.clear();
		
		Key key = dict.createKey(subject);
		Node sub =  dict.getNode(key);

		assertEquals(sub.getURI(), "http://example.com/product/1");
	}
	
	@Test
	public void disablePrefixDictionary() throws IOException {
		Config.enablePrefixDictionary(false);
		Node subject = NodeFactoryStar.createSimpleURINode("http://example.com/product/1");
		
		HashNodeDictionary dict = HashNodeDictionary.getInstance();
		dict.clear();
		
		assertEquals(false,((SimpleURINode)subject).hasPrefix());
		assertEquals(0, PrefixDictionary.getInstance().size());
	}
	
	@Test
	public void prefixIdWithSlashEnding() {
		Node subject = NodeFactoryStar.createSimpleURINode("http://example.com/product/1");
		int prefixId = ((SimpleURINode)subject).getPrefixId();
		
		String prefix = PrefixDictionary.getInstance().getPrefix(prefixId);
		
		assertEquals(true,((SimpleURINode)subject).hasPrefix());
		assertEquals(1, PrefixDictionary.getInstance().size());
		assertEquals("http://example.com/product/", prefix);
	}
	
	@Test
	public void prefixIdWithHashtagEnding() {
		Node subject = NodeFactoryStar.createSimpleURINode("http://example.com#stuff");
		int prefixId = ((SimpleURINode)subject).getPrefixId();
		
		String prefix = PrefixDictionary.getInstance().getPrefix(prefixId);
		
		assertEquals(true,((SimpleURINode)subject).hasPrefix());
		assertEquals(1, PrefixDictionary.getInstance().size());
		assertEquals("http://example.com", prefix);
	}
	
	@Test
	public void prefixIdWithSlashHashtagEnding() {
		Node subject = NodeFactoryStar.createSimpleURINode("http://example.com/product#boom");
		int prefixId = ((SimpleURINode)subject).getPrefixId();
		
		String prefix = PrefixDictionary.getInstance().getPrefix(prefixId);
		
		assertEquals(true,((SimpleURINode)subject).hasPrefix());
		assertEquals(1, PrefixDictionary.getInstance().size());
		assertEquals("http://example.com/product", prefix);
	}
	
	@Test
	public void uriWithNoValidPrefix() {
		Node subject = NodeFactoryStar.createSimpleURINode("boom");
		
		assertEquals(false,((SimpleURINode)subject).hasPrefix());
		assertEquals(0,PrefixDictionary.getInstance().size());
	}
}

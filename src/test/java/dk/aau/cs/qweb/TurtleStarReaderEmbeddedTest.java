package dk.aau.cs.qweb;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.BeforeClass;
import org.junit.Test;

import dk.aau.cs.qweb.graph.Graph;
import dk.aau.cs.qweb.main.App;
import dk.aau.cs.qweb.model.NodeFactoryStar;

public class TurtleStarReaderEmbeddedTest {

	static Graph g;
	
	@BeforeClass
	public static void setup() {
		g = new Graph();
		String filename = "src/test/resources/TurtleStar/embedded.ttls" ;

        App.registerTTLS();
    	
        RDFDataMgr.read(g, filename);
	}
	
	@Test
	public void subjectEmbeddedNode() {
		Node s1 = NodeFactory.createURI("http://example.org/kim");
		Node p1 = NodeFactory.createURI("http://example.org/worksAt"); 
		Node o1 = NodeFactory.createURI("http://example.org/aau");
		Node subject = NodeFactoryStar.createEmbeddedNode(s1, p1, o1);
		Node predicate = Node.ANY;
		Node object = Node.ANY;
		
		Triple triplePattern = new Triple(subject,predicate,object );
		
		Iterator<Triple> iterator = g.graphBaseFind(triplePattern);
		int count = 0;
		
		while (iterator.hasNext()) {
			iterator.next();
			count++;
		}
		
		assertEquals(2,count);
	}
	
	@Test
	public void subjectEmbeddedNodePredicate() {
		Node s1 = NodeFactory.createURI("http://example.org/kim");
		Node p1 = NodeFactory.createURI("http://example.org/worksAt"); 
		Node o1 = NodeFactory.createURI("http://example.org/aau");
		Node subject = NodeFactoryStar.createEmbeddedNode(s1, p1, o1);
		Node predicate = NodeFactory.createURI("http://example.org/is");
		Node object = Node.ANY;
		
		Triple triplePattern = new Triple(subject,predicate,object );
		
		Iterator<Triple> iterator = g.graphBaseFind(triplePattern);
		int count = 0;
		
		while (iterator.hasNext()) {
			iterator.next();
			count++;
		}
		
		assertEquals(1,count);
	}
	
	@Test
	public void findEmbeddedNode() {
		
		Node subject = Node.ANY;
		Node predicate = NodeFactory.createURI("http://example.org/is");
		Node object = NodeFactory.createLiteral("Not a lie");;
		
		Triple triplePattern = new Triple(subject,predicate,object );
		
		Iterator<Triple> iterator = g.graphBaseFind(triplePattern);
		int count = 0;
		
		while (iterator.hasNext()) {
			iterator.next();
			count++;
		}
		
		assertEquals(1,count);
	}
	
	@Test
	public void embeddedTriplesAreAddedAsNormalTriples() {
		Node s1 = NodeFactory.createURI("http://example.org/kim");
		Node p1 = NodeFactory.createURI("http://example.org/worksAt"); 
		Node o1 = NodeFactory.createURI("http://example.org/aau");
		
		Triple triplePattern = new Triple(s1,p1,o1 );
		
		Iterator<Triple> iterator = g.graphBaseFind(triplePattern);
		int count = 0;
		
		while (iterator.hasNext()) {
			iterator.next();
			count++;
		}
		
		assertEquals(1,count);
	}
}

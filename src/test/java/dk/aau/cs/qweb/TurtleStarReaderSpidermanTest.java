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

public class TurtleStarReaderSpidermanTest {
	static Graph g;

	@BeforeClass
	public static void setup() {
		g = new Graph();
		String filename = "src/test/resources/TurtleStar/spiderman.ttls" ;

        App.registerTTLS();
    	
        RDFDataMgr.read(g, filename);
	}
	

	@Test
	public void commaSeperatedTriples() {
		Node subject = NodeFactory.createURI("http://example.org/spiderman");
		Node predicate = NodeFactory.createURI("http://xmlns.com/foaf/0.1/name");
		Node object = Node.ANY;
		
		Triple triplePattern = new Triple(subject,predicate,object );
		
		Iterator<Triple> iterator = g.graphBaseFind(triplePattern);
		int count = 0;
		
		while (iterator.hasNext()) {
			iterator.next();
			count++;
		}
		
		assertEquals(count,2);
	}
	
	@Test
	public void standardTriplePattern() {
		Node subject = NodeFactory.createURI("http://example.org/spiderman");
		Node predicate = NodeFactory.createURI("http://www.perceive.net/schemas/relationship/enemyOf");
		Node object = Node.ANY;
		
		Triple triplePattern = new Triple(subject,predicate,object );
		
		Iterator<Triple> iterator = g.graphBaseFind(triplePattern);
		int count = 0;
		
		while (iterator.hasNext()) {
			iterator.next();
			count++;
		}
		
		assertEquals(count,1);
	}
	
	@Test
	public void linesWithComments() {
		Node subject = Node.ANY;
		Node predicate = NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		Node object = NodeFactory.createURI("http://xmlns.com/foaf/0.1/person");
		
		Triple triplePattern = new Triple(subject,predicate,object );
		
		Iterator<Triple> iterator = g.graphBaseFind(triplePattern);
		int count = 0;
		
		while (iterator.hasNext()) {
			iterator.next();
			count++;
		}
		
		assertEquals(count,2);
	}

}

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

public class TurtleStarReaderBaseTest {

	static Graph g;

	@BeforeClass
	public static void setup() {
		g = new Graph();
		String filename = "src/test/resources/TurtleStar/base.ttls" ;

        App.registerTTLS();
    	
        RDFDataMgr.read(g, filename);
	}
	

	@Test
	public void baseDirectiveUsed() {
		//This does not work for some reason.
		//When an IRI is converted from token to Node the default (base) namespace is not added.
		
		Node subject = Node.ANY;
		Node predicate = NodeFactory.createURI("http://example.org/title");
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
}

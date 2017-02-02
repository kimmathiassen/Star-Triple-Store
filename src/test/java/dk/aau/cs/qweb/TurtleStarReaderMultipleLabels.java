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

public class TurtleStarReaderMultipleLabels {

	static Graph g;

	@BeforeClass
	public static void setup() {
		g = new Graph();
		String filename = "src/test/resources/TurtleStar/multipleLabels.ttls" ;

        App.registerTTLS();
    	
        RDFDataMgr.read(g, filename);
	}
	

	@Test
	public void multipleLabel() {
		// Currently the dictionary do not distinguish between literals with different types and languages.
		Node subject = NodeFactory.createURI("http://example.org/vocab/show/218");
		Node predicate = NodeFactory.createURI("http://www.w3.org/2000/01/rdf-schema#label");
		Node object = Node.ANY;
		
		Triple triplePattern = new Triple(subject,predicate,object );
		
		Iterator<Triple> iterator = g.graphBaseFind(triplePattern);
		int count = 0;
		
		while (iterator.hasNext()) {
			iterator.next();
			count++;
		}
		
		assertEquals(3,count);
	}
	

}

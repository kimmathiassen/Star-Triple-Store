package dk.aau.cs.qweb;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.junit.BeforeClass;
import org.junit.Test;

import dk.aau.cs.qweb.graph.Graph;
import dk.aau.cs.qweb.input.TurtleStarReader;
import dk.aau.cs.qweb.model.NodeFactoryStar;

public class TurtleStarReaderEmbeddedTest {

	static Graph g;
	
	@BeforeClass
	public static void setup() {
		g = new Graph();
		TurtleStarReader reader = new TurtleStarReader(g);
		
		File spiderman = new File("src/test/resources/TurtleStar/embedded.ttls");
		try {
			reader.read(spiderman);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void blankNodeObject() {
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
		
		assertEquals(count,1);
	}
	
}

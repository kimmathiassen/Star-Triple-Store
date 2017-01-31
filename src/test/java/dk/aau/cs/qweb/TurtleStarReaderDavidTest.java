package dk.aau.cs.qweb;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import dk.aau.cs.qweb.graph.Graph;
import dk.aau.cs.qweb.input.TurtleStarReader;

public class TurtleStarReaderDavidTest {

	static Graph g;

	@BeforeClass
	public static void setup() {
		g = new Graph();
        Model model = ModelFactory.createModelForGraph(g);
		TurtleStarReader reader = new TurtleStarReader(model);
		
		File spiderman = new File("src/test/resources/TurtleStar/david.ttls");
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
		Node subject = NodeFactory.createURI("http://www.w3.org/TR/rdf-syntax-grammar");
		Node predicate = NodeFactory.createURI("http://example.org/stuff/1.0/editor");
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
	public void blankNodeSubject() {
		Node subject = Node.ANY;
		Node predicate = NodeFactory.createURI("http://example.org/stuff/1.0/fullname");
		Node object = NodeFactory.createLiteral("Dave Beckett");
		
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

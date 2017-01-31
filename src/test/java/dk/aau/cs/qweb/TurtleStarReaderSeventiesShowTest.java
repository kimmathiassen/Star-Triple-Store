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

public class TurtleStarReaderSeventiesShowTest {

	static Graph g;

	@BeforeClass
	public static void setup() {
		g = new Graph();
        Model model = ModelFactory.createModelForGraph(g);
		TurtleStarReader reader = new TurtleStarReader(model);
		
		File spiderman = new File("src/test/resources/TurtleStar/seventiesShow.ttls");
		try {
			reader.read(spiderman);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	@Test
	public void multiplesLabes() {
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
		
		assertEquals(count,3);
	}
	
	@Test
	public void multiplesLanguages() {
		Node subject = NodeFactory.createURI("http://example.org/vocab/show/218");
		Node predicate = NodeFactory.createURI("http://example.org/vocab/show/localName");
		Node object = Node.ANY;
		
		Triple triplePattern = new Triple(subject,predicate,object );
		
		Iterator<Triple> iterator = g.graphBaseFind(triplePattern);
		int count = 0;
		
		while (iterator.hasNext()) {
			iterator.next();
			count++;
		}
		
		assertEquals(count,3);
	}
	
	@Test
	public void multilineQuote() {
		Node subject = Node.ANY;
		Node predicate = NodeFactory.createURI("http://example.org/vocab/show/218");
		Node object = NodeFactory.createURI("http://example.org/vocab/show/blurb");
		
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

package dk.aau.cs.qweb;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Test;

import dk.aau.cs.qweb.graph.Graph;
import dk.aau.cs.qweb.input.TurtleStarReader;

public class TurtleStarReaderTest {


	@Test
	public void test() {
		Graph g = new Graph();
        Model model = ModelFactory.createModelForGraph(g);
		TurtleStarReader reader = new TurtleStarReader(model);
		
		File spiderman = new File("src/test/resources/TurtleStar/spiderman.ttls");
		try {
			reader.read(spiderman);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Node subject = NodeFactory.createURI("http://example.org/spiderman");
		Node predicate = NodeFactory.createURI("http://www.perceive.net/schemas/relationship/enemyOf");
		Node object = Node.ANY;
		
		Triple triplePattern = new Triple(subject,predicate,object );
		
		Iterator<Triple> iterator = g.graphBaseFind(triplePattern);
		int count = 0;
		
		while (iterator.hasNext()) {
			count++;
		}
		
		assertEquals(count,1);
	}

}

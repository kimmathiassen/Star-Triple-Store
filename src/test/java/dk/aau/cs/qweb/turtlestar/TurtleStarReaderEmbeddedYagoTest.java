package dk.aau.cs.qweb.turtlestar;

import static org.junit.Assert.assertEquals;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Test;

import dk.aau.cs.qweb.graph.Graph;
import dk.aau.cs.qweb.main.App;

public class TurtleStarReaderEmbeddedYagoTest {

	Graph g;
	String prolog;
	Model model;
	
	@Test
	public void doesItLoad() {
		g = new Graph();
		model = ModelFactory.createModelForGraph(g);
		String filename = "src/test/resources/TurtleStar/yagoSmall.ttls" ;

        App.registerTTLS();
        App.registerQueryEngine();
        
        prolog = "PREFIX dc: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  "+
        		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  " +
        		"PREFIX foaf: <http://xmlns.com/foaf/0.1/>  " + 
        		"PREFIX ex: <http://example.org/>  " + 
        		"PREFIX rel: <http://www.perceive.net/schemas/relationship/>  ";
    	
        RDFDataMgr.read(model, filename);
        
		assertEquals(true, true);
	}
}

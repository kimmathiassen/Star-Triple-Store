package dk.aau.cs.qweb.TurtleStar;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.BeforeClass;

import dk.aau.cs.qweb.graph.Graph;
import dk.aau.cs.qweb.main.App;

public class TurtleStarReaderBaseTest {

	static Graph g;
	static String prolog;
	static Model model;
	
	@BeforeClass
	public static void setup() {
		g = new Graph();
		model = ModelFactory.createModelForGraph(g);
		String filename = "src/test/resources/TurtleStar/base.ttls" ;

        App.registerTTLS();
        App.registerQueryEngine();
        
        prolog = "PREFIX dc: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  "+
        		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  " +
        		"PREFIX foaf: <http://xmlns.com/foaf/0.1/>  " + 
        		"PREFIX ex: <http://example.org/>  " + 
        		"PREFIX rel: <http://www.perceive.net/schemas/relationship/>  ";
    	
        RDFDataMgr.read(model, filename);
	}


//	@Test
//	public void baseDirectiveUsed() {
//		
//		//This does not work for some reason.
//		//When an IRI is converted from token to Node the default (base) namespace is not added.
//		
//		Node subject = Node.ANY;
//		Node predicate = NodeFactory.createURI("http://example.org/title");
//		Node object = Node.ANY;
//		
//		Triple triplePattern = new Triple(subject,predicate,object );
//		
//		Iterator<Triple> iterator = g.graphBaseFind(triplePattern);
//		int count = 0;
//		
//		while (iterator.hasNext()) {
//			iterator.next();
//			count++;
//		}
//		
//		assertEquals(1,count);
//	}
}

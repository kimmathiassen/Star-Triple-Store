package dk.aau.cs.qweb.querylayer;

import static org.junit.Assert.assertEquals;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dk.aau.cs.qweb.dictionary.NodeDictionary;
import dk.aau.cs.qweb.dictionary.PrefixDictionary;
import dk.aau.cs.qweb.graph.Graph;
import dk.aau.cs.qweb.main.App;
import dk.aau.cs.qweb.main.queryparser.SyntaxStar;

public class queriesWithEmbeddedTriplePatternsAlexLatta {

	Graph g;
	String prolog;
	Model model;
	
	@Before
	public void setup() {
		g = new Graph();
		model = ModelFactory.createModelForGraph(g);
		String filename = "src/test/resources/TurtleStar/alex_latta.ttls" ;

        App.registerTTLS();
        App.registerQueryEngine();
        
        prolog = "PREFIX dc: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  "+
        		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  " +
        		"PREFIX foaf: <http://xmlns.com/foaf/0.1/>  " + 
        		"PREFIX ex: <http://example.org/>  " + 
        		"PREFIX rel: <http://www.perceive.net/schemas/relationship/>  " +
        		"prefix xsd: <http://www.w3.org/2001/XMLSchema#> ";
    	
        RDFDataMgr.read(model, filename);
        g.eliminateDuplicates();
	}
	
	@After
	public void tearDown() {
		PrefixDictionary.getInstance().clear();
		NodeDictionary.getInstance().clear();
	}
	
	@Test
	public void queryEmbeddedAndNormalTriples() {
		String queryString = prolog +
				"select ?name ?gender WHERE { <Alex_Latta> <hasGivenName> ?name ; <hasGender> ?gender. } " ; 
       
	    Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
	    int count = 0;
	    
	    System.out.println(NodeDictionary.getInstance());
	    System.out.println(g.getStore().getSPO());
	    
	    RDFNode gender = null;
	    RDFNode name = null;
		try(QueryExecution qexec = QueryExecutionFactory.create(query, model)){
	        ResultSet results = qexec.execSelect() ;
	        
	        while ( results.hasNext() ) {
	            QuerySolution solution = results.next();
	            name = (solution.get("name"));
	            gender = (solution.get("gender"));
	            count++;
	        }
	    }
	    assertEquals("\"Alex\"^^eng",name.toString());
	    assertEquals("male",gender.toString());
		assertEquals(1,count);
	}
}


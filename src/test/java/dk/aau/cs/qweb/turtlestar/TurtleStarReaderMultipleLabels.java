package dk.aau.cs.qweb.turtlestar;

import static org.junit.Assert.assertEquals;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dk.aau.cs.qweb.dictionary.HashNodeDictionary;
import dk.aau.cs.qweb.dictionary.PrefixDictionary;
import dk.aau.cs.qweb.graph.Graph;
import dk.aau.cs.qweb.main.App;
import dk.aau.cs.qweb.main.queryparser.SyntaxStar;

public class TurtleStarReaderMultipleLabels {

	Graph g;
	String prolog;
	Model model;
	
	@Before
	public void setup() {
		g = new Graph();
		model = ModelFactory.createModelForGraph(g);
		String filename = "src/test/resources/TurtleStar/multipleLabels.ttls" ;

        App.registerTTLS();
        App.registerQueryEngine();
        
        prolog = "PREFIX dc: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  "+
        		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  " +
        		"PREFIX foaf: <http://xmlns.com/foaf/0.1/>  " + 
        		"PREFIX ex: <http://example.org/>  " + 
        		"PREFIX rel: <http://www.perceive.net/schemas/relationship/>  ";
    	
        RDFDataMgr.read(model, filename);
	}

	@After
	public void tearDown() {
		PrefixDictionary.getInstance().clear();
		HashNodeDictionary.getInstance().clear();
	}
	
	@Test
	public void multipleLabel() {
		String queryString = prolog +
        		"SELECT ?o WHERE {<http://example.org/vocab/show/218> <http://www.w3.org/2000/01/rdf-schema#label> ?o .}" ; 
       
	    Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
	    int count = 0;
	    
	    try(QueryExecution qexec = QueryExecutionFactory.create(query, model)){
	        ResultSet results = qexec.execSelect() ;
	        
	        while ( results.hasNext() ) {
	            results.next();
	            count++;
	        }
	    }
		
		//Maybe only 2 because literals are default strings.
		assertEquals(3,count);
	}
}

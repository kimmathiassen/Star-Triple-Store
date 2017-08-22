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

import dk.aau.cs.qweb.dictionary.NodeDictionaryFactory;
import dk.aau.cs.qweb.dictionary.PrefixDictionary;
import dk.aau.cs.qweb.graph.Graph;
import dk.aau.cs.qweb.main.App;
import dk.aau.cs.qweb.queryparser.SyntaxStar;

public class TurtleStarReaderSpidermanTest {
	Graph g;
	String prolog;
	Model model;

	@Before
	public void setup() {
		g = new Graph();
		NodeDictionaryFactory.getDictionary().open();
		String filename = "src/test/resources/TurtleStar/spiderman.ttls" ;
		model = ModelFactory.createModelForGraph(g);
		 
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
		NodeDictionaryFactory.getDictionary().clear();
		NodeDictionaryFactory.getDictionary().close();
	}

	@Test
	public void commaSeperatedTriples() {
		String queryString = prolog +
        		"SELECT  ?o WHERE {ex:spiderman <http://xmlns.com/foaf/0.1/name> ?o .}" ; 
       
	    Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
	    int count = 0;
	    
	    try(QueryExecution qexec = QueryExecutionFactory.create(query, model)){
	        ResultSet results = qexec.execSelect() ;
	        
	        while ( results.hasNext() ) {
	            results.next();
	            count++;
	        }
	    }
		
		assertEquals(2,count);
	}
	
	@Test
	public void standardTriplePattern() {
		String queryString = prolog +
        		"SELECT  ?o WHERE {ex:spiderman <http://www.perceive.net/schemas/relationship/enemyOf> ?o .}" ; 
       
	    Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
	    int count = 0;
	    
	    try(QueryExecution qexec = QueryExecutionFactory.create(query, model)){
	        ResultSet results = qexec.execSelect() ;
	        
	        while ( results.hasNext() ) {
	            results.next();
	            count++;
	        }
	    }
	    
		assertEquals(1,count);
	}
	
	@Test
	public void linesWithComments() {
		String queryString = prolog +
        		"SELECT  ?o WHERE {?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Person> .}" ; 
       
	    Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
	    int count = 0;
	    
	    try(QueryExecution qexec = QueryExecutionFactory.create(query, model)){
	        ResultSet results = qexec.execSelect() ;
	        
	        while ( results.hasNext() ) {
	            results.next();
	            count++;
	        }
	    }
		
		assertEquals(2,count);
	}
	
	@Test
	public void linesWithCommentsWithShorthandNotationForType() {
		String queryString = prolog +
        		"SELECT  ?o WHERE {?s a <http://xmlns.com/foaf/0.1/Person> .}" ; 
       
	    Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
	    int count = 0;
	    
	    
	    
	    try(QueryExecution qexec = QueryExecutionFactory.create(query, model)){
	        ResultSet results = qexec.execSelect() ;
	        
	        while ( results.hasNext() ) {
	            results.next();
	            count++;
	        }
	    }
		
		assertEquals(2,count);
	}
}

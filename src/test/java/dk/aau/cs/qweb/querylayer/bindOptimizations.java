package dk.aau.cs.qweb.querylayer;

import static org.junit.Assert.assertEquals;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Before;
import org.junit.Test;

import dk.aau.cs.qweb.graph.Graph;
import dk.aau.cs.qweb.main.App;
import dk.aau.cs.qweb.main.queryparser.SyntaxStar;

public class bindOptimizations {

	private static Graph g;
	private static String prolog;
	private static Model model;

	@Before
	public void setUpBefore() throws Exception {
		g = new Graph();
		model = ModelFactory.createModelForGraph(g);
		String filename = "src/test/resources/TurtleStar/embedded.ttls" ;

        App.registerTTLS();
        App.registerQueryEngine();
        
        prolog = "PREFIX dc: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  "+
        		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  " +
        		"PREFIX foaf: <http://xmlns.com/foaf/0.1/>  " + 
        		"PREFIX ex: <http://example.org/>  " + 
        		"PREFIX rel: <http://www.perceive.net/schemas/relationship/>  ";
    	
        RDFDataMgr.read(model, filename);
        g.eliminateDuplicates();
	}


	@Test
	public void saveLookupInBindWithBindVariableUseInLaterTriplePattern() {
		String queryString = prolog +
				"SELECT ?date ?t WHERE {BIND(<<ex:kim ex:worksAt ex:aau>> as ?t) .  ?t ex:started  ?date . }" ; 
       
	    Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
	    int count = 0;
	    
	    try(QueryExecution qexec = QueryExecutionFactory.create(query, model)){
	        ResultSet results = qexec.execSelect() ;
	        
	        while ( results.hasNext() ) {
	            results.next();
	            count++;
	        }
	    }
	    
	    assertEquals(1,g.getNumberOfLookups());
		assertEquals(1,count);
		
	}
	
	
	@Test
	public void indexLookupWithBind() {
		String queryString = prolog +
				"SELECT ?s WHERE {BIND(<<?s ex:worksAt ex:aau>> as ?t) .  ?s ex:worksAt ex:LiU . }" ; 
       
	    Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
	    
	    try(QueryExecution qexec = QueryExecutionFactory.create(query, model)){
	        ResultSet results = qexec.execSelect() ;
	        
	        while ( results.hasNext() ) {
	            results.next();
	        }
	    }
	    
	    assertEquals(2,g.getNumberOfLookups());
		
	}

}

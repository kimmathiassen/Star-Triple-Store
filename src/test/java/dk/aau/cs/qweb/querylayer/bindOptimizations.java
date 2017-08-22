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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dk.aau.cs.qweb.dictionary.NodeDictionaryFactory;
import dk.aau.cs.qweb.dictionary.PrefixDictionary;
import dk.aau.cs.qweb.graph.Graph;
import dk.aau.cs.qweb.main.App;
import dk.aau.cs.qweb.queryparser.SyntaxStar;

public class bindOptimizations {

	private Graph g;
	private String prolog;
	private Model model;

	@Before
	public void setUpBefore() throws Exception {
		NodeDictionaryFactory.getDictionary().open();
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

	@After
	public void tearDown() {
		PrefixDictionary.getInstance().clear();
		NodeDictionaryFactory.getDictionary().clear();
		NodeDictionaryFactory.getDictionary().close();
	}

	@Test
	public void saveLookupInBindWithBindVariableUseInLaterTriplePattern() {
		//Known bug
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
	    
	    //Since contains do not count as a lookup, the number of lookups should be 0
	    assertEquals(0,g.getNumberOfLookups());
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
	    
	    //One lookup and one contains
	    assertEquals(1,g.getNumberOfLookups());
	}
}

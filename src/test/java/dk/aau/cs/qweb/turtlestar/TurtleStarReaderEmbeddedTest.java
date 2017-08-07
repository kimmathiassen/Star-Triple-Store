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
import dk.aau.cs.qweb.main.queryparser.SyntaxStar;

public class TurtleStarReaderEmbeddedTest {

	Graph g;
	String prolog;
	Model model;
	
	@Before
	public void setup() {
		PrefixDictionary.getInstance().clear();
		NodeDictionaryFactory.getDictionary().clear();
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
	}
	
	@Test
	public void subjectEmbeddedNode() {
		// This test case fails when all tests are run, when run individually it pass
		String queryString = prolog +
	        		"SELECT ?p ?o WHERE {<<ex:kim ex:worksAt ex:aau>> ?p ?o .}" ; 
	       
		//System.out.println(NodeDictionary.getInstance());
		//System.out.println(g.getStore().getSPO());
		
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
	public void embeddedNodeInSubjectPositionTripleHasPredicate() {
		String queryString = prolog +
        		"SELECT ?o WHERE {<<ex:kim ex:worksAt ex:aau>> ex:is ?o .}" ; 
       
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
	public void findEmbeddedNode() {
		String queryString = prolog +
        		"SELECT ?s WHERE {?s ex:is \"Not a lie\" .}" ; 
       
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
	public void embeddedTriplesAreAddedAsNormalTriples() {
		String queryString = prolog +
        		"SELECT ?o WHERE {ex:kim ex:worksAt ?o .}" ; 
       
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
		assertEquals(0, NodeDictionaryFactory.getDictionary().getNumberOfReferenceTriples());
	}
	
//	@Test
//	public void test() {
//		Key refMax = new Key(-Long.parseLong("0100000000000000000000000000000000000000000000000000000000000000", 2)+1152921504606846975l);
//		System.out.println(refMax+" "+refMax.getId());
//		
//		Key refMin = new Key(-Long.parseLong("0100000000000000000000000000000000000000000000000000000000000000", 2)+0);
//		System.out.println(refMin+" "+refMin.getId());
//	}
}

package dk.aau.cs.qweb.queryengine;

import static org.junit.Assert.assertEquals;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dk.aau.cs.qweb.dictionary.VarDictionary;
import dk.aau.cs.qweb.graph.Graph;
import dk.aau.cs.qweb.main.App;
import dk.aau.cs.qweb.main.queryparser.SyntaxStar;

public class QueryEngineStarTest {

	private static VarDictionary varDict;
	private static Model model;

	@BeforeClass
	public static void setUpBeforeClass() {
        App.registerQueryEngine();
        Graph g = new Graph();
		model = ModelFactory.createModelForGraph(g);
        varDict = VarDictionary.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		varDict.clear();
	}

	@Test
	public void threeVariablesTriplePatternProjectThree() {
		String queryString = 
        		"SELECT ?s ?p ?o WHERE {?s ?p ?o .}" ;
       
	    Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
	    
	    try(QueryExecution qexec = QueryExecutionFactory.create(query, model)){
            qexec.execSelect() ;
        }
	    
	    assertEquals(3,varDict.size()); 
	}
	
	@Test
	public void threeVariablesTriplePatternProjectOne() {
		String queryString = 
        		"SELECT ?s  WHERE {?s ?p ?o .}" ; 
       
		Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
	    try(QueryExecution qexec = QueryExecutionFactory.create(query, model)){
            qexec.execSelect() ;
        }
	    
	    assertEquals(3,varDict.size()); 
	}
	
	@Test
	public void fixedVariableTriplePatterns() {
		String queryString = 
        		"SELECT ?o WHERE {<http://test.org/subject1> <http://test.org/predicate1> ?o . ?o <http://test.org/predicate2> <http://test.org/object1>}" ; 
       
		Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
	    try(QueryExecution qexec = QueryExecutionFactory.create(query, model)){
            qexec.execSelect() ;
        }
		
		assertEquals(1,varDict.size()); 
	}
	
	@Test
	public void triplePatternsWithOpenVariable() {
		String queryString = 
        		"SELECT ?s ?o WHERE {?s <http://test.org/predicate1>  <http://test.org/object1> . ?s <http://test.org/predicate2> ?o}" ; 
       
		Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
	    try(QueryExecution qexec = QueryExecutionFactory.create(query, model)){
            qexec.execSelect() ;
        }
	    
		assertEquals(2,varDict.size()); 
	}
	
	@Test
	public void disconnectedTriplePatterns() {
		String queryString = 
        		"SELECT ?s ?o WHERE {?s <http://test.org/predicate1>  <http://test.org/object1> . ?k <http://test.org/predicate2> ?o}" ; 
       
		Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
	    try(QueryExecution qexec = QueryExecutionFactory.create(query, model)){
            qexec.execSelect() ;
        }
	    
		assertEquals(3,varDict.size()); 
	}

}

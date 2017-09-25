package dk.aau.cs.qweb.querylayer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;

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

import dk.aau.cs.qweb.dictionary.NodeDictionaryFactory;
import dk.aau.cs.qweb.dictionary.PrefixDictionary;
import dk.aau.cs.qweb.graph.Graph;
import dk.aau.cs.qweb.main.App;
import dk.aau.cs.qweb.queryparser.SyntaxStar;

public class queriesWithEmbeddedTriplePatterns {

	Graph g;
	String prolog;
	Model model;
	
	@Before
	public void setup() {
//		g = new Graph();
//		model = ModelFactory.createModelForGraph(g);
//		Resource kim = ResourceFactory .createResource("http://example.org/kim");
//		Property worksAt = ResourceFactory.createProperty("http://example.org/worksAt");
//		Property is = ResourceFactory.createProperty("http://example.org/is");
//		Property started = ResourceFactory.createProperty("http://example.org/started");
//		Literal notALie = ResourceFactory.createStringLiteral("Not a Lie");
//		Literal date = ResourceFactory.createStringLiteral("1999-08-16");
//		RDFNode liu = ResourceFactory.createResource("http://example.org/liu");
//		RDFNode aau = ResourceFactory.createResource("http://example.org/aau");
//			
//		
//		Node embedded = NodeFactoryStar.createEmbeddedNode((Node)kim, (Node)worksAt, (Node)aau);
////		Triple sdfsdf = new Triple(embedded,started,date);
//		
//		
//		model.add(kim, worksAt, liu);
//		model.add((Resource)embedded, is, notALie);
//		model.add((Resource)embedded, started, date);
//		
//        App.registerQueryEngine();
//        
//        prolog = "PREFIX dc: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  "+
//        		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  " +
//        		"PREFIX foaf: <http://xmlns.com/foaf/0.1/>  " + 
//        		"PREFIX ex: <http://example.org/>  " + 
//        		"PREFIX rel: <http://www.perceive.net/schemas/relationship/>  ";
		
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
        		"PREFIX rel: <http://www.perceive.net/schemas/relationship/>  " +
        		"prefix xsd: <http://www.w3.org/2001/XMLSchema#> ";
    	
        RDFDataMgr.read(model, filename);
        g.eliminateDuplicates();
	}
	
	@After
	public void tearDown() throws IOException {
		PrefixDictionary.getInstance().clear();
		NodeDictionaryFactory.getDictionary().clear();
		NodeDictionaryFactory.getDictionary().close();
	}
	
	@Test
	public void queryEmbeddedPlusOtherVariable() {
		String queryString = prolog +
        		"SELECT ?t ?date WHERE {?t ex:started  ?date }" ; 
       
	    Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
	    int count = 0;
	    RDFNode t = null;
	    RDFNode date = null;
	    
	    try(QueryExecution qexec = QueryExecutionFactory.create(query, model)){
	        ResultSet results = qexec.execSelect() ;
	        
	        while ( results.hasNext() ) {
	            QuerySolution solution = results.next();
	            t = solution.get("t");
	            date = solution.get("date");
	            count++;
	        }
	    }
	    assertEquals("<<http://example.org/kim http://example.org/worksAt http://example.org/aau>>",t.toString());
	    assertEquals("1999-08-16^^http://www.w3.org/2001/XMLSchema#date",date.toString());
		assertEquals(1,count);
	}
	
	@Test
	public void queryMixOfEmbeddedAndNormalTriples() {
		String queryString = prolog +
				"SELECT ?o WHERE {ex:kim ex:worksAt ?o} order by ?o" ; 
       
	    Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
	    int count = 0;
	    ArrayList<RDFNode> o = new ArrayList<RDFNode>();
	    
	    try(QueryExecution qexec = QueryExecutionFactory.create(query, model)){
	        ResultSet results = qexec.execSelect() ;
	        
	        while ( results.hasNext() ) {
	            QuerySolution solution = results.next();
	            o.add(solution.get("o"));
	            count++;
	        }
	    }
	    assertEquals("http://example.org/aau",o.get(1).toString());
	    assertEquals("http://example.org/LiU",o.get(0).toString());
		assertEquals(2,count);
	}
	
	@Test
	public void queryWithBind() {
		String queryString = prolog +
				"SELECT ?date ?t WHERE {BIND(<<ex:kim ex:worksAt ex:aau>> as ?t) .  ?t ex:started  ?date . }" ; 
       
	    Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
	    int count = 0;
	    RDFNode date = null;
	    
	    try(QueryExecution qexec = QueryExecutionFactory.create(query, model)){
	        ResultSet results = qexec.execSelect() ;
	        
	        while ( results.hasNext() ) {
	            QuerySolution solution = results.next();
	            date  = solution.get("date");
	            count++;
	        }
	    }
	    assertEquals("1999-08-16^^http://www.w3.org/2001/XMLSchema#date",date.toString());
		assertEquals(1,count);
	}
	
	@Test
	public void queryWithBindContainingUnusedVariable() {
		String queryString = prolog +
				"SELECT ?date WHERE {BIND(<<ex:kim ex:worksAt ?o>> as ?t) .  ?t ex:started  ?date . }" ; 
       
	    Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
	    int count = 0;
	    RDFNode date = null;
	    
	    try(QueryExecution qexec = QueryExecutionFactory.create(query, model)){
	        ResultSet results = qexec.execSelect() ;
	        
	        while ( results.hasNext() ) {
	            QuerySolution solution = results.next();
	            date  = solution.get("date");
	            count++;
	        }
	    }
	    assertEquals("1999-08-16^^http://www.w3.org/2001/XMLSchema#date",date.toString());
		assertEquals(1,count);
	}
	
	@Test
	public void queryWithBindContainingUsedVariableUnusedBindVar() {
		String queryString = prolog +
				"SELECT ?s WHERE {BIND(<<?s ex:worksAt ex:aau>> as ?t) .  ?s ex:worksAt ex:LiU .}" ; 
       
	    Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
	    int count = 0;
	    RDFNode s = null;
	    try(QueryExecution qexec = QueryExecutionFactory.create(query, model)){
	        ResultSet results = qexec.execSelect() ;
	        
	        while ( results.hasNext() ) {
	            QuerySolution solution = results.next();
	            s  = solution.get("s");
	            count++;
	        }
	    }
	    assertEquals("http://example.org/kim",s.toString());
		assertEquals(1,count);
	}
	
	@Test
	public void queryWithBindThatDoNotMatchDataUsedInTriple() {
		String queryString = prolog +
				"SELECT ?date ?t WHERE {BIND(<<ex:kim ex:worksAt ex:LiU>> as ?t) .  ?t ex:started  ?date . }" ; 
       
	    Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
	    int count = 0;
	    try(QueryExecution qexec = QueryExecutionFactory.create(query, model)){
	        ResultSet results = qexec.execSelect() ;
	        
	        while ( results.hasNext() ) {
	        	results.next();
	            count++;
	        }
	    }
		assertEquals(0,count);
	}
	
	@Test
	public void queriesWithPrefixAndFullURI() {
		String queryString1 = prolog +
				"SELECT ?t WHERE {BIND(<<ex:kim ex:worksAt ex:LiU>> as ?t) }" ; 
       
	    Query query1 = QueryFactory.create(queryString1,SyntaxStar.syntaxSPARQL_Star) ;
	    RDFNode t1 = null;
	    try(QueryExecution qexec1 = QueryExecutionFactory.create(query1, model)){
	        ResultSet results1 = qexec1.execSelect() ;
	        
	        while ( results1.hasNext() ) {
	        	QuerySolution solution1 = results1.next();
	            t1  = solution1.get("t");
	        }
	    }
	    
	    String queryString2 = prolog +
				"SELECT ?t WHERE {BIND(<<ex:kim <http://example.org/worksAt> ex:LiU>> as ?t) }" ; 
       
	    Query query2 = QueryFactory.create(queryString2,SyntaxStar.syntaxSPARQL_Star) ;
	    RDFNode t2 = null;
	    try(QueryExecution qexec2 = QueryExecutionFactory.create(query2, model)){
	        ResultSet results2 = qexec2.execSelect() ;
	        
	        while ( results2.hasNext() ) {
	        	QuerySolution solution2 = results2.next();
	            t2  = solution2.get("t");
	        }
	    }
	    
		assertEquals(t1,t2);
	}
	
	@Test
	public void queryWithMultipleEmbeddedTriplePatternsWithDot() {
		String queryString = prolog +
        		"SELECT ?s WHERE {<<?s ex:worksAt ex:aau>> ex:is \"Not a lie\" . \n"+ 
        "<<?s ex:worksAt ex:aau>> ex:started \"1999-08-16\"^^xsd:date . }" ; 
       
	    Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
	    int count = 0;
	    RDFNode s = null;
	    
	    try(QueryExecution qexec = QueryExecutionFactory.create(query, model)){
	        ResultSet results = qexec.execSelect() ;
	        
	        while ( results.hasNext() ) {
	            QuerySolution solution = results.next();
	            s = solution.get("s");
	            count++;
	        }
	    }
	    assertEquals("http://example.org/kim",s.toString());
		assertEquals(1,count);
	}
	
	@Test
	public void queryWithMultipleEmbeddedTriplePatternsWithSemicolon() {
		String queryString = prolog +
        		"SELECT ?s WHERE {<<?s ex:worksAt ex:aau>> ex:is \"Not a lie\" ; "+ 
        " ex:started \"1999-08-16\"^^xsd:date . }" ; 
       
	    Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
	    int count = 0;
	    RDFNode s = null;
	    
	    try(QueryExecution qexec = QueryExecutionFactory.create(query, model)){
	        ResultSet results = qexec.execSelect() ;
	        
	        while ( results.hasNext() ) {
	            QuerySolution solution = results.next();
	            s = solution.get("s");
	            count++;
	        }
	    }
	    assertEquals("http://example.org/kim",s.toString());
		assertEquals(1,count);
	}
	
	@Test
	public void queryWithTwoTriplePatternsOneBind() {
		String queryString = prolog +
        		"SELECT ?s WHERE {Bind(<<?s ex:worksAt ex:aau>> AS ?t1) . ?t1 ex:is \"Not a lie\" ."+ 
        "?t1 ex:started \"1999-08-16\"^^xsd:date }" ; 
       
	    Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
	    int count = 0;
	    RDFNode s = null;
	    
	    try(QueryExecution qexec = QueryExecutionFactory.create(query, model)){
	        ResultSet results = qexec.execSelect() ;
	        
	        while ( results.hasNext() ) {
	            QuerySolution solution = results.next();
	            s = solution.get("s");
	            count++;
	        }
	    }
	    assertEquals("http://example.org/kim",s.toString());
		assertEquals(1,count);
	}
	
	
	@Test
	public void queryTriplePatternsWithDotSubjectSubjectJoin() {
		String queryString = prolog +
        		"SELECT ?s WHERE {?s ex:is \"Not a lie\" . "+ 
        "?s ex:started \"1999-08-16\"^^xsd:date . }" ; 
       
	    Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
	    int count = 0;
	    RDFNode s = null;
	    
	    try(QueryExecution qexec = QueryExecutionFactory.create(query, model)){
	        ResultSet results = qexec.execSelect() ;
	        
	        while ( results.hasNext() ) {
	            QuerySolution solution = results.next();
	            s = solution.get("s");
	            count++;
	        }
	    }
	    assertEquals("<<http://example.org/kim http://example.org/worksAt http://example.org/aau>>",s.toString());
		assertEquals(1,count);
	}
	
	@Test
	public void queryTriplePatternsWithDotSubjectObjectJoin() {
		String queryString = prolog +
        		"SELECT ?s WHERE {?s ex:is \"Not a lie\" . "+ 
        "<http://example.org/kim> ex:started ?s . }" ; 
       
	    Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
	    int count = 0;
	    
	    try(QueryExecution qexec = QueryExecutionFactory.create(query, model)){
	        ResultSet results = qexec.execSelect() ;
	        
	        while ( results.hasNext() ) {
	            count++;
	        }
	    }
		assertEquals(0,count);
	}
	
}


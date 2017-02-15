package dk.aau.cs.qweb.main;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.LangBuilder;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RDFParserRegistry;
import org.apache.jena.riot.ReaderRIOTFactory;
import org.apache.jena.sparql.lang.SPARQLParserRegistry;
import org.apache.jena.sparql.serializer.SerializerRegistry;

import dk.aau.cs.qweb.graph.Graph;
import dk.aau.cs.qweb.main.queryparser.SPARQLParserFactoryStar;
import dk.aau.cs.qweb.main.queryparser.SyntaxStar;
import dk.aau.cs.qweb.main.queryserializer.QuerySerializerFactoryStar;
import dk.aau.cs.qweb.queryengine.QueryEngineStar;
import dk.aau.cs.qweb.turtlestar.TTLSReaderFactory;

public class App {
	
	public static void main(String[] args) {
		Graph g = new Graph();
		String filename = "src/test/resources/TurtleStar/spiderman.ttls" ;
		Model model = ModelFactory.createModelForGraph(g);
        registerTTLS();
        registerQueryEngine();
    	
        RDFDataMgr.read(model, filename);
        
        String prolog = "PREFIX dc: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  "+
        		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  " +
        		"PREFIX foaf: <http://xmlns.com/foaf/0.1/>  " + 
        		"PREFIX ex: <http://example.org/>  " + 
        		"PREFIX rel: <http://www.perceive.net/schemas/relationship/>  ";
        
        String queryString = prolog +
        		"SELECT ?p ?o WHERE {<http://example.org/spiderman> ?p ?o .}" ; 
       
        Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
        
        try(QueryExecution qexec = QueryExecutionFactory.create(query, model)){
            ResultSet rs = qexec.execSelect() ;
            ResultSetFormatter.out(System.out, rs, query) ;
        }
    }

	public static void registerTTLS() {
		Lang lang = LangBuilder.create("SSE", "text/turtle-star").addFileExtensions("ttls").build() ;

        // This just registers the name, not the parser.
        RDFLanguages.register(lang) ;

        // Register the parser factory.
        ReaderRIOTFactory factory = new TTLSReaderFactory() ;
        RDFParserRegistry.registerLangTriples(lang, factory) ;
	}
    
	public static void registerQueryEngine() {
		SPARQLParserFactoryStar f = new SPARQLParserFactoryStar();
		    
		SPARQLParserRegistry.addFactory(SyntaxStar.syntaxSPARQL_Star, f);
		
		SerializerRegistry s = SerializerRegistry.get();
		QuerySerializerFactoryStar serializer = new QuerySerializerFactoryStar();
		s.addQuerySerializer(SyntaxStar.syntaxSPARQL_Star, serializer);
		
		QueryEngineStar.register();
	}
}

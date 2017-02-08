package dk.aau.cs.qweb.main;

import org.apache.jena.atlas.web.TypedInputStream;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.LangBuilder;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RDFParserRegistry;
import org.apache.jena.riot.ReaderRIOTFactory;
import org.apache.jena.riot.RiotNotFoundException;
import org.apache.jena.riot.system.stream.StreamManager;
import org.apache.jena.sparql.lang.SPARQLParserRegistry;
import org.apache.jena.sparql.serializer.SerializerRegistry;
import org.apache.jena.vocabulary.DC;

import dk.aau.cs.qweb.graph.Graph;
import dk.aau.cs.qweb.main.queryparser.SPARQLParserFactoryStar;
import dk.aau.cs.qweb.main.queryparser.SyntaxStar;
import dk.aau.cs.qweb.main.queryserializer.QuerySerializerFactoryStar;
import dk.aau.cs.qweb.turtlestar.TTLSReaderFactory;

public class App {
	static public final String NL = System.getProperty("line.separator") ; 
	
	
	public static void main(String[] args) {
		
		Graph g = new Graph();
		String filename = "src/test/resources/TurtleStar/spiderman.ttls" ;
		Model model = ModelFactory.createModelForGraph(g);
        registerTTLS();
    	
        RDFDataMgr.read(model, filename);
        
        String prolog = "PREFIX dc: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  "+
        		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  " +
        		"PREFIX foaf: <http://xmlns.com/foaf/0.1/>  " + 
        		"PREFIX ex: <http://example.org/>  " + 
        		"PREFIX rel: <http://www.perceive.net/schemas/relationship/>  ";
        
        // Query string.
        String queryString = prolog +
            "SELECT ?p ?o WHERE {<<ex:spiderman ex:spiderman1 ex:spiderman2>> ?p ?o .}" ; 
        
        SPARQLParserFactoryStar f = new SPARQLParserFactoryStar();
        
        SPARQLParserRegistry.addFactory(SyntaxStar.syntaxSPARQL_Star, f);
        
        SerializerRegistry s = SerializerRegistry.get();
        QuerySerializerFactoryStar serializer = new QuerySerializerFactoryStar();
        s.addQuerySerializer(SyntaxStar.syntaxSPARQL_Star, serializer);
        
        Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
        
        
        
        try(QueryExecution qexec = QueryExecutionFactory.create(query, model)){
            // Or QueryExecutionFactory.create(queryString, model) ;

            // Assumption: it's a SELECT query.
            ResultSet rs = qexec.execSelect() ;

            // The order of results is undefined. 
            ResultSetFormatter.out(System.out, rs, query) ;
        }
        
     // Close the dataset.
        //dataset.commit();
        //dataset.close();
    }

	public static void registerTTLS() {
		Lang lang = LangBuilder.create("SSE", "text/turtle-star").addFileExtensions("ttls").build() ;

        // This just registers the name, not the parser.
        RDFLanguages.register(lang) ;

        // Register the parser factory.
        ReaderRIOTFactory factory = new TTLSReaderFactory() ;
        RDFParserRegistry.registerLangTriples(lang, factory) ;
	}
    
    public static Model populateModel(Model m)
    {
        Resource r1 = m.createResource("http://example.org/book#1") ;
        Resource r2 = m.createResource("http://example.org/book#2") ;
        
        r1.addProperty(DC.title, "SPARQL - the book")
          .addProperty(DC.description, "A book about SPARQL") ;
        
        r2.addProperty(DC.title, "Advanced techniques for SPARQL") ;
        
        return m ;
    }
    
    public static TypedInputStream open(String filenameOrURI) {
        StreamManager sMgr = StreamManager.get() ;
             
        return open(filenameOrURI, sMgr) ;
    }
    
    /** Open a stream to the destination (URI or filename)
     * Performs content negotiation, including looking at file extension. 
     * @param filenameOrURI
     * @param streamManager
     * @return TypedInputStream
     */
    public static TypedInputStream open(String filenameOrURI, StreamManager streamManager) {
        TypedInputStream in = streamManager.open(filenameOrURI) ;
            
        if ( in == null ) {
            throw new RiotNotFoundException("Not found: "+filenameOrURI) ;
        }
        return in ;
    }
}

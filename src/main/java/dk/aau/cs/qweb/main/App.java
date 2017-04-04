package dk.aau.cs.qweb.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
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
		List<String> queries = new ArrayList<String>();
		CommandLineParser parser = new DefaultParser();
		Graph g = new Graph();
		String filename = "";
		Model model = ModelFactory.createModelForGraph(g);
		String  prolog = "PREFIX dc: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  "+
        		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  " +
        		"PREFIX foaf: <http://xmlns.com/foaf/0.1/>  " + 
        		"PREFIX ex: <http://example.org/>  " + 
        		"PREFIX rel: <http://www.perceive.net/schemas/relationship/>  "+
        		"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>   ";
		
		// create the Options
		Options options = new Options();
		options.addOption("h", "help", false, "Display this message." );
		options.addOption("q", "query", true, "the sparql* query");
		options.addOption("f", "query-folder", true, "path to folder with .sparqls files");
		options.addOption("l", "location", true, "path to the turtle* file");
		options.addOption("e", "explain", false, "prints the query plan");
		
		try {
		    CommandLine line = parser.parse( options, args );
				    
		    if (line.hasOption( "help" )) {
		    	printHelp(null,options);
		    	System.exit(0);
			} 
				    
		    if (line.hasOption("query")) {
		    	String queryString = prolog + line.getOptionValue("query"); 
		    	queries.add(queryString);
		    }
		    
		    if (line.hasOption("query-folder")) {
		    	final File folder = new File(line.getOptionValue("query-folder"));
		    	for (final File fileEntry : folder.listFiles()) {
		    		if (fileEntry.getName().endsWith(".sparqls")) {
		    			
		    			String queryString = prolog + new String(Files.readAllBytes(Paths.get(fileEntry.toString())));
		    			queries.add(queryString);
					}
		        }
		    }
		    
		    if (line.hasOption("location")) {
		    	filename = line.getOptionValue("location");
		    }
		    
		    if (line.hasOption("explain")) {
		    	Config.setExplainFlag();
		    }
		    
		} catch( ParseException exp ) {
			printHelp(exp, options);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
        registerTTLS();
        registerQueryEngine();
        ModelFactory.createDefaultModel();
        
     
    	
        RDFDataMgr.read(model, filename);
        g.eliminateDuplicates();
        
        for (String queryString : queries) {
        	System.out.println(queryString);
        	 Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
             
             try(QueryExecution qexec = QueryExecutionFactory.create(query, model)){
                 ResultSet rs = qexec.execSelect() ;
                 ResultSetFormatter.out(System.out, rs, query) ;
             }
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
	
	private static void printHelp(ParseException exp, Options options) {
		String header = "";
		HelpFormatter formatter = new HelpFormatter();
		if (exp != null) {
			header = "Unexpected exception:" + exp.getMessage();
		}
		formatter.printHelp("Provenance Enabled Cubes App", header, options, null, true);
	}
}

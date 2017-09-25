package dk.aau.cs.qweb.main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

import dk.aau.cs.qweb.dictionary.NodeDictionaryFactory;
import dk.aau.cs.qweb.graph.Graph;
import dk.aau.cs.qweb.queryengine.QueryEngineStar;
import dk.aau.cs.qweb.queryparser.SPARQLParserFactoryStar;
import dk.aau.cs.qweb.queryparser.SyntaxStar;
import dk.aau.cs.qweb.resultserializer.QueryStarResultSerializerFactory;
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
		options.addOption("q", "query", true, "the path to a file containing the sparql* query ");
		options.addOption("f", "query-folder", true, "path to folder with .sparqls files");
		options.addOption("l", "location", true, "path to the turtle* file");
		options.addOption("p", "disable-prefix-dictionary", false, "disable the prefix dictionary (default on)");
		options.addOption("i", "index", true, "type of index: hashindex, flatindex, treeindex. (default hashindex)");
		options.addOption("d", "dictionary", true, "type of dictionary: InMemoryHashMap, DiskBTree. (default DiskBTree)");
		
		// Parse options
		try {
		    CommandLine line = parser.parse( options, args );
				    
		    if (line.hasOption( "help" )) {
		    	printHelp(null,options);
		    	System.exit(0);
			} 
				    
		    if (line.hasOption("query")) {
		    	Scanner in = new Scanner(new FileReader(line.getOptionValue("query")));
		    	StringBuilder sb = new StringBuilder();
		    	while(in.hasNext()) {
		    	    sb.append(in.next());
		    	}
		    	in.close();
		    	String queryString = prolog + sb.toString(); 
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
		    	Config.setLocation(filename);
		    }
		    
		    if (line.hasOption("disable-prefix-dictionary")) {
		    	Config.enablePrefixDictionary(false);
		    }
		    
		    if (line.hasOption("index")) {
		    	Config.setIndex(line.getOptionValue("index"));
		    }
		    
		    if (line.hasOption("dictionary")) {
		    	Config.setDictionaryType(line.getOptionValue("dictionary"));
		    }
		    
		} catch( ParseException exp ) {
			printHelp(exp, options);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		// register the .ttls reader and parser
        registerTTLS();
        
        // register the sparql* query parser and optimizer
        registerQueryEngine();
        
        long start_time;
        
        // load data
        try {
        	NodeDictionaryFactory.getDictionary().open();
        	System.out.println("Loading file: "+filename);
        	start_time = System.nanoTime();
            RDFDataMgr.read(model, filename);
            System.out.println("Loading finished: "+(System.nanoTime() - start_time) / 1e6+" ms");
		} finally {
			//Ensure that potential physical database connections are closed.
			NodeDictionaryFactory.getDictionary().close();
		}
        
        // Delete duplicate triples
        System.out.println();
        System.out.println("Deleting duplicates");
        start_time = System.nanoTime();
        g.eliminateDuplicates();
        System.out.println("Deleting duplicates finished: "+(System.nanoTime() - start_time) / 1e6+" ms");
        
        // Evaluation of the queries
        System.out.println();
        System.out.println("Evaluating queries:");
        for (String queryString : queries) {
        	System.out.println(queryString);
             
            try{
            	 start_time = System.nanoTime();
            	 Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
            	 QueryExecution qexec = QueryExecutionFactory.create(query, model);
            	 NodeDictionaryFactory.getDictionary().open();
                 ResultSet rs = qexec.execSelect() ;
                 ResultSetFormatter.out(System.out, rs, query) ;
             } finally {
            	 NodeDictionaryFactory.getDictionary().close();
             }
             System.out.println("Query finished: "+(System.nanoTime() - start_time) / 1e6+" ms");
             System.out.println();
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
		QueryStarResultSerializerFactory serializer = new QueryStarResultSerializerFactory();
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

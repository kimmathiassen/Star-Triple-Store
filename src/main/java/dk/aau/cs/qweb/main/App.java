package dk.aau.cs.qweb.main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
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
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import dk.aau.cs.qweb.dictionary.NodeDictionaryFactory;
import dk.aau.cs.qweb.graph.Graph;
import dk.aau.cs.qweb.queryengine.QueryEngineStar;
import dk.aau.cs.qweb.queryparser.SPARQLParserFactoryStar;
import dk.aau.cs.qweb.queryparser.SyntaxStar;
import dk.aau.cs.qweb.resultserializer.QueryStarResultSerializerFactory;
import dk.aau.cs.qweb.turtlestar.TTLSReaderFactory;

public class App {
	
	static Logger log = Logger.getLogger(App.class.getName());
	static List<String> queries = new ArrayList<String>();
	static Graph g = new Graph();
	   
	public static void main(String[] args) {
		
		CommandLineParser parser = new DefaultParser();
		
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
		options.addOption("q", "query", true, "The path to a file containing the sparql* query ");
		options.addOption("f", "query-folder", true, "Path to folder with .sparqls files");
		options.addOption("l", "location", true, "Path to the turtle* file");
		options.addOption("p", "disable-prefix-dictionary", false, "Disable the prefix dictionary (default on)");
		options.addOption("i", "index", true, "Types of index: hashindex, flatindex, treeindex. (default hashindex)");
		options.addOption("d", "dictionary", true, "Types of dictionary: InMemoryHashMap, DiskBTree, HybridBTree, DiskBloomfilterBTree. (default HybridBTree)");
		options.addOption("e", "encoding", true, "The partitioning of the 62 bits of the embedded triples, format is AABBCC, e.g. 201032");
		options.addOption("r", "reference-triple-distribution", true, "Give a percentage number that artificially determine the distribution of reference triples, e.g. 50 ");
		options.addOption("j", "log4j", true, "set the logging level for log4j, i.e. All, Debug, Error, Fatal, Info, Off, Trace or Warn. (default Info) ");
		
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
		    
		    if (line.hasOption("encoding")) {
		    	String encoding = line.getOptionValue("encoding");
		    	Config.setSubjectSizeInBits(Integer.parseInt(encoding.substring(0, 1)));
		    	Config.setPredicateSizeInBits(Integer.parseInt(encoding.substring(2, 3)));
		    	Config.setObjectSizeInBits(Integer.parseInt(encoding.substring(4, 5)));
		    }
		    
		    if (line.hasOption("reference-triple-distribution")) {
		    	int percentage = Integer.parseInt( line.getOptionValue("encoding"));
		    	NodeDictionaryFactory.getDictionary().setReferenceTripleDistribution(percentage);
		    } 
		    
		    if (line.hasOption("log4j")) {
				setLoggingLevel(line.getOptionValue("log4j"));
			}
		    
		} catch( ParseException exp ) {
			printHelp(exp, options);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		writeExperimentalSetupToLog();
		
		validateBitEncoding();
		
		// register the .ttls reader and parser
        registerTTLS();
        
        // register the sparql* query parser and optimizer
        registerQueryEngine();
        
        loadData(filename, model);
        
        deleteDuplicateTriples(g);
        
        evaluateQueries(queries, model);
    }

	private static void writeExperimentalSetupToLog() {
		log.info("----------------------------- "+new Date().toString()+" -----------------------------");
		log.info("Dataset path: "+Config.getLocation());
		log.info("Query count: "+ queries.size());
		log.info("Dictionary type: "+Config.getDictionaryType());
		log.info("Index type: "+ Config.getIndex());
		log.info("Prefix dictionary is enabled: "+Config.isPrefixDictionaryEnabled());
		log.info("EmbeddedTriple encoding head:"+ Config.getEmbeddedHeaderSize()+
				" Subject: "+Config.getSubjectSizeInBits()+
				" Predicate "+Config.getPredicateSizeInBits()+
				" Object: "+Config.getObjectSizeInBits());
		log.info("Log4J logging level:" + Logger.getRootLogger().getLevel().toString());
		log.info("");
		
	}

	private static void evaluateQueries(List<String> queries, Model model) {
		long start_time;
		log.info("Evaluating queries:");
		
		start_time = System.nanoTime();
	   	NodeDictionaryFactory.getDictionary().open();
	   	log.info("Dictionary initialization: "+(System.nanoTime() - start_time) / 1e6+" ms");
		
        for (String queryString : queries) {
        	
            try{
            	start_time = System.nanoTime();
            	 Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
            	 log.info(query.toString());
            	 log.info("Query creation: "+(System.nanoTime() - start_time) / 1e6+" ms");
            	 
            	 start_time = System.nanoTime();
            	 QueryExecution qexec = QueryExecutionFactory.create(query, model);
                 ResultSet rs = qexec.execSelect() ;
                 log.info(ResultSetFormatter.asText(rs));
                 log.info("Evaluation finished: "+(System.nanoTime() - start_time) / 1e6+" ms");
                 
                 
			} catch (Exception e) {
				NodeDictionaryFactory.getDictionary().clear();
				log.warn(e.toString());
            }
		}
        NodeDictionaryFactory.getDictionary().close();
	}

	private static void deleteDuplicateTriples(Graph g) {
		log.info("Deleting duplicates");
		long start_time = System.nanoTime();
        int count = g.eliminateDuplicates();
        log.info("Number of triples deleted: "+count);
        log.info("Deleting duplicates finished: "+(System.nanoTime() - start_time) / 1e6+" ms");
        log.info("");
	}

	private static void loadData(String filename, Model model) {
		long start_time;
		try {
        	NodeDictionaryFactory.getDictionary().open();
        	log.info("Loading file: "+filename);
        	start_time = System.nanoTime();
            RDFDataMgr.read(model, filename);
            double time = (System.nanoTime() - start_time) / 1e6;
            log.info("Loading finished: "+time+" ms");
            log.info("Number of triples: "+g.getNumberOfTriples());
            log.info("average load time: "+g.getNumberOfTriples()/time*1000 +" triples per second");
            log.info("");
            NodeDictionaryFactory.getDictionary().logStatistics();
            NodeDictionaryFactory.getDictionary().close();
            
		}  catch (Exception e) {
			NodeDictionaryFactory.getDictionary().clear();
			log.warn(e.toString());
        }
	}

	private static void setLoggingLevel(String optionValue) {
		if (optionValue.equals("All")) {
			Logger.getRootLogger().setLevel(Level.ALL);
		} else if (optionValue.equals("Debug")) {
			Logger.getRootLogger().setLevel(Level.DEBUG);
		} else if (optionValue.equals("Error")) {
			Logger.getRootLogger().setLevel(Level.ERROR);
		} else if (optionValue.equals("Fatal")) {
			Logger.getRootLogger().setLevel(Level.FATAL);
		} else if (optionValue.equals("Error")) {
			Logger.getRootLogger().setLevel(Level.ERROR);
		} else if (optionValue.equals("Off")) {
			Logger.getRootLogger().setLevel(Level.OFF);
		} else if (optionValue.equals("Trace")) {
			Logger.getRootLogger().setLevel(Level.TRACE);
		} else if (optionValue.equals("Warn")) {
			Logger.getRootLogger().setLevel(Level.WARN);
		} 
	}

	public static void validateBitEncoding() {
		if (Config.getSubjectSizeInBits()+Config.getPredicateSizeInBits()+Config.getObjectSizeInBits() > 62) {
			throw new IllegalArgumentException("The bit encoding is invalid. No more than 62 bits can be allocated" );
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

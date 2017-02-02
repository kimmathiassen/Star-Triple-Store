package dk.aau.cs.qweb.main;

import java.util.Iterator;

import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.atlas.web.TypedInputStream;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.LangBuilder;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RDFParserRegistry;
import org.apache.jena.riot.ReaderRIOTFactory;
import org.apache.jena.riot.RiotNotFoundException;
import org.apache.jena.riot.system.stream.StreamManager;
import org.apache.jena.vocabulary.DC;

import dk.aau.cs.qweb.graph.Graph;
import dk.aau.cs.qweb.turtlestar.TTLSReaderFactory;

public class App {
	static public final String NL = System.getProperty("line.separator") ; 
	
	
	public static void main(String[] args) {
		Graph g = new Graph();
		String filename = "src/test/resources/TurtleStar/embedded.ttls" ;

        registerTTLS();
    	
        RDFDataMgr.read(g, filename);
        
        // Direct way: Make a TDB-back Jena model in the named directory.
        //String directory = "MyDatabases/DB1" ;
        //Dataset dataset = TDBFactory.createDataset(directory) ;
        
        ////////////////////////////////////////////////////////////
//        TypedInputStream in = open(filename) ;
//        PeekReader peekReader = PeekReader.makeUTF8(in) ;
//        TokenizerStar tokenizer = new TokenizerStar(peekReader) ;
//        
//        ErrorHandler handler = ErrorHandlerFactory.getDefaultErrorHandler();
//        
//        LabelToNode labelMapping = SyntaxLabels.createLabelToNode();
//        Prologue prologue = new Prologue(PrefixMapFactory.createForInput(), IRIResolver.create(filename)) ;
//        ParserProfileStar profile = new ParserProfileStar(prologue, handler, new FactoryRDFCaching(FactoryRDFCaching.DftNodeCacheSize, labelMapping)) ;
//        StreamRDF dest = StreamRDFLib.graph(g) ;
//        LangTurtleStar parser = new LangTurtleStar(tokenizer, profile, dest); 
//        parser.parse();
        
        
		Node subject = Node.ANY;
		Node predicate = NodeFactory.createURI("http://example.org/embedded/is");
		Node object = Node.ANY;
		
		Triple triplePattern = new Triple(subject,predicate,object );
		
		Iterator<Triple> iterator = g.graphBaseFind(triplePattern);
		int count = 0;
		
		while (iterator.hasNext()) {
			iterator.next();
			count++;
		}
		System.out.println(count);
        
        System.exit(0);
        Model test2 = ModelFactory.createModelForGraph(g);
        
        
        test2 = populateModel(test2);
        //dataset.begin(ReadWrite.WRITE) ;
        
        //Model model = createModel(dataset);
        
        // First part or the query string 
        String prolog = "PREFIX dc: <"+DC.getURI()+">" ;
        
        // Query string.
        String queryString = prolog + NL +
            "SELECT ?title WHERE {?x dc:title ?title. ?x dc:description ?y .}" ; 
        
        Query query = QueryFactory.create(queryString) ;
        // Print with line numbers
        query.serialize(new IndentedWriter(System.out,true)) ;
        System.out.println() ;
        
        // Create a single execution of this query, apply to a model
        // which is wrapped up as a Dataset
        
        try(QueryExecution qexec = QueryExecutionFactory.create(query, test2)){
            // Or QueryExecutionFactory.create(queryString, model) ;

            System.out.println("Titles: ") ;

            // Assumption: it's a SELECT query.
            ResultSet rs = qexec.execSelect() ;

            // The order of results is undefined. 
            for ( ; rs.hasNext() ; )
            {
                QuerySolution rb = rs.nextSolution() ;

                // Get title - variable names do not include the '?' (or '$')
                RDFNode x = rb.get("title") ;

                // Check the type of the result value
                if ( x.isLiteral() )
                {
                    Literal titleStr = (Literal)x  ;
                    System.out.println("    "+titleStr) ;
                }
                else
                    System.out.println("Strange - not a literal: "+x) ;
            }
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

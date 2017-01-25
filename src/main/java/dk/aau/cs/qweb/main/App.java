package dk.aau.cs.qweb.main;

import org.apache.jena.atlas.io.IndentedWriter;
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
import org.apache.jena.vocabulary.DC;

import dk.aau.cs.qweb.triplestore.MyGraph;

public class App {
	static public final String NL = System.getProperty("line.separator") ; 
	
	public static void main(String[] args) {
		//MyQueryEngine.register();
    	
        // Direct way: Make a TDB-back Jena model in the named directory.
        //String directory = "MyDatabases/DB1" ;
        //Dataset dataset = TDBFactory.createDataset(directory) ;
        
        
        MyGraph g = new MyGraph();
        Model test2 = ModelFactory.createModelForGraph(g);
        
        
        test2 = populateModel(test2);
        //dataset.begin(ReadWrite.WRITE) ;
        
        //Model model = createModel(dataset);
        
        // First part or the query string 
        String prolog = "PREFIX dc: <"+DC.getURI()+">" ;
        
        // Query string.
        String queryString = prolog + NL +
            "SELECT ?title WHERE {?x dc:title ?title}" ; 
        
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
    
    public static Model populateModel(Model m)
    {
    	
        Resource r1 = m.createResource("http://example.org/book#1") ;
        Resource r2 = m.createResource("http://example.org/book#2") ;
        
        r1.addProperty(DC.title, "SPARQL - the book")
          .addProperty(DC.description, "A book about SPARQL") ;
        
        r2.addProperty(DC.title, "Advanced techniques for SPARQL") ;
        
        return m ;
    }

	

}

package dk.aau.cs.qweb.queryparser;

import java.io.Reader;
import java.io.StringReader;

import org.apache.jena.atlas.logging.Log;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryException;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.shared.JenaException;
import org.apache.jena.sparql.lang.ParserSPARQL11;
import org.apache.jena.sparql.lang.SPARQLParser;
import org.apache.jena.sparql.lang.SyntaxVarScope;
import org.apache.jena.sparql.lang.sparql_11.ParseException;

public class ParserSPARQLStar extends SPARQLParser {

	private interface Action { void exec(SPARQLStarParser11 parser) throws Exception ; }
	
	@Override
	public Query parse$ ( Query query, String queryString ) throws QueryParseException
	{
		query.setSyntax( SyntaxStar.syntaxSPARQL_Star );
		
        Action action = new Action() {
            @Override
            public void exec(SPARQLStarParser11 parser) throws ParseException 
            {
                parser.QueryUnit() ;
            }
        } ;

        perform(query, queryString, action) ;
        validateParsedQuery(query) ;
		return query;
	}
	
	@Override
	protected void validateParsedQuery ( Query query ) 
	{
		SyntaxVarScope.check(query) ;
	}
	  // All throwable handling.
    private static void perform(Query query, String string, Action action)
    {
        Reader in = new StringReader(string) ;
        SPARQLStarParser11 parser = new SPARQLStarParser11(in) ;

        try {
            query.setStrict(false) ;
            parser.setQuery(query) ;
            action.exec(parser) ;
        }
        catch (org.apache.jena.sparql.lang.sparql_11.ParseException ex)
        { 
            throw new QueryParseException(ex.getMessage(),
                                          ex.currentToken.beginLine,
                                          ex.currentToken.beginColumn
                                          ) ; }
        catch (org.apache.jena.sparql.lang.sparql_11.TokenMgrError tErr)
        {
            // Last valid token : not the same as token error message - but this should not happen
            int col = parser.token.endColumn ;
            int line = parser.token.endLine ;
            throw new QueryParseException(tErr.getMessage(), line, col) ; }
        
        catch (QueryException ex) { throw ex ; }
        catch (JenaException ex)  { throw new QueryException(ex.getMessage(), ex) ; }
        catch (Error err)
        {
            System.err.println(err.getMessage()) ;
            // The token stream can throw errors.
            throw new QueryParseException(err.getMessage(), err, -1, -1) ;
        }
        catch (Throwable th)
        {
            Log.warn(ParserSPARQL11.class, "Unexpected throwable: ",th) ;
            throw new QueryException(th.getMessage(), th) ;
        }
    }
	

}

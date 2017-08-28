package dk.aau.cs.qweb.queryparser;

import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.lang.SPARQLParser;
import org.apache.jena.sparql.lang.SPARQLParserFactory;

/**
 * Class for registering the parser and ensure it is selected to answer query using the SPARQL* syntax
 *
 */
public class SPARQLParserFactoryStar implements SPARQLParserFactory {

	
	@Override
	public boolean accept(Syntax syntax) {
		return syntax.equals(SyntaxStar.syntaxSPARQL_Star);
	}

	@Override
	public SPARQLParser create(Syntax syntax) {
		return new ParserSPARQLStar();
	}

}

package dk.aau.cs.qweb.main.queryparser;

import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.lang.SPARQLParser;
import org.apache.jena.sparql.lang.SPARQLParserFactory;

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

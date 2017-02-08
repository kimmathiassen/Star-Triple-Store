package dk.aau.cs.qweb.main.queryparser;

import org.apache.jena.query.Syntax;

public class SyntaxStar extends Syntax {

	protected SyntaxStar(String s) {
		super(s);
	}
	
    public static final Syntax syntaxSPARQL_Star
                = new SyntaxStar("http://rdfstar.com") ;

}

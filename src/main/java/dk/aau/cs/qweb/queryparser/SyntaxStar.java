package dk.aau.cs.qweb.queryparser;

import org.apache.jena.query.Syntax;

/**
 * The syntax class for sparql star.
 *
 */
public class SyntaxStar extends Syntax {

	protected SyntaxStar(String s) {
		super(s);
	}
	
    public static final Syntax syntaxSPARQL_Star
                = new SyntaxStar("http://rdfstar.com") ;

}

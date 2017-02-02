package dk.aau.cs.qweb.turtlestar;

import static dk.aau.cs.qweb.turtlestar.TokenTypeStar.COMMA;
import static dk.aau.cs.qweb.turtlestar.TokenTypeStar.DIRECTIVE;
import static dk.aau.cs.qweb.turtlestar.TokenTypeStar.DOT;
import static dk.aau.cs.qweb.turtlestar.TokenTypeStar.EOF;
import static dk.aau.cs.qweb.turtlestar.TokenTypeStar.IRI;
import static dk.aau.cs.qweb.turtlestar.TokenTypeStar.KEYWORD;
import static dk.aau.cs.qweb.turtlestar.TokenTypeStar.LBRACE;
import static dk.aau.cs.qweb.turtlestar.TokenTypeStar.LBRACKET;
import static dk.aau.cs.qweb.turtlestar.TokenTypeStar.LPAREN;
import static dk.aau.cs.qweb.turtlestar.TokenTypeStar.NODE;
import static dk.aau.cs.qweb.turtlestar.TokenTypeStar.PREFIXED_NAME;
import static dk.aau.cs.qweb.turtlestar.TokenTypeStar.RBRACKET;
import static dk.aau.cs.qweb.turtlestar.TokenTypeStar.RPAREN;
import static dk.aau.cs.qweb.turtlestar.TokenTypeStar.SEMICOLON;

import org.apache.jena.atlas.AtlasException;
import org.apache.jena.atlas.iterator.PeekIterator;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.iri.IRI;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RiotParseException;
import org.apache.jena.riot.system.ErrorHandler;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.sparql.graph.NodeConst;

public class LangTurtleStar {

    protected final StreamRDF dest ; 
    protected ParserProfileStar profile ;
    protected final TokenizerStar tokens ;
    private PeekIterator<TokenStar> peekIter ;

    protected final static String  KW_A           = "a" ;
    protected final static String  KW_SAME_AS     = "=" ;
    protected final static String  KW_LOG_IMPLIES = "=>" ;
    protected final static String  KW_TRUE        = "true" ;
    protected final static String  KW_FALSE       = "false" ;

    protected final static boolean VERBOSE        = false ;
    // protected final static boolean CHECKING = true ;
    // Current graph - null for default graph
    private Node                   currentGraph   = null ;



    protected LangTurtleStar(TokenizerStar tokens, ParserProfileStar profile, StreamRDF dest)
    {
    	setCurrentGraph(null) ;
        this.dest = dest ;
        this.tokens = tokens ;
        this.profile = profile ;
        // The PeekIterator is always loaded with the next token until the end
        // (for simplicity) but it measn this can throw an exception. 
        try { this.peekIter = new PeekIterator<>(tokens) ; }
        catch (RiotParseException ex) { raiseException(ex) ; }
    }
    
    public void parse()
    {
        dest.base(profile.getPrologue().getBaseURI()) ;
        dest.start() ;
        try { 
            runParser() ;
        } finally {
            dest.finish() ;
            tokens.close();
        }
    }
    

    public ParserProfileStar getProfile()
    {
        return profile ;
    }

    public void setProfile(ParserProfileStar profile)
    {
        this.profile = profile ; 
    }	
	
 

    public Lang getLang() {
        return RDFLanguages.TURTLE ;
    }

    protected final void oneTopLevelElement() {
        triplesSameSubject() ;
    }

    protected void expectEndOfTriples() {
        expectEndOfTriplesTurtle() ;
    }

    protected void emit(Node subject, Node predicate, Node object) {
        Triple t = profile.createTriple(subject, predicate, object, currLine, currCol) ;
        dest.triple(t) ;
    }
    
    public final Node getCurrentGraph() {
        return currentGraph ;
    }

    public final void setCurrentGraph(Node graph) {
        this.currentGraph = graph ;
    }

 

    protected final void runParser() {
        while (moreTokens()) {
            TokenStar t = peekToken() ;
            if ( lookingAt(DIRECTIVE) ) {
                directive() ; // @form.
                continue ;
            }

            if ( lookingAt(KEYWORD) ) {
                if ( t.getImage().equalsIgnoreCase("PREFIX") || t.getImage().equalsIgnoreCase("BASE") ) {
                    directiveKeyword() ;
                    continue ;
                }
            }

            oneTopLevelElement() ;

            if ( lookingAt(EOF) )
                break ;
        }
    }


    protected final void directiveKeyword() {
    	TokenStar t = peekToken() ;
        String x = t.getImage() ;
        nextToken() ;

        if ( x.equalsIgnoreCase("BASE") ) {
            directiveBase() ;
            return ;
        }

        if ( x.equalsIgnoreCase("PREFIX") ) {
            directivePrefix() ;
            return ;
        }
        exception(t, "Unrecognized keyword for directive: %s", x) ;
    }

    protected final void directive() {
        // It's a directive ...
    	TokenStar t = peekToken() ;
        String x = t.getImage() ;
        nextToken() ;

        if ( x.equals("base") ) {
            directiveBase() ;
            if ( profile.isStrictMode() )
                // The line number is probably one ahead due to the newline
                expect("Base directive not terminated by a dot", DOT) ;
            else
                skipIf(DOT) ;
            return ;
        }

        if ( x.equals("prefix") ) {
            directivePrefix() ;
            if ( profile.isStrictMode() )
                // The line number is probably one ahead due to the newline
                expect("Prefix directive not terminated by a dot", DOT) ;   
            else
                skipIf(DOT) ;
            return ;
        }
        exception(t, "Unrecognized directive: %s", x) ;
    }

    protected final void directivePrefix() {
        // Raw - unresolved prefix name.
        if ( !lookingAt(PREFIXED_NAME) )
            exception(peekToken(), "@prefix or PREFIX requires a prefix (found '" + peekToken() + "')") ;
        if ( peekToken().getImage2().length() != 0 )
            exception(peekToken(), "@prefix or PREFIX requires a prefix with no suffix (found '" + peekToken() + "')") ;
        String prefix = peekToken().getImage() ;
        nextToken() ;
        if ( !lookingAt(IRI) )
            exception(peekToken(), "@prefix requires an IRI (found '" + peekToken() + "')") ;
        String iriStr = peekToken().getImage() ;
        IRI iri = profile.makeIRI(iriStr, currLine, currCol) ;
        profile.getPrologue().getPrefixMap().add(prefix, iri) ;
        emitPrefix(prefix, iri.toString()) ;
        nextToken() ;
    }

    protected final void directiveBase() {
    	TokenStar token = peekToken() ;
        if ( !lookingAt(IRI) )
            exception(token, "@base requires an IRI (found '" + token + "')") ;
        String baseStr = token.getImage() ;
        IRI baseIRI = profile.makeIRI(baseStr, currLine, currCol) ;
        emitBase(baseIRI.toString()) ;
        nextToken() ;
        profile.getPrologue().setBaseURI(baseIRI) ;
    }

    

    // Unlike many operations in this parser suite
    // this does not assume that we have definitely
    // entering this state. It does checks and may
    // signal a parse exception.

    protected final void triplesSameSubject() {
        // Either a IRI/prefixed name or a construct that generates triples

        // TriplesSameSubject -> Term PropertyListNotEmpty
        if ( lookingAt(NODE) ) {
            triples() ;
            return ;
        } else if  ( lookingAt(TokenTypeStar.EMBEDDED) ) {
            triples() ;
            return ;
        }

        boolean maybeList = lookingAt(LPAREN) ;
        
        // Turtle: TriplesSameSubject -> TriplesNode PropertyList?
        // TriG:   (blankNodePropertyList | collection) predicateObjectList? '.'
        //         labelOrSubject (wrappedGraph | predicateObjectList '.')
        if ( peekTriplesNodeCompound() ) {
            Node n = triplesNodeCompound() ;

            // May be followed by:
            // A predicateObject list
            // A DOT or EOF.
            // But if a DOT or EOF, then it can't have been () or [].

            // Turtle, as spec'ed does not allow
            // (1 2 3 4) .
            // There must be a predicate and object.

            // -- If strict turtle.
            if ( profile.isStrictMode() && maybeList ) {
                if ( peekPredicate() ) {
                    predicateObjectList(n) ;
                    expectEndOfTriples() ;
                    return ;
                }
                exception(peekToken(), "Predicate/object required after (...) - Unexpected token : %s",
                          peekToken()) ;
            }
            // ---
            // If we allow top-level lists and [...].
            // Should check if () and [].

            if ( lookingAt(EOF) )
                return ;
            if ( lookingAt(DOT) ) {
                nextToken() ;
                return ;
            }

            if ( peekPredicate() )
                predicateObjectList(n) ;
            expectEndOfTriples() ;
            //exception(peekToken(), "Unexpected token : %s", peekToken()) ;
            return ;
        }
        exception(peekToken(), "Out of place: %s", peekToken()) ;
    }

    // Must be at least one triple.
    protected final void triples() {
        // Looking at a node.
        Node subject = node() ;
        if ( subject == null )
            exception(peekToken(), "Not recognized: expected node: %s", peekToken().text()) ;

        nextToken() ;
        predicateObjectList(subject) ;
        expectEndOfTriples() ;
    }

    
    // The DOT is required by Turtle (strictly).
    // It is not in N3 and SPARQL.
    
    protected void expectEndOfTriplesTurtle() {
        if ( profile.isStrictMode() )
            expect("Triples not terminated by DOT", DOT) ;
        else
            expectOrEOF("Triples not terminated by DOT", DOT) ;
    }
        
    protected final void predicateObjectList(Node subject) {
        predicateObjectItem(subject) ;

        for (;;) {
            if ( !lookingAt(SEMICOLON) )
                break ;
            // predicatelist continues - move over all ";"
            while (lookingAt(SEMICOLON))
                nextToken() ;
            if ( !peekPredicate() )
                // Trailing (pointless) SEMICOLONs, no following
                // predicate/object list.
                break ;
            predicateObjectItem(subject) ;
        }
    }

    protected final void predicateObjectItem(Node subject) {
        Node predicate = predicate() ;
        nextToken() ;
        objectList(subject, predicate) ;
    }

    static protected final Node nodeSameAs     = NodeConst.nodeOwlSameAs ;
    static protected final Node nodeLogImplies = NodeFactory.createURI("http://www.w3.org/2000/10/swap/log#implies") ;

    /** Get predicate - maybe null for "illegal" */
    protected final Node predicate() {
    	TokenStar t = peekToken() ;

        if ( t.hasType(TokenTypeStar.KEYWORD) ) {
            boolean strict = profile.isStrictMode() ;
            TokenStar tErr = peekToken() ;
            String image = peekToken().getImage() ;
            if ( image.equals(KW_A) )
                return NodeConst.nodeRDFType ;
            if ( !strict && image.equals(KW_SAME_AS) )
                return nodeSameAs ;
            if ( !strict && image.equals(KW_LOG_IMPLIES) )
                return NodeConst.nodeRDFType ;
            exception(tErr, "Unrecognized: " + image) ;
        }

        Node n = node() ;
        if ( n == null || !n.isURI() )
            exception(t, "Expected IRI for predicate: got: %s", t) ;
        return n ;
    }

    /** Check raw token to see if it might be a predciate */
    protected final boolean peekPredicate() {
        if ( lookingAt(TokenTypeStar.KEYWORD) ) {
            String image = peekToken().getImage() ;
            boolean strict = profile.isStrictMode() ;
            if ( image.equals(KW_A) )
                return true ;
            if ( !strict && image.equals(KW_SAME_AS) )
                return true ;
            if ( !strict && image.equals(KW_LOG_IMPLIES) )
                return true ;
            return false ;
        }
        // if ( lookingAt(NODE) )
        // return true ;
        if ( lookingAt(TokenTypeStar.IRI) )
            return true ;
        if ( lookingAt(TokenTypeStar.PREFIXED_NAME) )
            return true ;
        return false ;
    }

    /** Maybe "null" for not-a-node. */
    protected final Node node() {
        // Token to Node
        Node n = tokenAsNode(peekToken()) ;
        if ( n == null )
            return null ;
        return n ;
    }

    protected final void objectList(Node subject, Node predicate) {
        for (;;) {
            Node object = triplesNode() ;
            emitTriple(subject, predicate, object) ;

            if ( !moreTokens() )
                break ;
            if ( !lookingAt(COMMA) )
                break ;
            // list continues - move over the ","
            nextToken() ;
        }
    }

    // A structure of triples that itself generates a node.
    // Special checks for [] and ().

    protected final Node triplesNode() {
        if ( lookingAt(NODE) ) {
            Node n = node() ;
            nextToken() ;
            return n ;
        }

        // Special words.
        if ( lookingAt(TokenTypeStar.KEYWORD) ) {
            TokenStar tErr = peekToken() ;
            // Location independent node words
            String image = peekToken().getImage() ;
            nextToken() ;
            if ( image.equals(KW_TRUE) )
                return NodeConst.nodeTrue ;
            if ( image.equals(KW_FALSE) )
                return NodeConst.nodeFalse ;
            if ( image.equals(KW_A) )
                exception(tErr, "Keyword 'a' not legal at this point") ;

            exception(tErr, "Unrecognized keyword: " + image) ;
        }

        return triplesNodeCompound() ;
    }

    protected final boolean peekTriplesNodeCompound() {
        if ( lookingAt(LBRACKET) )
            return true ;
        if ( lookingAt(LBRACE) )
            return true ;
        if ( lookingAt(LPAREN) )
            return true ;
        return false ;
    }

    protected final Node triplesNodeCompound() {
        if ( lookingAt(LBRACKET) )
            return triplesBlankNode() ;
        if ( lookingAt(LBRACE) )
            return triplesFormula() ;
        if ( lookingAt(LPAREN) )
            return triplesList() ;
        exception(peekToken(), "Unrecognized: " + peekToken()) ;
        return null ;
    }

    protected final Node triplesBlankNode() {
        TokenStar t = nextToken() ; // Skip [
        Node subject = profile.createBlankNode(currentGraph, t.getLine(), t.getColumn()) ;
        triplesBlankNode(subject) ;
        return subject ;
    }

    protected final void triplesBlankNode(Node subject) {
        if ( peekPredicate() )
            predicateObjectList(subject) ;
        expect("Triples not terminated properly in []-list", RBRACKET) ;
        // Exit: after the ]
    }

    protected final Node triplesFormula() {
        exception(peekToken(), "Not implemented (formulae, graph literals)") ;
        return null ;
    }

    protected final Node triplesList() {
        nextToken() ;
        Node lastCell = null ;
        Node listHead = null ;

        startList() ;

        for (;;) {
            TokenStar errorToken = peekToken() ;
            if ( eof() )
                exception(peekToken(), "Unterminated list") ;

            if ( lookingAt(RPAREN) ) {
                nextToken() ;
                break ;
            }

            // The value.
            Node n = triplesNode() ;

            if ( n == null )
                exception(errorToken, "Malformed list") ;

            // Node for the list structre.
            Node nextCell = NodeFactory.createBlankNode() ;
            if ( listHead == null )
                listHead = nextCell ;
            if ( lastCell != null )
                emitTriple(lastCell, NodeConst.nodeRest, nextCell) ;
            lastCell = nextCell ;

            emitTriple(nextCell, NodeConst.nodeFirst, n) ;

            if ( !moreTokens() ) // Error.
                break ;
        }
        // On exit, just after the RPARENS

        if ( lastCell == null )
            // Simple ()
            return NodeConst.nodeNil ;

        // Finish list.
        emitTriple(lastCell, NodeConst.nodeRest, NodeConst.nodeNil) ;

        finishList() ;

        return listHead ;
    }

    // Signal start of a list
    protected void finishList() {}

    // Signal end of a list
    protected void startList() {}

    protected final void emitTriple(Node subject, Node predicate, Node object) {
        emit(subject, predicate, object) ;
    }

    private final void emitPrefix(String prefix, String iriStr) {
        dest.prefix(prefix, iriStr) ; 
    }

    private final void emitBase(String baseStr) { 
        dest.base(baseStr);
    }

    protected final Node tokenAsNode(TokenStar token) {
        return profile.create(currentGraph, token) ;
    }
    


    
    // ---- Managing tokens.
    
    protected final TokenStar peekToken()
    {
        // Avoid repeating.
        if ( eof() ) return tokenEOF ;
        return peekIter.peek() ;
    }
    
    // Set when we get to EOF to record line/col of the EOF.
    private TokenStar tokenEOF = null ;

    protected final boolean eof()
    {
        if ( tokenEOF != null )
            return true ;
        
        if ( ! moreTokens() )
        {
            tokenEOF = new TokenStar(tokens.getLine(), tokens.getColumn()) ;
            tokenEOF.setType(EOF) ;
            return true ;
        }
        return false ;
    }

    protected final boolean moreTokens() 
    {
        return peekIter.hasNext() ;
    }
    
    protected final boolean lookingAt(TokenTypeStar tokenType)
    {
        if ( eof() )
            return tokenType == EOF ;
        if ( tokenType == NODE )
            return peekToken().isNode() ;
        return peekToken().hasType(tokenType) ;
    }
    
    // Remember line/col of last token for messages 
    protected long currLine = -1 ;
    protected long currCol = -1 ;
    
    protected final TokenStar nextToken()
    {
        if ( eof() )
            return tokenEOF ;
        
        // Tokenizer errors appear here!
        try {
            TokenStar t = peekIter.next() ;
            currLine = t.getLine() ;
            currCol = t.getColumn() ;
            return t ;
        } catch (RiotParseException ex)
        {
            // Intercept to log it.
            raiseException(ex) ;
            throw ex ;
        }
        catch (AtlasException ex)
        {
            // Bad I/O
            RiotParseException ex2 = new RiotParseException(ex.getMessage(), -1, -1) ;
            raiseException(ex2) ;
            throw ex2 ;
        }
    }

//	    protected final Node scopedBNode(Node scopeNode, String label)
//	    {
//	        return profile.getLabelToNode().get(scopeNode, label) ;
//	    }
//    
    protected final void expectOrEOF(String msg, TokenTypeStar tokenType)
    {
        // DOT or EOF
        if ( eof() )
            return ;
        expect(msg, tokenType) ;
    }
    
    protected final void skipIf(TokenTypeStar ttype) {
        if ( lookingAt(ttype) )
            nextToken() ;
    }
    
    protected final void expect(String msg, TokenTypeStar ttype)
    {
        if ( ! lookingAt(ttype) )
        {
        	TokenStar location = peekToken() ;
            exception(location, msg) ;
        }
        nextToken() ;
    }

    protected final void exception(TokenStar token, String msg, Object... args)
    { 
        if ( token != null )
            exceptionDirect(String.format(msg, args), token.getLine(), token.getColumn()) ;
        else
            exceptionDirect(String.format(msg, args), -1, -1) ;
    }

    protected final void exceptionDirect(String msg, long line, long col)
    { 
        raiseException(new RiotParseException(msg, line, col)) ;
    }
    
    protected final void raiseException(RiotParseException ex)
    { 
        ErrorHandler errorHandler = profile.getHandler() ; 
        if ( errorHandler != null )
            errorHandler.fatal(ex.getOriginalMessage(), ex.getLine(), ex.getCol()) ;
        throw ex ;
    }
    
}

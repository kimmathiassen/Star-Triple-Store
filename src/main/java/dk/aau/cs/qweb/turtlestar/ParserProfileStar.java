package dk.aau.cs.qweb.turtlestar;

import java.util.Objects;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.iri.IRI;
import org.apache.jena.query.ARQ;
import org.apache.jena.riot.RiotException;
import org.apache.jena.riot.SysRIOT;
import org.apache.jena.riot.system.ErrorHandler;
import org.apache.jena.riot.system.FactoryRDF;
import org.apache.jena.riot.system.ParserProfileBase;
import org.apache.jena.riot.system.Prologue;
import org.apache.jena.riot.system.RiotLib;
import org.apache.jena.sparql.core.Quad;

import dk.aau.cs.qweb.node.EmbeddedNode;
import dk.aau.cs.qweb.node.NodeFactoryStar;
import dk.aau.cs.qweb.node.SimpleNode;

/**
 * The parser for turtle star files.
 * It parses a tree of tokens and create "nodes" that correspond to the elements of a triple.
 * This class is mostly a copy of the {@link ParserProfileBase}, however, with custom handling of embedded triples and instead of generating Jena {@link Node} we create {@link SimpleNode} and {@link EmbeddedNode}.
 * This is essential because the Jena nodes used some string caching that consumed to much memory. Instead we use a dictionary.  
 *
 */
public class ParserProfileStar {
	protected ErrorHandler errorHandler ;
    protected Prologue     prologue ;
    protected boolean      strictMode = SysRIOT.isStrictMode() ;
    protected FactoryRDF   factory ;
	protected NodeFactoryStar factoryStar;

    public ParserProfileStar(Prologue prologue, ErrorHandler errorHandler) {
        this(prologue, errorHandler, RiotLib.factoryRDF()) ;
    }

    public ParserProfileStar(Prologue prologue, ErrorHandler errorHandler, FactoryRDF factory) {
        Objects.requireNonNull(prologue) ;
        Objects.requireNonNull(errorHandler) ;
        Objects.requireNonNull(factory) ;
        this.prologue = prologue ;
        this.errorHandler = errorHandler ;
        this.factory = factory ;
        this.factoryStar = new NodeFactoryStar();
    }

    public ErrorHandler getHandler() {
        return errorHandler ;
    }

    public void setHandler(ErrorHandler handler) {
        errorHandler = handler ;
    }

    public Prologue getPrologue() {
        return prologue ;
    }

    public void setPrologue(Prologue p) {
        prologue = p ;
    }

    public FactoryRDF getFactoryRDF() {
        return factory;
    }

    public void setFactoryRDF(FactoryRDF factory) {
        this.factory = factory;
    }
   
    public String resolveIRI(String uriStr, long line, long col) {
        return prologue.getResolver().resolveToString(uriStr) ;
    }

    public IRI makeIRI(String uriStr, long line, long col) {
        return prologue.getResolver().resolve(uriStr) ;
    }

    public Quad createQuad(Node g, Node s, Node p, Node o, long line, long col) {
        return factory.createQuad(g, s, p, o);
    }

    public Triple createTriple(Node s, Node p, Node o, long line, long col) {
        return factory.createTriple(s, p, o);
    }

    public Node createURI(String uriStr, long line, long col) {
    	return NodeFactoryStar.createSimpleURINode(uriStr);
        //return factory.createURI(uriStr);
    }

    public Node createBlankNode(Node scope, String label, long line, long col) {
        //return factory.createBlankNode(label);
    	return NodeFactoryStar.createSimpleBlankNode(factory.createBlankNode(label));
    }

    public Node createBlankNode(Node scope, long line, long col) {
    	return NodeFactoryStar.createSimpleBlankNode(factory.createBlankNode());
        //return factory.createBlankNode();
    }

    public Node createTypedLiteral(String lexical, RDFDatatype dt, long line, long col) {
    	return NodeFactoryStar.createSimpleLiteralNode(lexical,dt);
        //return factory.createTypedLiteral(lexical, dt);
    }

    public Node createLangLiteral(String lexical, String langTag, long line, long col) {
    	return NodeFactoryStar.createSimpleLiteralNode(lexical,langTag);
        //return factory.createLangLiteral(lexical, langTag);
    }

    public Node createStringLiteral(String lexical, long line, long col) {
    	return NodeFactoryStar.createSimpleLiteralNode(lexical);
        //return factory.createStringLiteral(lexical);
    }
  
    /** Special token forms */
    public Node createNodeFromToken(Node scope, TokenStar token, long line, long col) {
        // OFF - Don't produce Node.ANY by default.
        return null ;
    }

    public Node create(Node currentGraph, TokenStar token) {
        return create(this, currentGraph, token) ;
    }
        
    private static Node create(ParserProfileStar pp, Node currentGraph, TokenStar token) {
        // Dispatches to the underlying ParserProfile operation
        long line = token.getLine() ;
        long col = token.getColumn() ;
        String str = token.getImage() ;
        switch (token.getType()) {
            case BNODE :
                return pp.createBlankNode(currentGraph, str, line, col) ;
            case IRI :
                return pp.createURI(str, line, col) ;
            case EMBEDDED :
                return pp.createEmbedded(pp, currentGraph, token) ;
            case PREFIXED_NAME : {
                String prefix = str ;
                String suffix = token.getImage2() ;
                String expansion = expandPrefixedName(pp, prefix, suffix, token) ;
                return pp.createURI(expansion, line, col) ;
            }
            case DECIMAL :
                return pp.createTypedLiteral(str, XSDDatatype.XSDdecimal, line, col) ;
            case DOUBLE :
                return pp.createTypedLiteral(str, XSDDatatype.XSDdouble, line, col) ;
            case INTEGER :
                return pp.createTypedLiteral(str, XSDDatatype.XSDinteger, line, col) ;
            case LITERAL_DT : {
                TokenStar tokenDT = token.getSubToken2() ;
                String uriStr ;

                switch (tokenDT.getType()) {
                    case IRI :
                        uriStr = tokenDT.getImage() ;
                        break ;
                    case PREFIXED_NAME : {
                        String prefix = tokenDT.getImage() ;
                        String suffix = tokenDT.getImage2() ;
                        uriStr = expandPrefixedName(pp, prefix, suffix, tokenDT) ;
                        break ;
                    }
                    default :
                        throw new RiotException("Expected IRI for datatype: " + token) ;
                }

                uriStr = pp.resolveIRI(uriStr, tokenDT.getLine(), tokenDT.getColumn()) ;
                RDFDatatype dt = NodeFactory.getType(uriStr) ;
                return pp.createTypedLiteral(str, dt, line, col) ;
            }

            case LITERAL_LANG :
                return pp.createLangLiteral(str, token.getImage2(), line, col) ;

            case STRING :
            case STRING1 :
            case STRING2 :
            case LONG_STRING1 :
            case LONG_STRING2 :
                return pp.createStringLiteral(str, line, col) ;
            default : {
                Node x = pp.createNodeFromToken(currentGraph, token, line, col) ;
                if (x != null)
                    return x ;
                pp.getHandler().fatal("Not a valid token for an RDF term: " + token, line, col) ;
                return null ;
            }
        }
    }

    public Node createEmbedded(ParserProfileStar pp, Node currentGraph, TokenStar token) {
    	Node subject = create(pp, currentGraph, token.getEmbeddedToken1());
    	Node predicate = create(pp, currentGraph, token.getEmbeddedToken2());
    	Node object = create(pp, currentGraph, token.getEmbeddedToken3());
    	
    	return NodeFactoryStar.createEmbeddedNode(subject,predicate,object);
	}

	private static String expandPrefixedName(ParserProfileStar pp, String prefix, String localPart, TokenStar token) {
        String expansion = pp.getPrologue().getPrefixMap().expand(prefix, localPart) ;
        if (expansion == null) {
            if ( ARQ.isTrue(ARQ.fixupUndefinedPrefixes) )
                return RiotLib.fixupPrefixIRI(prefix, localPart) ;
            pp.getHandler().fatal("Undefined prefix: " + prefix, token.getLine(), token.getColumn()) ;
        }
        return expansion ;
    }

    public boolean isStrictMode() {
        return strictMode ;
    }

    public void setStrictMode(boolean mode) {
        strictMode = mode ;
    }
}

package dk.aau.cs.qweb.main;

import static org.apache.jena.atlas.lib.Chars.CH_COMMA;
import static org.apache.jena.atlas.lib.Chars.CH_DOT;
import static org.apache.jena.atlas.lib.Chars.CH_LBRACE;
import static org.apache.jena.atlas.lib.Chars.CH_LBRACKET;
import static org.apache.jena.atlas.lib.Chars.CH_LPAREN;
import static org.apache.jena.atlas.lib.Chars.CH_RBRACE;
import static org.apache.jena.atlas.lib.Chars.CH_RBRACKET;
import static org.apache.jena.atlas.lib.Chars.CH_RPAREN;
import static org.apache.jena.atlas.lib.Chars.CH_SEMICOLON;
import static org.apache.jena.atlas.lib.Lib.hashCodeObject;
import static dk.aau.cs.qweb.main.TokenTypeStar.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.jena.atlas.io.PeekReader;
import org.apache.jena.atlas.iterator.Iter;
import org.apache.jena.atlas.lib.Pair;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.riot.RiotException;
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.riot.system.Prologue;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.graph.NodeConst;
import org.apache.jena.sparql.util.FmtUtils;
import org.apache.jena.sparql.util.NodeUtils;
import org.apache.jena.vocabulary.XSD;

public class TokenStar {
	// Some tokens are "multipart"
    //   A language tag is a sub-token string and token part.
    //     It uses subToken1, and image2.
    //   A datatype literal is two tokens
    //     It uses subToken1, subToken2 and sets image to the lexical part.
    //   A prefixed name is two strings. 
    //     It uses tokenImage and tokenImage2
    
//	private TokenTypeStar tokenEmbedded1Type = null ;
//	private TokenTypeStar tokenEmbedded2Type = null ;
//	private TokenTypeStar tokenEmbedded3Type = null ;
//	private String tokenEmbedded1Image = null ;
//    private String tokenEmbedded1Image2 = null ;         // Used for language tag and second part of prefix name
//    private String tokenEmbedded2Image = null ;
//    private String tokenEmbedded2Image2 = null ;         // Used for language tag and second part of prefix name
//    private String tokenEmbedded3Image = null ;
//    private String tokenEmbedded3Image2 = null ;         // Used for language tag and second part of prefix name
//	
//    public final TokenStar setEmbedded1Type(TokenTypeStar tokenType) { this.tokenEmbedded1Type = tokenType ; return this ; }
//    public final TokenStar setEmbedded1Image(String tokenImage)      { this.tokenEmbedded1Image = tokenImage ; return this ; }
//    public final TokenStar setEmbedded1Image2(String tokenImage2)    { this.tokenEmbedded1Image2 = tokenImage2 ; return this ; }
//	
//    public final TokenStar setEmbedded2Type(TokenTypeStar tokenType) { this.tokenEmbedded2Type = tokenType ; return this ; }
//    public final TokenStar setEmbedded2Image(String tokenImage)      { this.tokenEmbedded2Image = tokenImage ; return this ; }
//    public final TokenStar setEmbedded2Image2(String tokenImage2)    { this.tokenEmbedded2Image2 = tokenImage2 ; return this ; }
//   
//    public final TokenStar setEmbedded3Type(TokenTypeStar tokenType) { this.tokenEmbedded3Type = tokenType ; return this ; }
//    public final TokenStar setEmbedded3Image(String tokenImage)      { this.tokenEmbedded3Image = tokenImage ; return this ; }
//    public final TokenStar setEmbedded3Image2(String tokenImage2)    { this.tokenEmbedded3Image2 = tokenImage2 ; return this ; }
//    
//	public final TokenTypeStar getEmbedded1Type()   { return tokenEmbedded1Type ; }
//    public final String getEmbedded1Image()      	{ return tokenEmbedded1Image ; }
//    public final String getEmbedded1Image2()     	{ return tokenEmbedded1Image2 ; }
//    
//	public final TokenTypeStar getEmbedded2Type()   { return tokenEmbedded2Type ; }
//    public final String getEmbedded2Image()      	{ return tokenEmbedded2Image ; }
//    public final String getEmbedded2Image2()     	{ return tokenEmbedded2Image2 ; }
//    
//	public final TokenTypeStar getEmbedded3Type()   { return tokenEmbedded3Type ; }
//    public final String getEmbedded3Image()      	{ return tokenEmbedded3Image ; }
//    public final String getEmbedded3Image2()     	{ return tokenEmbedded3Image2 ; }
//    
    
	private TokenStar embeddedToken1 = null;
	private TokenStar embeddedToken2 = null;
	private TokenStar embeddedToken3 = null;
	
    private TokenTypeStar tokenType = null ;
    
    private String tokenImage = null ;
    private String tokenImage2 = null ;         // Used for language tag and second part of prefix name
    
    private TokenStar subToken1 = null ;            // A related token (used for datatype literals and language tags)
    private TokenStar subToken2 = null ;            // A related token (used for datatype literals and language tags)
    
    public int cntrlCode = 0 ;
    private long column ;
    private long line ;
    
    // Keywords recognized.
    public static final String ImageANY     = "ANY" ;
    public static final String ImageTrue    = "true" ;
    public static final String ImageFalse   = "false" ;
    
	public final TokenTypeStar getType()    { return tokenType ; }
    public final String getImage()      { return tokenImage ; }
    //public final String getImage1()  { return tokenImage1 ; }
    
    public final String getImage2()     { return tokenImage2 ; }
    public final int getCntrlCode()     { return cntrlCode ; }
    public final TokenStar getSubToken1()   { return subToken1 ; }
    public final TokenStar getSubToken2()   { return subToken2 ; }
    
    public final TokenStar setType(TokenTypeStar tokenType)     { this.tokenType = tokenType ; return this ; }
    public final TokenStar setImage(String tokenImage)      { this.tokenImage = tokenImage ; return this ; }
    public final TokenStar setImage(char tokenImage)        { this.tokenImage = String.valueOf(tokenImage) ; return this ; }
    
    public final TokenStar setImage2(String tokenImage2)    { this.tokenImage2 = tokenImage2 ; return this ; }
    
    public final TokenStar setCntrlCode(int cntrlCode)      { this.cntrlCode = cntrlCode ; return this ; }

    public final TokenStar setSubToken1(TokenStar subToken12)     { this.subToken1 = subToken12 ; return this ; }
    public final TokenStar setSubToken2(TokenStar subToken)     { this.subToken2 = subToken ; return this ; }
    
    static TokenStar create(String s)
    {
        PeekReader pr = PeekReader.readString(s) ;
        TokenizerStar tt = new TokenizerStar(pr) ;
        if ( ! tt.hasNext() )
            throw new RiotException("No token") ;
        TokenStar t = tt.next() ;
        if ( tt.hasNext() )
            throw new RiotException("Extraneous charcaters") ;
        return t ;
    }
    
    static TokenStar createEmbedded() {
    	return new TokenStar();
    }

    static Iter<TokenStar> createN(String s)
    {
        PeekReader pr = PeekReader.readString(s) ;
        TokenizerStar tt = new TokenizerStar(pr) ;
        List<TokenStar> x = new ArrayList<>() ;
        while(tt.hasNext())
            x.add(tt.next()) ;
        return Iter.iter(x) ;
    }
    
    public long getColumn()
    {
        return column ;
    }

    public long getLine()
    {
        return line ;
    }

    TokenStar(String string) { this(STRING, string) ; } 

    TokenStar(TokenTypeStar type) { this(type, null, null) ; }

    TokenStar(TokenTypeStar type, String image1) { this(type, image1, null) ; }

    TokenStar(TokenTypeStar type, String image1, String image2) { 
        this() ;
        setType(type) ;
        setImage(image1) ;
        setImage2(image2) ;
    }
    
//    private Token(TokenTypeStar type) { this(type, null, null, null) ; }
//    
//    private Token(TokenTypeStar type, String image1) { this(type, image1, null, null) ; }
//    
//    private Token(TokenTypeStar type, String image1, String image2)
//    { this(type, image1, image2, null) ; }
//
//    private Token(TokenTypeStar type, String image1, Token subToken)
//    { this(type, image1, null, subToken) ; }
//
//
    private TokenStar(TokenTypeStar type, String image1, String image2, TokenStar subToken1, TokenStar subToken2)
    {
        this() ;
        setType(type) ;
        setImage(image1) ;
        setImage2(image2) ;
        setSubToken1(subToken1) ;
        setSubToken2(subToken2) ;
    }
    
    private TokenStar() { this(-1, -1) ; }
    
    public TokenStar(long line, long column) { this.line = line ; this.column = column ; }
    
    public TokenStar(TokenStar token)
    { 
        this(token.tokenType, 
             token.tokenImage, token.tokenImage2,
             token.subToken1, token.subToken2) ;
        this.cntrlCode      = token.cntrlCode ;
        this.line           = token.line ; 
        this.column         = token.column ;
        this.embeddedToken1 = token.embeddedToken1;
        this.embeddedToken2 = token.embeddedToken2;
        this.embeddedToken3 = token.embeddedToken3;
    }
    
    // Convenience operations for accessing tokens. 
    
    public String asString() {
        switch (tokenType)
        {
            case STRING: 
            case STRING1: case STRING2: 
            case LONG_STRING1: case LONG_STRING2:
                return getImage() ;
            default:
                return null ;
        }
    }
    
    public int asInt() {
        if ( ! hasType(TokenTypeStar.INTEGER) ) return -1 ;
        return Integer.valueOf(tokenImage);
    }
    
    public long asLong()
    {
        return asLong(-1) ;
    }
    
    public long asLong(long dft)
    {
        switch (tokenType)
        {
            case INTEGER:   return Long.valueOf(tokenImage) ;
            case HEX:       return Long.valueOf(tokenImage, 16) ;
            default:
                 return dft ;
        }
    }
    
    public String asWord()
    {
        if ( ! hasType(TokenTypeStar.KEYWORD) ) return null ;
        return tokenImage ; 
    }
    
    public String text()
    {
        return toString(false) ;
        
    }
    
    @Override
    public String toString()
    {
        return toString(false) ;
    }
     
    static final String delim1 = "" ;
    static final String delim2 = "" ;
    public String toString(boolean addLocation)
    {
        StringBuilder sb = new StringBuilder() ;
        if ( addLocation && getLine() >= 0 && getColumn() >= 0 )
            sb.append(String.format("[%d,%d]", getLine(), getColumn())) ;
        sb.append("[") ;
        if ( getType() == null )
            sb.append("null") ;
        else
            sb.append(getType().toString()) ;
        
        if ( getImage() != null )
        {
            sb.append(":") ;
            sb.append(delim1) ;
            sb.append(getImage()) ;
            sb.append(delim1) ;
        }
            
        if ( getImage2() != null )
        {
            sb.append(":") ;
            sb.append(delim2) ;
            sb.append(getImage2()) ;
            sb.append(delim2) ;
        }
        
        if ( getSubToken1() != null )
        {
            sb.append(";") ;
            sb.append(delim2) ;
            sb.append(getSubToken1().toString()) ;
            sb.append(delim2) ;
        }   

        if ( getSubToken2() != null )
        {
            sb.append(";") ;
            sb.append(delim2) ;
            sb.append(getSubToken2().toString()) ;
            sb.append(delim2) ;
        }   

        if ( getCntrlCode() != 0 )
        {
            sb.append(":") ; 
            sb.append(getCntrlCode()) ;
        }
        sb.append("]") ;
        return sb.toString() ;
    }
    
    public boolean isEOF() { return tokenType == TokenTypeStar.EOF ; }
    
    public boolean isCtlCode() { return tokenType == TokenTypeStar.CNTRL ; }

    public boolean isWord() { return tokenType == TokenTypeStar.KEYWORD ; }

    public boolean isString()
    {
        switch(tokenType)
        {
            case STRING:
            case STRING1:
            case STRING2:
            case LONG_STRING1:
            case LONG_STRING2:
                return true ;
            default:
                return false ;
        }
    }

    public boolean isNumber()
    {
        switch(tokenType)
        {
            case DECIMAL: 
            case DOUBLE:
            case INTEGER:
                return true ;
            default:
                return false ;
        }
    }
    
    public boolean isNode()
    {
        switch(tokenType)
        {
            case BNODE :
            case IRI : 
            case PREFIXED_NAME :
            case DECIMAL: 
            case DOUBLE:
            case INTEGER:
            case LITERAL_DT:
            case LITERAL_LANG:
            case STRING:
            case STRING1:
            case STRING2:
            case LONG_STRING1:
            case LONG_STRING2:
                return true ;
            case KEYWORD:
                if ( tokenImage.equals(ImageANY) )
                    return true ;
                return false ;
            default:
                return false ;
        }
    }
    
    // N-Triples but allows single quoted strings as well.
    public boolean isNodeBasic()
    {
        switch(tokenType)
        {
            case BNODE :
            case IRI : 
            case PREFIXED_NAME :
            case LITERAL_DT:
            case LITERAL_LANG:
            case STRING1:
            case STRING2:
                return true ;
            default:
                return false ;
        }
    }
    
    public boolean isBasicLiteral()
    {
        switch(tokenType)
        {
            case LITERAL_DT:
            case LITERAL_LANG:
            case STRING:
            case STRING1:
            case STRING2:
            case LONG_STRING1:
            case LONG_STRING2:
                return true ;
            default:
                return false ;
        }
    }
    
    public boolean isInteger()
    {
        return tokenType.equals(TokenTypeStar.INTEGER) ;
    }
    
    public boolean isIRI()
    {
        return tokenType.equals(TokenTypeStar.IRI) || tokenType.equals(TokenTypeStar.PREFIXED_NAME);
    }

    public boolean isBNode()
    {
        return tokenType.equals(TokenTypeStar.BNODE) ;
    }

    
    /** Token to Node, a very direct form that is purely driven off the token.
     *  Turtle and N-triples need to process the token and not call this:
     *  1/ Use bNode label as given
     *  2/ No prefix or URI resolution.
     *  3/ No checking.
     */
    public Node asNode()
    {
        return asNode(null) ;
    }
    
    /** Token to Node, with a prefix map
     *  Turtle and N-triples need to process the token and not call this:
     *  1/ Use bNode label as given
     *  2/ No prefix or URI resolution.
     *  3/ No checking.
     */
    public Node asNode(PrefixMap pmap)
    {
        switch(tokenType)
        {
            // Assumes that bnode labels have been sorted out already.
            case BNODE : return NodeFactory.createBlankNode(tokenImage) ;
            case IRI :
                // RiotLib.createIRIorBNode(tokenImage) includes processing <_:label>
                return NodeFactory.createURI(tokenImage) ; 
            case PREFIXED_NAME :
                if ( pmap == null )
                    return NodeFactory.createURI("urn:prefixed-name:"+tokenImage+":"+tokenImage2) ;
                String x = pmap.expand(tokenImage, tokenImage2) ;
                if ( x == null )
                    throw new RiotException("Can't expand prefixed name: "+this) ;
                return NodeFactory.createURI(x) ;
            case DECIMAL :  return NodeFactory.createLiteral(tokenImage, XSDDatatype.XSDdecimal)  ; 
            case DOUBLE :   return NodeFactory.createLiteral(tokenImage, XSDDatatype.XSDdouble)  ;
            case INTEGER:   return NodeFactory.createLiteral(tokenImage, XSDDatatype.XSDinteger) ;
            case LITERAL_DT :
            {
            	TokenStar lexToken = getSubToken1() ;
            	TokenStar dtToken  = getSubToken2() ;
                
                if ( pmap == null && dtToken.hasType(TokenTypeStar.PREFIXED_NAME) )
                    // Must be able to resolve the datattype else we can't find it's datatype.
                    throw new RiotException("Invalid token: "+this) ;
                Node n = dtToken.asNode(pmap);
                if ( ! n.isURI() )
                    throw new RiotException("Invalid token: "+this) ;
                RDFDatatype dt = TypeMapper.getInstance().getSafeTypeByName(n.getURI()) ;
                return NodeFactory.createLiteral(lexToken.getImage(), dt)  ;
            }
            case LITERAL_LANG : return NodeFactory.createLiteral(tokenImage, tokenImage2)  ;
            case STRING:
            case STRING1:
            case STRING2:
            case LONG_STRING1:
            case LONG_STRING2:
                return NodeFactory.createLiteral(tokenImage) ;
            case VAR:
                return Var.alloc(tokenImage) ;
            case KEYWORD:
                if ( tokenImage.equals(ImageANY) )
                    return NodeConst.nodeANY ;
                if ( tokenImage.equals(ImageTrue) )
                    return NodeConst.nodeTrue ;
                if ( tokenImage.equals(ImageFalse) )
                    return NodeConst.nodeFalse ;
                //$FALL-THROUGH$
            default: break ;
        }
        return null ;
    }

    
    public boolean hasType(TokenTypeStar tokenType)
    {
        return getType() == tokenType ;
    }
    
    @Override
    public int hashCode()
    {
        return hashCodeObject(tokenType) ^
                hashCodeObject(tokenImage) ^
                hashCodeObject(tokenImage2) ^
                hashCodeObject(cntrlCode) ;
    }
    
    @Override
    public boolean equals(Object other)
    {
        if ( ! ( other instanceof TokenStar ) ) return false ;
        TokenStar t = (TokenStar)other ;
        return  Objects.equals(tokenType, t.tokenType) &&
        		Objects.equals(tokenImage, t.tokenImage) &&
        		Objects.equals(tokenImage2, t.tokenImage2) &&
        		Objects.equals(cntrlCode, t.cntrlCode) ;
    }
    
    public static TokenStar tokenForChar(char character)
    {
        switch(character)
        { 
            case CH_DOT:        return new TokenStar(TokenTypeStar.DOT) ;
            case CH_SEMICOLON:  return new TokenStar(TokenTypeStar.SEMICOLON) ;
            case CH_COMMA:      return new TokenStar(TokenTypeStar.COMMA) ;
            case CH_LBRACE:     return new TokenStar(TokenTypeStar.LBRACE) ;
            case CH_RBRACE:     return new TokenStar(TokenTypeStar.RBRACE) ;
            case CH_LPAREN:     return new TokenStar(TokenTypeStar.LPAREN) ;
            case CH_RPAREN:     return new TokenStar(TokenTypeStar.RPAREN) ;
            case CH_LBRACKET:   return new TokenStar(TokenTypeStar.LBRACKET) ;
            case CH_RBRACKET:   return new TokenStar(TokenTypeStar.RBRACKET) ;
            default:
                throw new RuntimeException("Token error: unrecognized charcater: "+character) ;
        }
    }
    
    public static TokenStar tokenForInteger(long value)
    {
        return new TokenStar(TokenTypeStar.INTEGER, Long.toString(value)) ;
    }
    
    public static TokenStar tokenForWord(String word)
    {
        return new TokenStar(TokenTypeStar.KEYWORD, word) ; 
    }

    public static TokenStar tokenForNode(Node n)
    {
        return tokenForNode(n, null, null) ;
    }

    public static TokenStar tokenForNode(Node n, Prologue prologue)
    {
        return tokenForNode(n, prologue.getBaseURI(), prologue.getPrefixMap()) ;
    }

    public static TokenStar tokenForNode(Node node, String base, PrefixMap mapping) {
        if ( node.isURI() ) {
            String uri = node.getURI() ;
            if ( mapping != null ) {
                Pair<String, String> pname = mapping.abbrev(uri) ;
                if ( pname != null )
                    return new TokenStar(TokenTypeStar.PREFIXED_NAME, pname.getLeft(), pname.getRight()) ;
            }
            if ( base != null ) {
                String x = FmtUtils.abbrevByBase(uri, base) ;
                if ( x != null )
                    return new TokenStar(TokenTypeStar.IRI, x) ;
            }
            return new TokenStar(IRI, node.getURI()) ;
        }

        if ( node.isBlank() )
            return new TokenStar(BNODE, node.getBlankNodeLabel()) ;

        if ( node.isVariable() )
            return new TokenStar(VAR, node.getName()) ;

        if ( node.isLiteral() ) {
            if ( NodeUtils.isSimpleString(node) ) {
                String lex = node.getLiteralLexicalForm() ;
                return new TokenStar(STRING, lex) ;
            }

            if ( NodeUtils.isLangString(node) ) {
                String lex = node.getLiteralLexicalForm() ;
                TokenStar sub1 = new TokenStar(STRING, lex) ;
                String lang = node.getLiteralLanguage() ;
                return new TokenStar(LITERAL_LANG, lex, lang, sub1, null) ;
            }

            // Has a datatype (RDF 1.0 and RDF 1.1)
            String datatype = node.getLiteralDatatypeURI() ;
            String s = node.getLiteralLexicalForm() ;

            // Special form we know how to handle?
            // Assume valid text
            if ( datatype.equals(XSD.integer.getURI()) ) {
                try {
                    String s1 = s ;
                    // BigInteger does not allow leading +
                    // so chop it off before the format test
                    // BigDecimal does allow a leading +
                    if ( s.startsWith("+") )
                        s1 = s.substring(1) ;
                    new java.math.BigInteger(s1) ;
                    return new TokenStar(INTEGER, s) ;
                }
                catch (NumberFormatException nfe) {}
                // No luck. Continue.
                // Continuing is always safe.
            }

            if ( datatype.equals(XSD.decimal.getURI()) ) {
                if ( s.indexOf('.') > 0 ) {
                    try {
                        // BigDecimal does allow a leading +
                        new java.math.BigDecimal(s) ;
                        return new TokenStar(DECIMAL, s) ;
                    }
                    catch (NumberFormatException nfe) {}
                    // No luck. Continue.
                }
            }

            if ( datatype.equals(XSD.xdouble.getURI()) ) {
                // Assumes SPARQL has decimals and doubles.
                // Must have 'e' or 'E' to be a double short form.

                if ( s.indexOf('e') >= 0 || s.indexOf('E') >= 0 ) {
                    try {
                        Double.parseDouble(s) ;
                        return new TokenStar(DOUBLE, s) ;
                    }
                    catch (NumberFormatException nfe) {}
                    // No luck. Continue.
                }
            }

            // if ( datatype.equals(XSD.xboolean.getURI()) ) {
            //     if ( s.equalsIgnoreCase("true") ) return new Token(BOOLEAN, s) ;
            //     if ( s.equalsIgnoreCase("false") ) return new Token(BOOLEAN, s) ;
            // }

            Node dt = NodeFactory.createURI(datatype) ;
            TokenStar subToken1 = new TokenStar(STRING, s) ;
            TokenStar subToken2 = tokenForNode(dt) ;
            TokenStar t = new TokenStar(LITERAL_DT, s) ;
            t.setSubToken1(subToken1) ;
            t.setSubToken2(subToken2) ;
            return t ;
        }

        if ( node.equals(Node.ANY) )
            return new TokenStar(TokenTypeStar.KEYWORD, ImageANY) ;

        throw new IllegalArgumentException() ;
    }
	public TokenStar getEmbeddedToken1() {
		return embeddedToken1;
	}
	public void setEmbeddedToken1(TokenStar embeddedToken1) {
		this.embeddedToken1 = embeddedToken1;
	}
	public TokenStar getEmbeddedToken2() {
		return embeddedToken2;
	}
	public void setEmbeddedToken2(TokenStar embeddedToken2) {
		this.embeddedToken2 = embeddedToken2;
	}
	public TokenStar getEmbeddedToken3() {
		return embeddedToken3;
	}
	public void setEmbeddedToken3(TokenStar embeddedToken3) {
		this.embeddedToken3 = embeddedToken3;
	}
}

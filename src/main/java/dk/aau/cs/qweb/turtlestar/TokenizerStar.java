package dk.aau.cs.qweb.turtlestar;

import static org.apache.jena.atlas.lib.Chars.BSPACE;
import static org.apache.jena.atlas.lib.Chars.CH_AT;
import static org.apache.jena.atlas.lib.Chars.CH_COLON;
import static org.apache.jena.atlas.lib.Chars.CH_COMMA;
import static org.apache.jena.atlas.lib.Chars.CH_DOT;
import static org.apache.jena.atlas.lib.Chars.CH_EQUALS;
import static org.apache.jena.atlas.lib.Chars.CH_GT;
import static org.apache.jena.atlas.lib.Chars.CH_HASH;
import static org.apache.jena.atlas.lib.Chars.CH_LBRACE;
import static org.apache.jena.atlas.lib.Chars.CH_LBRACKET;
import static org.apache.jena.atlas.lib.Chars.CH_LPAREN;
import static org.apache.jena.atlas.lib.Chars.CH_LT;
import static org.apache.jena.atlas.lib.Chars.CH_MINUS;
import static org.apache.jena.atlas.lib.Chars.CH_PERCENT;
import static org.apache.jena.atlas.lib.Chars.CH_PLUS;
import static org.apache.jena.atlas.lib.Chars.CH_QMARK;
import static org.apache.jena.atlas.lib.Chars.CH_QUOTE1;
import static org.apache.jena.atlas.lib.Chars.CH_QUOTE2;
import static org.apache.jena.atlas.lib.Chars.CH_RBRACE;
import static org.apache.jena.atlas.lib.Chars.CH_RBRACKET;
import static org.apache.jena.atlas.lib.Chars.CH_RPAREN;
import static org.apache.jena.atlas.lib.Chars.CH_RSLASH;
import static org.apache.jena.atlas.lib.Chars.CH_SEMICOLON;
import static org.apache.jena.atlas.lib.Chars.CH_STAR;
import static org.apache.jena.atlas.lib.Chars.CH_UNDERSCORE;
import static org.apache.jena.atlas.lib.Chars.CR;
import static org.apache.jena.atlas.lib.Chars.EOF;
import static org.apache.jena.atlas.lib.Chars.NL;
import static org.apache.jena.atlas.lib.Chars.SPC;
import static org.apache.jena.atlas.lib.Chars.TAB;
import static org.apache.jena.riot.system.RiotChars.isA2Z;
import static org.apache.jena.riot.system.RiotChars.isA2ZN;
import static org.apache.jena.riot.system.RiotChars.isAlphaNumeric;
import static org.apache.jena.riot.system.RiotChars.isHexChar;
import static org.apache.jena.riot.system.RiotChars.isHorizontalWhitespace;
import static org.apache.jena.riot.system.RiotChars.isNewlineChar;
import static org.apache.jena.riot.system.RiotChars.isPNChars;
import static org.apache.jena.riot.system.RiotChars.isWhitespace;
import static org.apache.jena.riot.system.RiotChars.range;
import static org.apache.jena.riot.system.RiotChars.valHexChar;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.jena.atlas.AtlasException;
import org.apache.jena.atlas.io.IO;
import org.apache.jena.atlas.io.PeekReader;
import org.apache.jena.atlas.lib.Chars;
import org.apache.jena.atlas.lib.Closeable;
import org.apache.jena.riot.RiotParseException;
import org.apache.jena.riot.system.RiotChars;
import org.apache.jena.sparql.ARQInternalErrorException;

public class TokenizerStar implements Iterator<TokenStar>, Closeable {
	// TODO Remove CNTL and make SYMBOLS
    // Drop through to final general symbol/keyword reader, including <=, != 
    // Care with <=
    // STRING, not STRING1/2, LONG_STRING1/2
    // Policy driven for CURIES?
    
    // Various allow/deny options (via checker?)
    
    // RDF mode:
    //   Prefixes - yes and no.
    //   IRIs
    //   BNodes
    
    // Space for CURIEs, stricter Turtle QNames, sane Turtle (i.e. leading digits in local part).
    public static final int CTRL_CHAR = CH_STAR ;
    
    public static boolean Checking = false ;

    private TokenStar token = null ; 
    private final StringBuilder stringBuilder = new StringBuilder(200) ;
    private final PeekReader reader ;
    private final boolean lineMode ;        // Whether whitespace includes or excludes NL (in its various forms).  
    private boolean finished = false ;
    private TokenCheckerStar checker = null ;

    /*package*/ TokenizerStar(PeekReader reader) {
        this(reader, false) ;
    }
    
    /* package */TokenizerStar(PeekReader reader, boolean lineMode) {
        this.reader = reader ;
        this.lineMode = lineMode ;
    }
    
    public final boolean hasNext() {
        if ( finished )
            return false ;
        if ( token != null )
            return true ;

        try {
            skip() ;
            if ( reader.eof() ) {
                // close() ;
                finished = true ;
                return false ;
            }
            token = parseToken() ;
            if ( token == null ) {
                // close() ;
                finished = true ;
                return false ;
            }
            return true ;
        } catch (AtlasException ex) {
            if ( ex.getCause() != null ) {
                if ( ex.getCause().getClass() == java.nio.charset.MalformedInputException.class )
                    throw new RiotParseException("Bad character encoding", reader.getLineNum(), reader.getColNum()) ;
                throw new RiotParseException("Bad input stream [" + ex.getCause() + "]", reader.getLineNum(),
                                             reader.getColNum()) ;
            }
            throw new RiotParseException("Bad input stream", reader.getLineNum(), reader.getColNum()) ;
        }
    }
    
    
    public final boolean eof() {
        return hasNext() ;
    }

    public final TokenStar next() {
        if ( !hasNext() )
            throw new NoSuchElementException() ;
        TokenStar t = token ;
        token = null ;
        return t ;
    }

    public final TokenStar peek() {
        if ( !hasNext() )
            return null ;
        return token ;
    }
    
    public void remove()                            { throw new UnsupportedOperationException() ; }

    public TokenCheckerStar getChecker()                { return checker ; }
    public void setChecker(TokenCheckerStar checker)    { this.checker = checker ; }

    public void close() {
        IO.close(reader) ;
    }

    // ---- Machinary
    
    private void skip() {
        int ch = EOF ;
        for (;;) {
            if ( reader.eof() )
                return ;

            ch = reader.peekChar() ;
            if ( ch == CH_HASH ) {
                reader.readChar() ;
                // Comment. Skip to NL
                for (;;) {
                    ch = reader.peekChar() ;
                    if ( ch == EOF || isNewlineChar(ch) )
                        break ;
                    reader.readChar() ;
                }
            }

            // Including excess newline chars from comment.
            if ( lineMode ) {
                if ( !isHorizontalWhitespace(ch) )
                    break ;
            } else {
                if ( !isWhitespace(ch) )
                    break ;
            }
            reader.readChar() ;
        }
    }

    private TokenStar parseToken() {
        token = new TokenStar(getLine(), getColumn()) ;
        
        int ch = reader.peekChar() ;

        // ---- IRI or embedded triple
        if ( ch == CH_LT ) {
            reader.readChar() ;
            int ch2 = reader.peekChar() ;
            if ( ch2 == ch ) { //Embedded
            	token.setImage(readEmbeddedTriple()) ;
                token.setType(TokenTypeStar.EMBEDDED) ;
                if ( Checking )
                    checkURI(token.getImage()) ;
                return token ;
            } else { //IRI
            	token.setImage(readIRI()) ;
                token.setType(TokenTypeStar.IRI) ;
                if ( Checking )
                    checkURI(token.getImage()) ;
                return token ;
            }
            
        }

        // ---- Literal
        if ( ch == CH_QUOTE1 || ch == CH_QUOTE2 ) {
            reader.readChar() ;
            int ch2 = reader.peekChar() ;
            if ( ch2 == ch ) {
                reader.readChar() ; // Read potential second quote.
                int ch3 = reader.peekChar() ;
                if ( ch3 == ch ) {
                    reader.readChar() ;
                    token.setImage(readLongString(ch, false)) ;
                    TokenTypeStar tt = (ch == CH_QUOTE1) ? TokenTypeStar.LONG_STRING1 : TokenTypeStar.LONG_STRING2 ;
                    token.setType(tt) ;
                } else {
                    // Two quotes then a non-quote.
                    // Must be '' or ""
                    // No need to pushback characters as we know the lexical
                    // form is the empty string.
                    // if ( ch2 != EOF ) reader.pushbackChar(ch2) ;
                    // if ( ch1 != EOF ) reader.pushbackChar(ch1) ; // Must be
                    // '' or ""
                    token.setImage("") ;
                    token.setType((ch == CH_QUOTE1) ? TokenTypeStar.STRING1 : TokenTypeStar.STRING2) ;
                }
            } else {
                // Single quote character.
                token.setImage(readString(ch, ch)) ;
                // Single quoted string.
                token.setType((ch == CH_QUOTE1) ? TokenTypeStar.STRING1 : TokenTypeStar.STRING2) ;
            }

            // Whte space after lexical part of a literal.
            skip() ;
            
            // Literal. Is it @ or ^^
            if ( reader.peekChar() == CH_AT ) {
                reader.readChar() ;
                // White space is not legal here.
                // The Turtle spec terminal is "LANGTAG" which includes the '@'.
                TokenStar mainToken = new TokenStar(token) ;
                mainToken.setType(TokenTypeStar.LITERAL_LANG) ;
                mainToken.setSubToken1(token) ;
                mainToken.setImage2(langTag()) ;
                token = mainToken ;
                if ( Checking )
                    checkLiteralLang(token.getImage(), token.getImage2()) ;
            } else if ( reader.peekChar() == '^' ) {
                expect("^^") ;
                // White space is legal after a ^^. 
                // It's not a good idea, but it is legal.
//                // Check no whitespace.
//                int nextCh = reader.peekChar() ;
//                if ( isWhitespace(nextCh) )
//                    exception("No whitespace after ^^ in literal with datatype") ;
                skip() ;
                
                // Stash current token.
                TokenStar mainToken = new TokenStar(token) ;
                mainToken.setSubToken1(token) ;
                mainToken.setImage(token.getImage()) ;

                TokenStar subToken = parseToken() ;
                if ( !subToken.isIRI() )
                    exception("Datatype URI required after ^^ - URI or prefixed name expected") ;

                mainToken.setSubToken2(subToken) ;
                mainToken.setType(TokenTypeStar.LITERAL_DT) ;

                token = mainToken ;
                if ( Checking )
                    checkLiteralDT(token.getImage(), subToken) ;
            } else {
                // Was a simple string.
                if ( Checking )
                    checkString(token.getImage()) ;
            }
            return token ;
        }

        if ( ch == CH_UNDERSCORE ) {
            // Blank node :label must be at least one char
            expect("_:") ;
            token.setImage(readBlankNodeLabel()) ;
            token.setType(TokenTypeStar.BNODE) ;
            if ( Checking ) checkBlankNode(token.getImage()) ;
            return token ;
        }

        // TODO remove and make a symbol/keyword.
        // Control
        if ( ch == CTRL_CHAR ) {
            reader.readChar() ;
            token.setType(TokenTypeStar.CNTRL) ;
            ch = reader.readChar() ;
            if ( ch == EOF )
                exception("EOF found after " + CTRL_CHAR) ;
            if ( RiotChars.isWhitespace(ch) )
                token.cntrlCode = -1 ;
            else
                token.cntrlCode = (char)ch ;
            if ( Checking )
                checkControl(token.cntrlCode) ;
            return token ;
        }

        // A directive (not part of a literal as lang tag)
        if ( ch == CH_AT ) {
            reader.readChar() ;
            token.setType(TokenTypeStar.DIRECTIVE) ;
            token.setImage(readWord(false)) ;
            if ( Checking )
                checkDirective(token.cntrlCode) ;
            return token ;
        }

        // Variable
        if ( ch == CH_QMARK ) {
            reader.readChar() ;
            token.setType(TokenTypeStar.VAR) ;
            // Character set?
            token.setImage(readVarName()) ;
            if ( Checking )
                checkVariable(token.getImage()) ;
            return token ;
        }
        
        // Symbol?
        switch(ch)
        { 
            // DOT can start a decimal.  Check for digit.
            case CH_DOT:
                reader.readChar() ;
                ch = reader.peekChar() ;
                if ( range(ch, '0', '9') ) {
                    // Not a DOT after all.
                    reader.pushbackChar(CH_DOT) ;
                    readNumber() ;
                    if ( Checking )
                        checkNumber(token.getImage(), token.getImage2()) ;
                    return token ;
                }
                token.setType(TokenTypeStar.DOT) ;
                return token ;
            
            case CH_SEMICOLON:  reader.readChar() ; token.setType(TokenTypeStar.SEMICOLON) ; /*token.setImage(CH_SEMICOLON) ;*/ return token ;
            case CH_COMMA:      reader.readChar() ; token.setType(TokenTypeStar.COMMA) ;     /*token.setImage(CH_COMMA) ;*/ return token ;
            case CH_LBRACE:     reader.readChar() ; token.setType(TokenTypeStar.LBRACE) ;    /*token.setImage(CH_LBRACE) ;*/ return token ;
            case CH_RBRACE:     reader.readChar() ; token.setType(TokenTypeStar.RBRACE) ;    /*token.setImage(CH_RBRACE) ;*/ return token ;
            case CH_LPAREN:     reader.readChar() ; token.setType(TokenTypeStar.LPAREN) ;    /*token.setImage(CH_LPAREN) ;*/ return token ;
            case CH_RPAREN:     reader.readChar() ; token.setType(TokenTypeStar.RPAREN) ;    /*token.setImage(CH_RPAREN) ;*/ return token ;
            case CH_LBRACKET:   reader.readChar() ; token.setType(TokenTypeStar.LBRACKET) ;  /*token.setImage(CH_LBRACKET) ;*/ return token ;
            case CH_RBRACKET:   reader.readChar() ; token.setType(TokenTypeStar.RBRACKET) ;  /*token.setImage(CH_RBRACKET) ;*/ return token ;
            case CH_EQUALS:     reader.readChar() ; token.setType(TokenTypeStar.EQUALS) ;    /*token.setImage(CH_EQUALS) ;*/ return token ;

            // Specials (if blank node processing off)
            //case CH_COLON:      reader.readChar() ; token.setType(TokenTypeStar.COLON) ; return token ;
            case CH_UNDERSCORE: reader.readChar() ; token.setType(TokenTypeStar.UNDERSCORE) ; /*token.setImage(CH_UNDERSCORE) ;*/ return token ;
            case CH_LT:         reader.readChar() ; token.setType(TokenTypeStar.LT) ; /*token.setImage(CH_LT) ;*/ return token ;
            case CH_GT:         reader.readChar() ; token.setType(TokenTypeStar.GT) ; /*token.setImage(CH_GT) ;*/ return token ;
            case CH_STAR:       reader.readChar() ; token.setType(TokenTypeStar.STAR) ; /*token.setImage(CH_STAR) ;*/ return token ;
            
            // Multi character symbols
            // Two character tokens && || GE >= , LE <=
            // Single character symbols for * /
            // +/- may start numbers.

//            case CH_PLUS:
//            case CH_MINUS:
//            case CH_STAR:
//            case CH_SLASH:
//            case CH_RSLASH:
                
        }
        
        // ---- Numbers.
        // But a plain "+" and "-" are symbols.
        
        /*
        [16]    integer         ::=     ('-' | '+') ? [0-9]+
        [17]    double          ::=     ('-' | '+') ? ( [0-9]+ '.' [0-9]* exponent | '.' ([0-9])+ exponent | ([0-9])+ exponent )
                                        0.e0, .0e0, 0e0
        [18]    decimal         ::=     ('-' | '+')? ( [0-9]+ '.' [0-9]* | '.' ([0-9])+ | ([0-9])+ )
                                        0.0 .0 0.
        [19]    exponent        ::=     [eE] ('-' | '+')? [0-9]+
        []      hex             ::=     0x0123456789ABCDEFG
        
        */
        
        // TODO readNumberNoSign
        
        //int signCh = 0 ;

        if ( ch == CH_PLUS || ch == CH_MINUS ) {
            reader.readChar() ;
            int ch2 = reader.peekChar() ;

            if ( !range(ch2, '0', '9') ) {
                // ch was end of symbol.
                // reader.readChar() ;
                if ( ch == CH_PLUS )
                    token.setType(TokenTypeStar.PLUS) ;
                else
                    token.setType(TokenTypeStar.MINUS) ;
                return token ;
            }

            // Already got a + or - ...
            // readNumberNoSign
            // Because next, old code proceses signs.
            reader.pushbackChar(ch) ;
            //signCh = ch ;
            // Drop to next "if"
        }

        if ( ch == CH_PLUS || ch == CH_MINUS || range(ch, '0', '9') ) {
            // readNumberNoSign
            readNumber() ;
            if ( Checking )
                checkNumber(token.getImage(), token.getImage2()) ;
            return token ;
        }

        if ( isNewlineChar(ch) ) {
            //** - If collecting token image.
            //** stringBuilder.setLength(0) ;
            // Any number of NL and CR become one "NL" token.
            do {
                //int ch2 = reader.readChar() ;
                //** stringBuilder.append((char)ch2) ;
            } while (isNewlineChar(reader.peekChar())) ;
            token.setType(TokenTypeStar.NL) ;
            //** token.setImage(stringBuilder.toString()) ;
            return token ;
        }
        
        // Plain words and prefixes.
        //   Can't start with a number due to numeric test above.
        //   Can't start with a '_' due to blank node test above.
        // If we see a :, the first time it means a prefixed name else it's a token break.
        
        readPrefixedNameOrKeyword(token) ;
        
        if ( Checking ) checkKeyword(token.getImage()) ;
        return token ;
    }

    
    private String readEmbeddedTriple() {
    	String image = "<<";
    	int ch = reader.readChar() ; //Consumers "<"
    	ch = reader.peekChar() ;
    	
        long posn = reader.getPosition() ;
        
        TokenStar embedded1 = null;
        if ( ch == CH_LT ) { 
        	
        	reader.readChar() ;
        	embedded1 = TokenStar.createEmbedded();
        	String iri = "<"+readIRI()+"> ";
        	image += iri;
        	embedded1.setImage(iri) ;
        	embedded1.setType(TokenTypeStar.IRI) ;
        	
        	
        	
        } else if ( ch == CH_UNDERSCORE ) {
        	
        	expect("_:") ;
        	
        	String iri = "_:"+readBlankNodeLabel();
        	image += iri+" ";
        	embedded1 = TokenStar.createEmbedded();
        	embedded1.setImage(iri+" ") ;
        	embedded1.setType(TokenTypeStar.BNODE) ;
        } else {
        	 String prefixPart = readPrefixPart() ; // Prefix part or keyword
             embedded1 = TokenStar.createEmbedded();
             embedded1.setImage(prefixPart) ;
             image += prefixPart;
             embedded1.setType(TokenTypeStar.KEYWORD) ;
             ch = reader.peekChar() ;
             if ( ch == CH_COLON ) {
            	 
                 reader.readChar() ;
                 embedded1.setType(TokenTypeStar.PREFIXED_NAME) ;
                 String ln = readLocalPart() ; // Local part
                 embedded1.setImage2(ln) ;
                 image += ":"+ln+" ";
             }
        }    

        // If we made no progress, nothing found, not even a keyword -- it's an
        // error.
        if ( posn == reader.getPosition() )
            {exception("Failed to find a prefix name or keyword: %c(%d;0x%04X)", ch, ch, ch) ;}
        
        token.setEmbeddedToken1(embedded1);
        
        skip() ;
        ch = reader.peekChar() ;
        
        TokenStar embedded2 = null;
		if ( ch == CH_LT ) {
        	reader.readChar() ;
        	embedded2 = TokenStar.createEmbedded();
        	String iri = "<"+readIRI()+"> ";
        	image += iri;
        	embedded2.setImage(iri) ;
        	embedded2.setType(TokenTypeStar.IRI) ;
        	
        } else {
        	posn = reader.getPosition() ;
            String prefixPart = readPrefixPart() ; // Prefix part or keyword
            image += prefixPart;
            embedded2 = TokenStar.createEmbedded();
            embedded2.setImage(prefixPart) ;
            embedded2.setType(TokenTypeStar.KEYWORD) ;
            ch = reader.peekChar() ;
            if ( ch == CH_COLON ) {
                reader.readChar() ;
                embedded2.setType(TokenTypeStar.PREFIXED_NAME) ;
                String ln = readLocalPart() ; // Local part
                embedded2.setImage2(ln) ;
                image += ":"+ln+" ";
            }
        }

        // If we made no progress, nothing found, not even a keyword -- it's an
        // error.
        if ( posn == reader.getPosition() )
            exception("Failed to find a prefix name or keyword: %c(%d;0x%04X)", ch, ch, ch) ;
    	
        token.setEmbeddedToken2(embedded2);
        skip() ;
        ch = reader.peekChar() ;
        
        // ---- Literal
        if ( ch == CH_QUOTE1 || ch == CH_QUOTE2 ) {
        	TokenStar embedded3 = TokenStar.createEmbedded();
            reader.readChar() ;
            int ch2 = reader.peekChar() ;
            if ( ch2 == ch ) {
                reader.readChar() ; // Read potential second quote.
                int ch3 = reader.peekChar() ;
                if ( ch3 == ch ) {
                    reader.readChar() ;
                    String longString = readLongString(ch, false);
                    embedded3.setImage(longString) ;
                    image += longString;
                    TokenTypeStar tt = (ch == CH_QUOTE1) ? TokenTypeStar.LONG_STRING1 : TokenTypeStar.LONG_STRING2 ;
                    embedded3.setType(tt) ;
                } else {
                    // Two quotes then a non-quote.
                    // Must be '' or ""
                    // No need to pushback characters as we know the lexical
                    // form is the empty string.
                    // if ( ch2 != EOF ) reader.pushbackChar(ch2) ;
                    // if ( ch1 != EOF ) reader.pushbackChar(ch1) ; // Must be
                    // '' or ""
                	embedded3.setImage("") ;
                	image += "\"\"";
                	embedded3.setType((ch == CH_QUOTE1) ? TokenTypeStar.STRING1 : TokenTypeStar.STRING2) ;
                }
            } else {
                // Single quote character.
            	String normalStirng = readString(ch, ch);
            	embedded3.setImage(normalStirng) ;
            	image += normalStirng;
                // Single quoted string.
            	embedded3.setType((ch == CH_QUOTE1) ? TokenTypeStar.STRING1 : TokenTypeStar.STRING2) ;
            }

            // Whte space after lexical part of a literal.
            skip() ;
            
            // Literal. Is it @ or ^^
            if ( reader.peekChar() == CH_AT ) {
                reader.readChar() ;
                // White space is not legal here.
                // The Turtle spec terminal is "LANGTAG" which includes the '@'.
                TokenStar mainToken = new TokenStar(embedded3) ;
                mainToken.setType(TokenTypeStar.LITERAL_LANG) ;
                mainToken.setSubToken1(token) ;
                String tag = langTag();
                mainToken.setImage2(tag) ;
                image += tag;
                embedded3 = mainToken ;
            } else if ( reader.peekChar() == '^' ) {
                expect("^^") ;
                // White space is legal after a ^^. 
                // It's not a good idea, but it is legal.
//                // Check no whitespace.
//                int nextCh = reader.peekChar() ;
//                if ( isWhitespace(nextCh) )
//                    exception("No whitespace after ^^ in literal with datatype") ;
                skip() ;
                
                // Stash current token.
                TokenStar mainToken = new TokenStar(embedded3) ;
                
                mainToken.setSubToken1(embedded3) ;//This might be wrong!
                mainToken.setImage(embedded3.getImage()) ;
                
                TokenStar temp = token;
                TokenStar subToken = parseToken() ; // token is overridden
                token = temp;
                if ( !subToken.isIRI() )
                    exception("Datatype URI required after ^^ - URI or prefixed name expected") ;

                mainToken.setSubToken2(subToken) ;
                mainToken.setType(TokenTypeStar.LITERAL_DT) ;
                image += "^^<"+subToken.getImage()+">";

                embedded3 = mainToken ;
            } 
            token.setEmbeddedToken3(embedded3);
        } else if ( ch == CH_UNDERSCORE ) {
        	expect("_:") ;
        	TokenStar embedded3 = TokenStar.createEmbedded();
        	String iri = "_:"+readBlankNodeLabel();
        	image += iri+" ";
        	embedded3.setImage(iri) ;
        	embedded3.setType(TokenTypeStar.BNODE) ;
        	token.setEmbeddedToken3(embedded3);
        	
        } else if ( ch == CH_LT ) {
        	reader.readChar() ;
        	TokenStar embedded3 = TokenStar.createEmbedded();
        	String iri = "<"+readIRI()+"> ";
        	image += iri;
        	embedded3.setImage(iri) ;
        	embedded3.setType(TokenTypeStar.IRI) ;
        	token.setEmbeddedToken3(embedded3);
        	
        } else {
        	posn = reader.getPosition() ;
            String prefixPart = readPrefixPart() ; // Prefix part or keyword
            TokenStar embedded3 = TokenStar.createEmbedded();
            embedded3.setImage(prefixPart) ;
            image += prefixPart;
            embedded3.setType(TokenTypeStar.KEYWORD) ;
            ch = reader.peekChar() ;
            if ( ch == CH_COLON ) {
                reader.readChar() ;
                embedded3.setType(TokenTypeStar.PREFIXED_NAME) ;
                String ln = readLocalPart() ; // Local part
                embedded3.setImage2(ln) ;
                image+=":"+ln;
            }

            // If we made no progress, nothing found, not even a keyword -- it's an error.
            if ( posn == reader.getPosition() )
                exception("Failed to find a prefix name or keyword: %c(%d;0x%04X)", ch, ch, ch) ;
            
            token.setEmbeddedToken3(embedded3);
        }
    	
    	
        ch = reader.peekChar() ;
        if ( ch == CH_GT  ) {
            reader.readChar() ;
            ch = reader.peekChar() ;
            if (ch == CH_GT) {
            	reader.readChar() ;
			} else exception("Expected \">\"");
        } else exception("Expected \">\"");
        
    	return image+">>";
	}


	private static final boolean VeryVeryLaxIRI = false ;
    
    // [8]  IRIREF  ::= '<' ([^#x00-#x20<>"{}|^`\] | UCHAR)* '>'
    private String readIRI() {
        stringBuilder.setLength(0) ;
        for (;;) {
            int ch = reader.readChar() ;
            switch(ch) {
                case EOF:
                    exception("Broken IRI (End of file)") ;
                case NL:
                    exception("Broken IRI (newline): %s", stringBuilder.toString()) ;
                case CR:
                    exception("Broken IRI (CR): %s", stringBuilder.toString()) ;
                case CH_GT:
                    // Done!
                    return stringBuilder.toString() ;
                case CH_RSLASH:
                    if ( VeryVeryLaxIRI )
                        // Includes unicode escapes and also \n etc 
                        ch = readLiteralEscape() ;
                    else
                        // NORMAL
                        ch = readUnicodeEscape() ;
                    // Don't check legality of ch (strict syntax at this point).
                    // That does not mean it is a good idea to bypass checking.
                    // Bad characters will lead to trouble elsewhere.
                    break ;
                case CH_LT:
                    // Probably a corrupt file so not a warning.
                    exception("Bad character in IRI (bad character: '<'): <%s<...>", stringBuilder.toString()) ;
                case TAB:
                    exception("Bad character in IRI (Tab character): <%s[tab]...>", stringBuilder.toString()) ;
                case SPC:
                    warning("Bad character in IRI (space): <%s[space]...>", stringBuilder.toString()) ;
                case '{': case '}': case '"': case '|': case '^': case '`' :
                    if ( ! VeryVeryLaxIRI )
                        warning("Illegal character in IRI (codepoint 0x%02X, '%c'): <%s[%c]...>", ch, (char)ch, stringBuilder.toString(), (char)ch) ;
                default:
                    if ( ch <= 0x19 )
                        warning("Illegal character in IRI (control char 0x%02X): %s", ch, stringBuilder.toString()) ;
            }
            insertCodepoint(stringBuilder, ch) ;
        }
    }
    
    // Read a unicode escape : does not allow \\ bypass
    private final int readUnicodeEscape() {
        int ch = reader.readChar() ;
        if ( ch == EOF )
            exception("Broken escape sequence") ;
    
        switch (ch) {
            case 'u': return readUnicode4Escape(); 
            case 'U': return readUnicode8Escape(); 
            default:
                exception("Illegal unicode escape sequence value: \\%c (0x%02X)", ch, ch);
        }
        return 0 ;
    }

    private void readPrefixedNameOrKeyword(TokenStar token) {
        long posn = reader.getPosition() ;
        String prefixPart = readPrefixPart() ; // Prefix part or keyword
        token.setImage(prefixPart) ;
        token.setType(TokenTypeStar.KEYWORD) ;
        int ch = reader.peekChar() ;
        if ( ch == CH_COLON ) {
            reader.readChar() ;
            token.setType(TokenTypeStar.PREFIXED_NAME) ;
            String ln = readLocalPart() ; // Local part
            token.setImage2(ln) ;
            if ( Checking )
                checkPrefixedName(token.getImage(), token.getImage2()) ;
        }

        // If we made no progress, nothing found, not even a keyword -- it's an
        // error.
        if ( posn == reader.getPosition() )
            exception("Failed to find a prefix name or keyword: %c(%d;0x%04X)", ch, ch, ch) ;

        if ( Checking )
            checkKeyword(token.getImage()) ;

    }
    
    /*
    The token rules from SPARQL and Turtle.
    PNAME_NS       ::=  PN_PREFIX? ':'
    PNAME_LN       ::=  PNAME_NS PN_LOCAL
    
    PN_CHARS_BASE  ::=  [A-Z] | [a-z] | [#x00C0-#x00D6] | [#x00D8-#x00F6] | [#x00F8-#x02FF] | [#x0370-#x037D] | [#x037F-#x1FFF] | [#x200C-#x200D] | [#x2070-#x218F] | [#x2C00-#x2FEF] | [#x3001-#xD7FF] | [#xF900-#xFDCF] | [#xFDF0-#xFFFD] | [#x10000-#xEFFFF]
    PN_CHARS_U  ::=  PN_CHARS_BASE | '_'
    PN_CHARS  ::=  PN_CHARS_U | '-' | [0-9] | #x00B7 | [#x0300-#x036F] | [#x203F-#x2040]
    
    PN_PREFIX  ::=  PN_CHARS_BASE ((PN_CHARS|'.')* PN_CHARS)?
    PN_LOCAL  ::=  (PN_CHARS_U | ':' | [0-9] | PLX ) ((PN_CHARS | '.' | ':' | PLX)* (PN_CHARS | ':' | PLX) )?
    PLX  ::=  PERCENT | PN_LOCAL_ESC
    PERCENT  ::=  '%' HEX HEX
    HEX  ::=  [0-9] | [A-F] | [a-f]
    PN_LOCAL_ESC  ::=  '\' ( '_' | '~' | '.' | '-' | '!' | '$' | '&' | "'" | '(' | ')' | '*' | '+' | ',' | ';' | '=' | '/' | '?' | '#' | '@' | '%' )
    */
    
    private String readPrefixPart()
    { return readSegment(false) ; }
    
    private String readLocalPart()
    { return readSegment(true) ; }

    private String readSegment(boolean isLocalPart) { 
        // Prefix: PN_CHARS_BASE                       ((PN_CHARS|'.')* PN_CHARS)?
        // Local: ( PN_CHARS_U | ':' | [0-9] | PLX )   ((PN_CHARS | '.' | ':' | PLX)* (PN_CHARS | ':' | PLX) )?

        // RiotChars has isPNChars_U_N for   ( PN_CHARS_U | [0-9] )
        stringBuilder.setLength(0) ;

        // -- Test first character
        int ch = reader.peekChar() ;
        if ( ch == EOF )
            return "" ;
        if ( isLocalPart ) {
            if ( ch == CH_COLON ) {
                reader.readChar() ;
                stringBuilder.append((char)ch) ;
            }

            // processPLX
            else if ( ch == CH_PERCENT || ch == CH_RSLASH ) {
                reader.readChar() ;
                processPLX(ch) ;
            } else if ( RiotChars.isPNChars_U_N(ch) ) {
                stringBuilder.append((char)ch) ;
                reader.readChar() ;
            } else
                return "" ;
        } else {
            if ( !RiotChars.isPNCharsBase(ch) )
                return "" ;
            stringBuilder.append((char)ch) ;
            reader.readChar() ;
        }
        // Done first character
        int chDot = 0 ;

        for (;;) {
            ch = reader.peekChar() ;
            boolean valid = false ;

            if ( isLocalPart && (ch == CH_PERCENT || ch == CH_RSLASH) ) {
                reader.readChar() ;
                if ( chDot != 0 )
                    stringBuilder.append((char)chDot) ;
                processPLX(ch) ;
                chDot = 0 ;
                continue ;
            }

            // Single valid characters
            if ( isLocalPart && ch == CH_COLON )
                valid = true ;
            else if ( isPNChars(ch) )
                valid = true ;
            else if ( ch == CH_DOT )
                valid = true ;
            else
                valid = false ;

            if ( !valid )
                break ; // Exit loop

            // Valid character.
            reader.readChar() ;
            // Was there also a DOT previous loop?
            if ( chDot != 0 ) {
                stringBuilder.append((char)chDot) ;
                chDot = 0 ;
            }

            if ( ch != CH_DOT )
                stringBuilder.append((char)ch) ;
            else
                // DOT - delay until next loop.
                chDot = ch ;
        }

        // On exit, chDot may hold a character.

        if ( chDot == CH_DOT )
            // Unread it.
            reader.pushbackChar(chDot) ;
        return stringBuilder.toString() ;
    }

    // Process PLX (percent or character escape for a prefixed name)
    private void processPLX(int ch)
    {
        if ( ch == CH_PERCENT )
        {
            stringBuilder.append((char)ch) ;

            ch = reader.peekChar() ;
            if ( ! isHexChar(ch) )
                exception("Not a hex charcater: '%c'",ch) ;
            stringBuilder.append((char)ch) ;
            reader.readChar() ;

            ch = reader.peekChar() ;
            if ( ! isHexChar(ch) )
                exception("Not a hex charcater: '%c'",ch) ;
            stringBuilder.append((char)ch) ;
            reader.readChar() ;
        }
        else if ( ch == CH_RSLASH )
        {
            ch = readCharEscape() ;
            stringBuilder.append((char)ch) ;
        }
        else
            throw new ARQInternalErrorException("Not a '\\' or a '%' character") ;
    }
    
    // Get characters between two markers.
    // strEscapes may be processed
    private String readString(int startCh, int endCh) {
        long y = getLine() ;
        long x = getColumn() ;
        stringBuilder.setLength(0) ;
        // Assumes first delimiter char read already.
        // Reads terminating delimiter 

        for (;;) {
            int ch = reader.readChar() ;
            if ( ch == EOF ) {
                // if ( endNL ) return stringBuilder.toString() ;
                exception("Broken token: " + stringBuilder.toString(), y, x) ;
            }

            if ( ch == NL )
                exception("Broken token (newline): " + stringBuilder.toString(), y, x) ;

            if ( ch == endCh ) {
                return stringBuilder.toString() ;
            }

            if ( ch == CH_RSLASH )
                ch = readLiteralEscape() ;
            insertCodepoint(stringBuilder, ch) ;
        }
    }

    private String readLongString(int quoteChar, boolean endNL) {
        stringBuilder.setLength(0) ;
        for (;;) {
            int ch = reader.readChar() ;
            if ( ch == EOF ) {
                if ( endNL )
                    return stringBuilder.toString() ;
                exception("Broken long string") ;
            }

            if ( ch == quoteChar ) {
                if ( threeQuotes(quoteChar) )
                    return stringBuilder.toString() ;
            }

            if ( ch == CH_RSLASH )
                ch = readLiteralEscape() ;
            insertCodepoint(stringBuilder, ch) ;
        }
    }

    private String readWord(boolean leadingDigitAllowed)
    { return readWordSub(leadingDigitAllowed, false) ; }

    // A 'word' is used in several places:
    //   keyword
    //   prefix part of prefix name 
    //   local part of prefix name (allows digits)
    
    static private char[] extraCharsWord = new char[] {'_', '.' , '-'};
    
    private String readWordSub(boolean leadingDigitAllowed, boolean leadingSignAllowed) {
        return readCharsAnd(leadingDigitAllowed, leadingSignAllowed, extraCharsWord, false) ;
    }

    static private char[] extraCharsVar = new char[]{'_', '.', '-', '?', '@', '+'} ;

    private String readVarName() {
        return readCharsAnd(true, true, extraCharsVar, true) ;
    }
    
    // See also readBlankNodeLabel
    
    private String readCharsAnd(boolean leadingDigitAllowed, boolean leadingSignAllowed, char[] extraChars,
                                boolean allowFinalDot) {
        stringBuilder.setLength(0) ;
        int idx = 0 ;
        if ( !leadingDigitAllowed ) {
            int ch = reader.peekChar() ;
            if ( Character.isDigit(ch) )
                return "" ;
        }

        // Used for local part of prefix names =>
        if ( !leadingSignAllowed ) {
            int ch = reader.peekChar() ;
            if ( ch == '-' || ch == '+' )
                return "" ;
        }

        for (;; idx++) {
            int ch = reader.peekChar() ;

            if ( isAlphaNumeric(ch) || Chars.charInArray(ch, extraChars) ) {
                reader.readChar() ;
                stringBuilder.append((char)ch) ;
                continue ;
            } else
                // Inappropriate character.
                break ;

        }

        if ( !allowFinalDot ) {
            // BAD : assumes pushbackChar is infinite.
            // Check is ends in "."
            while (idx > 0 && stringBuilder.charAt(idx - 1) == CH_DOT) {
                // Push back the dot.
                reader.pushbackChar(CH_DOT) ;
                stringBuilder.setLength(idx - 1) ;
                idx-- ;
            }
        }
        return stringBuilder.toString() ;
    }

    // BLANK_NODE_LABEL    ::=     '_:' (PN_CHARS_U | [0-9]) ((PN_CHARS | '.')* PN_CHARS)?

    private String readBlankNodeLabel() {
        stringBuilder.setLength(0) ;
        // First character.
        {
            int ch = reader.peekChar() ;
            if ( ch == EOF )
                exception("Blank node label missing (EOF found)") ;
            if ( isWhitespace(ch) )
                exception("Blank node label missing") ;
            // if ( ! isAlpha(ch) && ch != '_' )
            // Not strict

            if ( !RiotChars.isPNChars_U_N(ch) )
                exception("Blank node label does not start with alphabetic or _ :" + (char)ch) ;
            reader.readChar() ;
            stringBuilder.append((char)ch) ;
        }

        // Remainder. DOT can't be last so do a delay on that.

        int chDot = 0 ;

        for (;;) {
            int ch = reader.peekChar() ;
            if ( ch == EOF )
                break ;

            // DOT magic.
            if ( !(RiotChars.isPNChars(ch) || ch == CH_DOT) )
                break ;
            reader.readChar() ;

            if ( chDot != 0 ) {
                stringBuilder.append((char)chDot) ;
                chDot = 0 ;
            }

            if ( ch != CH_DOT )
                stringBuilder.append((char)ch) ;
            else
                // DOT - delay until next loop.
                chDot = ch ;
        }

        if ( chDot == CH_DOT )
            // Unread it.
            reader.pushbackChar(chDot) ;

        // if ( ! seen )
        // exception("Blank node label missing") ;
        return stringBuilder.toString() ;
    }

    /*
     * [146]  INTEGER  ::=  [0-9]+
     * [147]  DECIMAL  ::=  [0-9]* '.' [0-9]+
     * [148]  DOUBLE  ::=  [0-9]+ '.' [0-9]* EXPONENT | '.' ([0-9])+ EXPONENT | ([0-9])+ EXPONENT
     * []     hex             ::=     0x0123456789ABCDEFG
     */
    private void readNumber() {
        // One entry, definitely a number.
        // Beware of '.' as a (non) decimal.
        /*
        maybeSign()
        digits()
        if dot ==> decimal, digits
        if e   ==> double, maybeSign, digits
        else
            check not "." for decimal.
        */
        boolean isDouble = false ;
        boolean isDecimal = false ;
        stringBuilder.setLength(0) ;
        
        /*
        readPossibleSign(stringBuilder) ;
        readDigits may be hex
        readDot
        readDigits
        readExponent.
        */
        
        int x = 0 ; // Digits before a dot.
        int ch = reader.peekChar() ;
        if ( ch == '0' ) {
            x++ ;
            reader.readChar() ;
            stringBuilder.append((char)ch) ;
            ch = reader.peekChar() ;
            if ( ch == 'x' || ch == 'X' ) {
                reader.readChar() ;
                stringBuilder.append((char)ch) ;
                readHex(reader, stringBuilder) ;
                token.setImage(stringBuilder.toString()) ;
                token.setType(TokenTypeStar.HEX) ;
                return ;
            }
        } else if ( ch == '-' || ch == '+' ) {
            readPossibleSign(stringBuilder) ;
        }

        x += readDigits(stringBuilder) ;
//        if ( x == 0 ) {}
        ch = reader.peekChar() ;
        if ( ch == CH_DOT ) {
            reader.readChar() ;
            stringBuilder.append(CH_DOT) ;
            isDecimal = true ; // Includes things that will be doubles.
            readDigits(stringBuilder) ;
        }

        if ( x == 0 && !isDecimal )
            // Possible a tokenizer error - should not have entered readNumber
            // in the first place.
            exception("Unrecognized as number") ;

        if ( exponent(stringBuilder) ) {
            isDouble = true ;
            isDecimal = false ;
        }

        // Final part - "decimal" 123. is an integer 123 and a DOT.
        if ( isDecimal ) {
            int len = stringBuilder.length() ;
            if ( stringBuilder.charAt(len - 1) == CH_DOT ) {
                stringBuilder.setLength(len - 1) ;
                reader.pushbackChar(CH_DOT) ;
                isDecimal = false ;
            }
        }

        token.setImage(stringBuilder.toString()) ;
        if ( isDouble )
            token.setType(TokenTypeStar.DOUBLE) ;
        else if ( isDecimal )
            token.setType(TokenTypeStar.DECIMAL) ;
        else
            token.setType(TokenTypeStar.INTEGER) ;
    }

    private static void readHex(PeekReader reader, StringBuilder sb) {
        // Just after the 0x, which are in sb
        int x = 0 ;
        for (;;) {
            int ch = reader.peekChar() ;

            if ( !isHexChar(ch) )
                break ;
            reader.readChar() ;
            sb.append((char)ch) ;
            x++ ;
        }
        if ( x == 0 )
            exception(reader, "No hex characters after " + sb.toString()) ;
    }

    private int readDigits(StringBuilder buffer) {
        int count = 0 ;
        for (;;) {
            int ch = reader.peekChar() ;
            if ( !range(ch, '0', '9') )
                break ;
            reader.readChar() ;
            buffer.append((char)ch) ;
            count++ ;
        }
        return count ;
    }

    private void readPossibleSign(StringBuilder sb) {
        int ch = reader.peekChar() ;
        if ( ch == '-' || ch == '+' ) {
            reader.readChar() ;
            sb.append((char)ch) ;
        }
    }

    // Assume have read the first quote char.
    // On return:
    //   If false, have moved over no more characters (due to pushbacks) 
    //   If true, at end of 3 quotes
    private boolean threeQuotes(int ch) {
        // reader.readChar() ; // Read first quote.
        int ch2 = reader.peekChar() ;
        if ( ch2 != ch ) {
            // reader.pushbackChar(ch2) ;
            return false ;
        }

        reader.readChar() ; // Read second quote.
        int ch3 = reader.peekChar() ;
        if ( ch3 != ch ) {
            // reader.pushbackChar(ch3) ;
            reader.pushbackChar(ch2) ;
            return false ;
        }

        // Three quotes.
        reader.readChar() ; // Read third quote.
        return true ;
    }

    private boolean exponent(StringBuilder sb) {
        int ch = reader.peekChar() ;
        if ( ch != 'e' && ch != 'E' )
            return false ;
        reader.readChar() ;
        sb.append((char)ch) ;
        readPossibleSign(sb) ;
        int x = readDigits(sb) ;
        if ( x == 0 )
            exception("Malformed double: " + sb) ;
        return true ;
    }

    private String langTag() {
        stringBuilder.setLength(0) ;
        a2z(stringBuilder) ;
        if ( stringBuilder.length() == 0 )
            exception("Bad language tag") ;
        for (;;) {
            int ch = reader.peekChar() ;
            if ( ch == '-' ) {
                reader.readChar() ;
                stringBuilder.append('-') ;
                int x = stringBuilder.length() ;
                a2zN(stringBuilder) ;
                if ( stringBuilder.length() == x )
                    exception("Bad language tag") ;
            } else
                break ;
        }
        return stringBuilder.toString().intern() ;
    }
    
    // ASCII-only e.g. in lang tags.
    private void a2z(StringBuilder sb2) {
        for (;;) {
            int ch = reader.peekChar() ;
            if ( isA2Z(ch) ) {
                reader.readChar() ;
                stringBuilder.append((char)ch) ;
            } else
                return ;
        }
    }

    private void a2zN(StringBuilder sb2) {
        for (;;) {
            int ch = reader.peekChar() ;
            if ( isA2ZN(ch) ) {
                reader.readChar() ;
                stringBuilder.append((char)ch) ;
            } else
                return ;
        }
    }

    private void insertCodepoint(StringBuilder buffer, int ch) {
        if ( Character.charCount(ch) == 1 )
            buffer.append((char)ch) ;
        else {
            // Convert to UTF-16. Note that the rest of any system this is used
            // in must also respect codepoints and surrogate pairs.
            if ( !Character.isDefined(ch) && !Character.isSupplementaryCodePoint(ch) )
                exception("Illegal codepoint: 0x%04X", ch) ;
            char[] chars = Character.toChars(ch) ;
            buffer.append(chars) ;
        }
    }

    public long getColumn() {
        return reader.getColNum() ;
    }

    public long getLine() {
        return reader.getLineNum() ;
    }

    // ---- Routines to check tokens

    private void checkBlankNode(String blankNodeLabel) {
        if ( checker != null )
            checker.checkBlankNode(blankNodeLabel) ;
    }

    private void checkLiteralLang(String lexicalForm, String langTag) {
        if ( checker != null )
            checker.checkLiteralLang(lexicalForm, langTag) ;
    }

    private void checkLiteralDT(String lexicalForm, TokenStar datatype) {
        if ( checker != null )
            checker.checkLiteralDT(lexicalForm, datatype) ;
    }

    private void checkString(String string) {
        if ( checker != null )
            checker.checkString(string) ;
    }

    private void checkURI(String uriStr) {
        if ( checker != null )
            checker.checkURI(uriStr) ;
    }

    private void checkNumber(String image, String datatype) {
        if ( checker != null )
            checker.checkNumber(image, datatype) ;
    }

    private void checkVariable(String tokenImage) {
        if ( checker != null )
            checker.checkVariable(tokenImage) ;
    }

    private void checkDirective(int cntrlCode) {
        if ( checker != null )
            checker.checkDirective(cntrlCode) ;
    }

    private void checkKeyword(String tokenImage) {
        if ( checker != null )
            checker.checkKeyword(tokenImage) ;
    }

    private void checkPrefixedName(String tokenImage, String tokenImage2) {
        if ( checker != null )
            checker.checkPrefixedName(tokenImage, tokenImage2) ;
    }

    private void checkControl(int code) {
        if ( checker != null )
            checker.checkControl(code) ;
    }

    // ---- Escape sequences

    private final int readLiteralEscape() {
        int c = reader.readChar() ;
        if ( c == EOF )
            exception("Escape sequence not completed") ;

        switch (c) {
            case 'n':   return NL ; 
            case 'r':   return CR ;
            case 't':   return '\t' ;
            case 'f':   return '\f' ;
            case 'b':   return BSPACE ;
            case '"':   return '"' ;
            case '\'':  return '\'' ;
            case '\\':  return '\\' ;
            case 'u':   return readUnicode4Escape();
            case 'U':   return readUnicode8Escape();
            default:
                exception("Illegal escape sequence value: %c (0x%02X)", c, c);
                return 0 ;
        }
    }

    private final int readCharEscape() {
        // PN_LOCAL_ESC ::= '\' ( '_' | '~' | '.' | '-' | '!' | '$' | '&' | "'"
        // | '(' | ')' | '*' | '+' | ',' | ';' | '=' | '/' | '?' | '#' | '@' |
        // '%' )

        int c = reader.readChar() ;
        if ( c == EOF )
            exception("Escape sequence not completed") ;

        switch (c) {
            case '_': case '~': case '.':  case '-':  case '!':  case '$':  case '&': 
            case '\'':  
            case '(':  case ')':  case '*':  case '+':  case ',':  case ';': 
            case '=':  case '/':  case '?':  case '#':  case '@':  case '%':
                return c ;
            default:
                exception("illegal character escape value: \\%c", c);
                return 0 ;
        }
    }
    
    private final
    int readUnicode4Escape() { return readHexSequence(4) ; }
    
    private final int readUnicode8Escape() {
        int ch8 = readHexSequence(8) ;
        if ( ch8 > Character.MAX_CODE_POINT )
            exception("Illegal code point in \\U sequence value: 0x%08X", ch8) ;
        return ch8 ;
    }

    private final int readHexSequence(int N) {
        int x = 0 ;
        for (int i = 0; i < N; i++) {
            int d = readHexChar() ;
            if ( d < 0 )
                return -1 ;
            x = (x << 4) + d ;
        }
        return x ;
    }

    private final int readHexChar() {
        int ch = reader.readChar() ;
        if ( ch == EOF )
            exception("Not a hexadecimal character (end of file)") ;

        int x = valHexChar(ch) ;
        if ( x != -1 )
            return x ;
        exception("Not a hexadecimal character: " + (char)ch) ;
        return -1 ;
    }

    private boolean expect(String str) {
        for (int i = 0; i < str.length(); i++) {
            char want = str.charAt(i) ;
            if ( reader.eof() ) {
                exception("End of input during expected string: " + str) ;
                return false ;
            }
            int inChar = reader.peekChar() ;
            if ( inChar != want ) {
                // System.err.println("N-triple reader error");
                exception("expected \"" + str + "\"") ;
                return false ;
            }
            reader.readChar() ;
        }
        return true ;
    }

    private void warning(String message, Object... args) {
        exception(message, args); 
    }
    
    private void exception(String message, Object... args) {
        exception$(message, reader.getLineNum(), reader.getColNum(), args) ;
    }

    private static void exception(PeekReader reader, String message, Object... args) {
        exception$(message, reader.getLineNum(), reader.getColNum(), args) ;
    }

    private static void exception$(String message, long line, long col, Object... args) {
        throw new RiotParseException(String.format(message, args), line, col) ;
    }
}

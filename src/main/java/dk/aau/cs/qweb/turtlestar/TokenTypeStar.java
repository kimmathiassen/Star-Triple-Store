package dk.aau.cs.qweb.turtlestar;

import org.apache.jena.riot.tokens.TokenType;

/**
 * The types of tokens
 * This file is based on {@link TokenType}, we added EMBEDDED
 *
 */
public enum TokenTypeStar {
    NODE, IRI, PREFIXED_NAME, BNODE,EMBEDDED,
    // BOOLEAN,
    // One kind of string?
    STRING, // Token created programmatically and superclass of ...
    STRING1, STRING2, LONG_STRING1, LONG_STRING2,

    LITERAL_LANG, LITERAL_DT, INTEGER, DECIMAL, DOUBLE,

    // Not RDF
    KEYWORD, VAR, HEX, CNTRL,   // Starts with *
    UNDERSCORE,                 // In RDF, UNDERSCORE is only visible if BNode processing is not enabled.


    // COLON is only visible if prefix names are not being processed.
    DOT, COMMA, SEMICOLON, COLON, DIRECTIVE,
    // LT, GT, LE, GE are only visible if IRI processing is not enabled.
    LT, GT, LE, GE, LOGICAL_AND, LOGICAL_OR, // && and ||
    VBAR, AMPHERSAND,

    LBRACE, RBRACE,     // {}
    LPAREN, RPAREN,                 // ()
    LBRACKET, RBRACKET,             // []
    // = == + - * / \
    EQUALS, EQUIVALENT, PLUS, MINUS, STAR, SLASH, RSLASH,
    // Whitespace, any comment, (one line comment, multiline comment)
    NL, WS, COMMENT, COMMENT1, COMMENT2, EOF
}

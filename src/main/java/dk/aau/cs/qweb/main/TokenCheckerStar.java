package dk.aau.cs.qweb.main;

public interface TokenCheckerStar
{
    public void checkBlankNode(String blankNodeLabel) ;
    public void checkLiteralLang(String lexicalForm, String langTag) ;
    public void checkLiteralDT(String lexicalForm, TokenStar datatype) ;
    public void checkString(String string) ;
    public void checkURI(String uriStr) ;
    public void checkNumber(String lexical, String datatypeURI) ;
    public void checkVariable(String tokenImage) ;
    public void checkDirective(int cntrlCode) ;
    public void checkKeyword(String lexical) ;
    public void checkPrefixedName(String prefixName, String localName) ;
    public void checkControl(int code) ;
}
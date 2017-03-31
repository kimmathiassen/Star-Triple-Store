package dk.aau.cs.qweb.turtlestar;

import java.io.InputStream;
import java.io.Reader;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.jena.atlas.io.PeekReader;
import org.apache.jena.atlas.web.ContentType;
import org.apache.jena.riot.ReaderRIOT;
import org.apache.jena.riot.lang.LabelToNode;
import org.apache.jena.riot.system.ErrorHandler;
import org.apache.jena.riot.system.ErrorHandlerFactory;
import org.apache.jena.riot.system.FactoryRDFStd;
import org.apache.jena.riot.system.IRIResolver;
import org.apache.jena.riot.system.ParserProfile;
import org.apache.jena.riot.system.PrefixMapFactory;
import org.apache.jena.riot.system.Prologue;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.SyntaxLabels;
import org.apache.jena.sparql.util.Context;

public class TTLSReader implements ReaderRIOT {
	Prologue prologue = null;
	ErrorHandler handler = null;
	ParserProfileStar profile = null;
	
	public TTLSReader() {
		 handler = ErrorHandlerFactory.getDefaultErrorHandler();
	}

	@Override
	public void read(InputStream in, String baseURI, ContentType ct, StreamRDF output, Context context) {
		prologue = new Prologue(PrefixMapFactory.createForInput(), IRIResolver.create(baseURI)) ;
		PeekReader peekReader = PeekReader.makeUTF8(in) ;
        TokenizerStar tokenizer = new TokenizerStar(peekReader) ;
        ErrorHandler handler = ErrorHandlerFactory.getDefaultErrorHandler();
        
        LabelToNode labelMapping = SyntaxLabels.createLabelToNode();
        profile = new ParserProfileStar(prologue, handler, new FactoryRDFStd(labelMapping)) ;
        
		LangTurtleStar parser = new LangTurtleStar(tokenizer, profile, output); 
        parser.parse();
	}

	@Override
	public void read(Reader reader, String baseURI, ContentType ct, StreamRDF output, Context context) {
		TokenizerStar tokenizer = new TokenizerStar( (PeekReader) reader) ;
        
        LabelToNode labelMapping = SyntaxLabels.createLabelToNode();
        profile = new ParserProfileStar(prologue, handler, new FactoryRDFStd(labelMapping)) ;
        
		LangTurtleStar parser = new LangTurtleStar(tokenizer, profile, output); 
        parser.parse();
	}

	@Override
	public ErrorHandler getErrorHandler() {
		return handler;
	}

	@Override
	public void setErrorHandler(ErrorHandler errorHandler) {
		throw new NotImplementedException("No need to set an error handler, it is never used");
		//handler = errorHandler;
	}

	@Override
	public ParserProfile getParserProfile() {
		return (ParserProfile) profile;
	}

	@Override
	public void setParserProfile(ParserProfile profile) {
		throw new NotImplementedException("No need to set an ParserProfile, it is never used");
		//this.profile = (ParserProfileStar) profile;
	}
}

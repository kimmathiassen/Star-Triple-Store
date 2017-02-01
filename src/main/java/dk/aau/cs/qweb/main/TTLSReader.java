package dk.aau.cs.qweb.main;

import java.io.InputStream;
import java.io.Reader;

import org.apache.jena.atlas.web.ContentType;
import org.apache.jena.riot.ReaderRIOT;
import org.apache.jena.riot.system.ErrorHandler;
import org.apache.jena.riot.system.ParserProfile;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.sparql.util.Context;

public class TTLSReader implements ReaderRIOT {

	@Override
	public void read(InputStream in, String baseURI, ContentType ct, StreamRDF output, Context context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void read(Reader reader, String baseURI, ContentType ct, StreamRDF output, Context context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ErrorHandler getErrorHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setErrorHandler(ErrorHandler errorHandler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ParserProfile getParserProfile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParserProfile(ParserProfile profile) {
		// TODO Auto-generated method stub
		
	}
}

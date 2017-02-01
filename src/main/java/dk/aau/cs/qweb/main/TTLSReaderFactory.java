package dk.aau.cs.qweb.main;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.ReaderRIOT;
import org.apache.jena.riot.ReaderRIOTFactory;
import org.apache.jena.riot.system.ParserProfile;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.tokens.Tokenizer;

public class TTLSReaderFactory implements ReaderRIOTFactory {

	@Override
	public ReaderRIOT create(Lang language) {
		Tokenizer tokens;
		ParserProfile profile;
		StreamRDF dest;
        return new TTLSReader() ;
    }

}

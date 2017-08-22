package dk.aau.cs.qweb.turtlestar;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.ReaderRIOT;
import org.apache.jena.riot.ReaderRIOTFactory;

public class TTLSReaderFactory implements ReaderRIOTFactory {

	@Override
	public ReaderRIOT create(Lang language) {
        return new TTLSReader() ;
    }
}

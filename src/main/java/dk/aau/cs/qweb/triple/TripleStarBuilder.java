package dk.aau.cs.qweb.triple;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.graph.Triple;

import dk.aau.cs.qweb.dictionary.MyDictionary;
import dk.aau.cs.qweb.helper.BitHelper;

public class TripleStarBuilder {

	public List<TripleStar> createTriple(Triple t) {
		MyDictionary dict = MyDictionary.getInstance();
		List<TripleStar> triples = new ArrayList<TripleStar>();
		Key subject = dict.createKey(t.getSubject());
		Key predicate = dict.createKey(t.getPredicate());
		Key object = dict.createKey(t.getObject());
		
		if (BitHelper.isIdAnEmbeddedTriple(subject)) {
			Key s1 = new Key(BitHelper.getEmbeddedSubject(subject));
			Key p1 = new Key(BitHelper.getEmbeddedPredicate(subject));
			Key o1 = new Key(BitHelper.getEmbeddedObject(subject));
			triples.add(new TripleStar(s1,p1,o1));
		}
		
		if (BitHelper.isIdAnEmbeddedTriple(object)) {
			Key s2 = new Key(BitHelper.getEmbeddedSubject(object));
			Key p2 = new Key(BitHelper.getEmbeddedPredicate(object));
			Key o2 = new Key(BitHelper.getEmbeddedObject(object));
			triples.add(new TripleStar(s2,p2,o2));
		}
		
		triples.add(new TripleStar(subject,predicate,object));
		
		return triples;
	}
}

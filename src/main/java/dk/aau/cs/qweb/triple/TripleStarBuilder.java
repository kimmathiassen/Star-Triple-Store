package dk.aau.cs.qweb.triple;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.graph.Triple;

import dk.aau.cs.qweb.dictionary.NodeDictionary;
import dk.aau.cs.qweb.dictionary.NodeDictionaryFactory;
import dk.aau.cs.qweb.helper.BitHelper;
import dk.aau.cs.qweb.node.EmbeddedNode;

/**
 * Class for creating TripleStar(s) from a Jena Triple.
 * One Jena triple corresponds to multiple TripleStars 
 * e.g. 
 * The Jena triple: <<s1,p2,o1>> p, o
 * will result in the following two StarTriples being created:
 * - s1,p2,o1
 * - embedded, p, o (where "embedded" is they key of the fist triple)
 */
public class TripleStarBuilder {
	public List<TripleStar> createTriple(Triple triple) {
		NodeDictionary dict = NodeDictionaryFactory.getDictionary();;
		List<TripleStar> triples = new ArrayList<TripleStar>();
		Key subject = dict.createKey(triple.getSubject());
		Key predicate = dict.createKey(triple.getPredicate());
		Key object = dict.createKey(triple.getObject());
		
		if (BitHelper.isReferenceBitSet(subject)) {
			Key s1 = dict.createKey(((EmbeddedNode)triple.getSubject()).getSubject());
			Key p1 = dict.createKey(((EmbeddedNode)triple.getSubject()).getPredicate());
			Key o1 = dict.createKey(((EmbeddedNode)triple.getSubject()).getObject());
			triples.add(new TripleStar(s1,p1,o1));
		} else if (BitHelper.isIdAnEmbeddedTriple(subject)) {
			Key s1 = new Key(BitHelper.getEmbeddedSubject(subject));
			Key p1 = new Key(BitHelper.getEmbeddedPredicate(subject));
			Key o1 = new Key(BitHelper.getEmbeddedObject(subject));
			triples.add(new TripleStar(s1,p1,o1));
		}
		
		if (BitHelper.isReferenceBitSet(object)) {
			Key s2 = dict.createKey(((EmbeddedNode)triple.getObject()).getSubject());
			Key p2 = dict.createKey(((EmbeddedNode)triple.getObject()).getPredicate());
			Key o2 = dict.createKey(((EmbeddedNode)triple.getObject()).getObject());
			triples.add(new TripleStar(s2,p2,o2)); 
		} else if (BitHelper.isIdAnEmbeddedTriple(object)) {
			Key s2 = new Key(BitHelper.getEmbeddedSubject(object));
			Key p2 = new Key(BitHelper.getEmbeddedPredicate(object));
			Key o2 = new Key(BitHelper.getEmbeddedObject(object));
			triples.add(new TripleStar(s2,p2,o2));
		}
		
		triples.add(new TripleStar(subject,predicate,object));
		return triples;
	}
	
}

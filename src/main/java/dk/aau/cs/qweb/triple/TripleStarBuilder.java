package dk.aau.cs.qweb.triple;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.graph.Triple;

import dk.aau.cs.qweb.dictionary.HashNodeDictionary;
import dk.aau.cs.qweb.helper.BitHelper;
import dk.aau.cs.qweb.node.Node_Triple;

public class TripleStarBuilder {

//	public List<TripleStar> createTriple(Triple t) {
//		//There might be a problem here in regards to nodes being added multiple times, as in there is some code somewhere that does the same.
//		NodeDictionary dict = NodeDictionary.getInstance();
//		List<TripleStar> triples = new ArrayList<TripleStar>();
//		Key subject = dict.createKey(t.getSubject());
//		Key predicate = dict.createKey(t.getPredicate());
//		Key object = dict.createKey(t.getObject());
//		
//		if (BitHelper.isReferenceBitSet(subject)) {
//			Key s1 = dict.createKey(((Node_Triple)t.getSubject()).getSubject());
//			Key p1 = dict.createKey(((Node_Triple)t.getSubject()).getPredicate());
//			Key o1 = dict.createKey(((Node_Triple)t.getSubject()).getObject());
//			triples.add(new TripleStar(s1,p1,o1));
//		} else if (BitHelper.isIdAnEmbeddedTriple(subject)) {
//			Key s1 = new Key(BitHelper.getEmbeddedSubject(subject));
//			Key p1 = new Key(BitHelper.getEmbeddedPredicate(subject));
//			Key o1 = new Key(BitHelper.getEmbeddedObject(subject));
//			triples.add(new TripleStar(s1,p1,o1));
//		}
//		
//		if (BitHelper.isReferenceBitSet(object)) {
//			Key s2 = dict.createKey(((Node_Triple)t.getObject()).getSubject());
//			Key p2 = dict.createKey(((Node_Triple)t.getObject()).getPredicate());
//			Key o2 = dict.createKey(((Node_Triple)t.getObject()).getObject());
//			triples.add(new TripleStar(s2,p2,o2)); 
//		} else if (BitHelper.isIdAnEmbeddedTriple(object)) {
//			Key s2 = new Key(BitHelper.getEmbeddedSubject(object));
//			Key p2 = new Key(BitHelper.getEmbeddedPredicate(object));
//			Key o2 = new Key(BitHelper.getEmbeddedObject(object));
//			triples.add(new TripleStar(s2,p2,o2));
//		}
//		
//		triples.add(new TripleStar(subject,predicate,object));
//		
//		return triples;
//	}
	
	public List<TripleStar> createTriple(Triple t) {
		//There might be a problem here in regards to nodes being added multiple times, as in there is some code somewhere that does the same.
		HashNodeDictionary dict = HashNodeDictionary.getInstance();
		Key subject = dict.createKey(t.getSubject());
		Key predicate = dict.createKey(t.getPredicate());
		Key object = dict.createKey(t.getObject());
		
		if (BitHelper.isReferenceBitSet(subject)) {
			Key s1 = dict.createKey(((Node_Triple)t.getSubject()).getSubject());
			Key p1 = dict.createKey(((Node_Triple)t.getSubject()).getPredicate());
			Key o1 = dict.createKey(((Node_Triple)t.getSubject()).getObject());
		} else if (BitHelper.isIdAnEmbeddedTriple(subject)) {
			Key s1 = new Key(BitHelper.getEmbeddedSubject(subject));
			Key p1 = new Key(BitHelper.getEmbeddedPredicate(subject));
			Key o1 = new Key(BitHelper.getEmbeddedObject(subject));
		}
		
		if (BitHelper.isReferenceBitSet(object)) {
			Key s2 = dict.createKey(((Node_Triple)t.getObject()).getSubject());
			Key p2 = dict.createKey(((Node_Triple)t.getObject()).getPredicate());
			Key o2 = dict.createKey(((Node_Triple)t.getObject()).getObject());
		} else if (BitHelper.isIdAnEmbeddedTriple(object)) {
			Key s2 = new Key(BitHelper.getEmbeddedSubject(object));
			Key p2 = new Key(BitHelper.getEmbeddedPredicate(object));
			Key o2 = new Key(BitHelper.getEmbeddedObject(object));
		}
		
		return new ArrayList<TripleStar>();
	}
}

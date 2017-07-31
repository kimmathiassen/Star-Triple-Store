package dk.aau.cs.qweb.triple;

import dk.aau.cs.qweb.dictionary.NodeDictionary;
import dk.aau.cs.qweb.main.Config;
import dk.aau.cs.qweb.node.SimpleNode;

//First bit in key is set if key is an embedded triple.
//Second bit is set if embedded key does not fit in dictionary, otherwise it is part of the id.
public class KeyFactory {
	
	private static final long EMBEDDED_BIT = Long.MIN_VALUE;
	private static final long REFERENCE_BIT =	-Long.parseLong("0100000000000000000000000000000000000000000000000000000000000000", 2);
	private static long elementId = 1l;
	private static long referenceTripleId = 1l;
	//private static final long EMBEDDED_IDENTIFIER_TO_LARGE_BIT = Long.MIN_VALUE >>> 1;

	public static Key createKey(final Key subject,final Key predicate,final Key object) {
	
		return createKey(subject.getId(),predicate.getId(),object.getId());
	}
	
//	public static Key createReferenceTriple(long subject, long predicate, long object) {
//		return createReferenceTriple(subject,predicate,object);
//	}
	
	public static Key createKey(long subject, long predicate, long object) {
		
		if (subject < 0 || 	predicate < 0 || object < 0) {
			throw new IllegalArgumentException("identifier must not be negative, (MSB is set)");
		}
		NodeDictionary dict = NodeDictionary.getInstance();
		
		if (subject > Config.getLargestSubjectId() || 
				predicate > Config.getLargestSubjectId() ||
				object > Config.getLargestSubjectId()) {
			return createReferenceTriple();
		} else if ( dict.isThereAnySpecialReferenceTripleDistributionConditions() ) {
			//If this flag is set we have to check if the keys already exist as an reference key
			Key s = new Key(subject); Key p = new Key(predicate); Key o = new Key(object);
			if (dict.containsReferernceTripleKey(s, p, o)) {
				return dict.getReferernceTripleKey(s, p,o);
			} else {
				return createEmbeddedTriple(subject, predicate, object);
			}
			
		} else {
			return createEmbeddedTriple(subject, predicate, object);
		}
	}

	public static Key createReferenceTriple() {
		Key key = new Key(REFERENCE_BIT + referenceTripleId);
		referenceTripleId++;
		
		return key;
	}

	private static Key createEmbeddedTriple(long subject, long predicate, long object) {
		subject = subject << 40;
		predicate = predicate << 20;
		
		return new Key(EMBEDDED_BIT + subject + predicate + object);
	}

	public static Key createKey(SimpleNode node) {
		Key key = new Key(elementId);
		elementId++;
		return key;
	}

	public static void reset() {
		elementId = 1l;
		referenceTripleId = 1l;
	}

	public static Key createKey(long id) {
		if (id < 0) {
			throw new IllegalArgumentException("ids must not be negative");
		}
		return new Key(id);
	}

//	public static Key createReferenceTriple(Key subject, Key predicate, Key object) {
//		return createReferenceTriple(subject.getId(),predicate.getId(),object.getId());
//	}
}

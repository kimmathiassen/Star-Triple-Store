package dk.aau.cs.qweb.triple;

import dk.aau.cs.qweb.dictionary.NodeDictionary;
import dk.aau.cs.qweb.dictionary.NodeDictionaryFactory;
import dk.aau.cs.qweb.helper.BitHelper;
import dk.aau.cs.qweb.main.Config;
import dk.aau.cs.qweb.node.SimpleNode;

/**
 * Class for creating keys. It will perform a lookup in the appropriate dictionary to ensure 
 * that existing keys receive the same internal id.  
 * In the case of either the subject, predicate or object of an embedded triple
 * is represented using a key that is to large, a reference is created instead.
 * 
 * All keys should be created using this factory. 
 * This class should be used to combine keys into embedded keys.
 * It also create references keys if need be.
 * It also contains counters for creating new keys. It is not thread safe. 
 * However, not that this class is not responsible for adding keys to the dictionary.
 */
public class KeyFactory {
	private static final long EMBEDDED_BIT = Long.MIN_VALUE;
	private static final long REFERENCE_BIT =	-Long.parseLong("0100000000000000000000000000000000000000000000000000000000000000", 2);
	private static long elementId = 1l;
	private static long referenceTripleId = 1l;

	/**
	 * create a new or retrieve an existing embedded triple key
	 * 
	 * @param subject
	 * @param predicate
	 * @param object
	 * @return an existing or new key, depending on if the embedded triple already existed.
	 */
	public static Key createKey(final Key subject,final Key predicate,final Key object) {
	
		return createKey(subject.getId(),predicate.getId(),object.getId());
	}
	
	/**
	 * create a new or retrieve an existing embedded triple key
	 * 
	 * @param subject
	 * @param predicate
	 * @param object
	 * @return an existing or new key, depending on if the embedded triple already existed.
	 */
	public static Key createKey(long subject, long predicate, long object) {
		
		if (subject < 0 || 	predicate < 0 || object < 0) {
			throw new IllegalArgumentException("identifier must not be negative, (Most significant bit (MSB) must be set)");
		}
		NodeDictionary dict = NodeDictionaryFactory.getDictionary();
		
		if (isElementsTooLargeToEncode(subject, predicate, object)) {
			return createReferenceTriple();
		} else if ( dict.isThereAnySpecialReferenceTripleDistributionConditions() ) { 
		/**
		 * For information about this flag, see {@link NodeDictionary}
		 */
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

	private static boolean isElementsTooLargeToEncode(long subject, long predicate, long object) {
		return subject > BitHelper.maxValue(Config.getSubjectSizeInBits()) || 
				predicate > BitHelper.maxValue(Config.getSubjectSizeInBits()) ||
				object > BitHelper.maxValue(Config.getSubjectSizeInBits());
	}

	
	public static Key createReferenceTriple() {
		Key key = new Key(REFERENCE_BIT + referenceTripleId);
		referenceTripleId++;
		
		return key;
	}
	
	private static Key createEmbeddedTriple(long subject, long predicate, long object) {
		long sKey = BitHelper.createEmbeddedSubject(subject);
		long pKey = BitHelper.createEmbeddedPredicate(predicate);
		long oKey = object;
		return new Key(EMBEDDED_BIT | pKey | sKey | oKey);
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
}

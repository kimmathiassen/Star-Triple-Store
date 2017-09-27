package dk.aau.cs.qweb.helper;

import dk.aau.cs.qweb.dictionary.NodeDictionary;
import dk.aau.cs.qweb.main.Config;
import dk.aau.cs.qweb.triple.Key;

/**
 * Helper class for parsing keys.
 */
public class BitHelper {
	private static final long REFERENCE_BIT_MASK =			Long.parseLong("0100000000000000000000000000000000000000000000000000000000000000", 2);
	private static final long REFERENCE_TRIPLE_BODY_MASK =	Long.parseLong("0000111111111111111111111111111111111111111111111111111111111111", 2);
	private static final long REFERENCE_TRIPLE_HEADER = 12;
	private static final long EMBEDDED_TRIPLE_HEADER = 8;

	public static long maxValue(int numberOfBits) {
		return (long)Math.pow(2, numberOfBits)-1;
	}
	
	private static long getSubjectMask() {
		return maxValue(Config.getSubjectSizeInBits()) << (Config.getObjectSizeInBits() + Config.getPredicateSizeInBits());
	}
	
	private static long getPredicateMask() {
		return maxValue(Config.getPredicateSizeInBits()) << Config.getObjectSizeInBits();
	}
	
	private static long getObjectMask() {
		return maxValue(Config.getObjectSizeInBits());
	}
	
	public static long createEmbeddedSubject(long id) {
		return id << (Config.getPredicateSizeInBits() + Config.getObjectSizeInBits());
	}
	
	public static long createEmbeddedPredicate(long id) {
		return id << (Config.getObjectSizeInBits());
	}
	
	public static long createEmbeddedObject(long id) {
		return id;
	}
	
	/**
	 * @param key
	 * @return True iff key is an embedded or reference key. 
	 */
	public static boolean isIdAnEmbeddedTriple(final Key key) {
		return isIdAnEmbeddedTriple(key.getId());
	}
	
	/**
	 * @param inner id of a key
	 * @return True iff key is an embedded or reference key. 
	 */
	public static boolean isIdAnEmbeddedTriple(long id) {
		return id < 0 ? true : false;
	}
	
	/**
	 * Throws IllegalArgumentException if id is not an embedded id.
	 * Does not work on reference keys. Use the {@link NodeDictionary} lookup a reference key.
	 * 
	 * @param Inner id of a key
	 * @return The subject key of the embedded key
	 */
	public static long getEmbeddedSubject(long id) {
		if (!isIdAnEmbeddedTriple(id)) {
			throw new IllegalArgumentException("expected an embedded triple, but recieved: "+String.format("%64s", Long.toBinaryString(id)).replace(' ', '0'));
		}
		return (id & getSubjectMask()) >>> (Config.getPredicateSizeInBits() + Config.getObjectSizeInBits());
	}
	
	/**
	 * Throws IllegalArgumentException if id is not an embedded id.
	 * Does not work on reference keys. Use the {@link NodeDictionary} lookup a reference key.
	 * 
	 * @param Inner id of a key
	 * @return The predicate key of the embedded key
	 */
	public static long getEmbeddedPredicate(long id) {
		if (!isIdAnEmbeddedTriple(id)) {
			throw new IllegalArgumentException("expected an embedded triple, but recieved: "+String.format("%64s", Long.toBinaryString(id)).replace(' ', '0'));
		}
		return (id & getPredicateMask()) >>> Config.getObjectSizeInBits();
	}

	/**
	 * Throws IllegalArgumentException if id is not an embedded id.
	 * Does not work on reference keys. Use the {@link NodeDictionary} lookup a reference key.
	 * 
	 * @param Inner id of a key
	 * @return The object key of the embedded key
	 */
	public static long getEmbeddedObject(long id) {
		if (!isIdAnEmbeddedTriple(id)) {
			throw new IllegalArgumentException("expected an embedded triple, but recieved: "+String.format("%64s", Long.toBinaryString(id)).replace(' ', '0'));
		}
		return id & getObjectMask();
	}
	
	/**
	 * Throws IllegalArgumentException if id is not an embedded id.
	 * Does not work on reference keys. Use the {@link NodeDictionary} lookup a reference key.
	 * 
	 * @param The embedded key
	 * @return The subject key of the embedded key
	 */
	public static long getEmbeddedSubject(final Key key) {
		return getEmbeddedSubject(key.getId());
	}
	
	/**
	 * Throws IllegalArgumentException if id is not an embedded id.
	 * Does not work on reference keys. Use the {@link NodeDictionary} lookup a reference key.
	 * 
	 * @param The embedded key
	 * @return The predicate key of the embedded key
	 */
	public static long getEmbeddedPredicate(final Key key) {
		return getEmbeddedPredicate(key.getId());
	}
	
	/**
	 * Throws IllegalArgumentException if id is not an embedded id.
	 * Does not work on reference keys. Use the {@link NodeDictionary} lookup a reference key.
	 * 
	 * @param The embedded key
	 * @return The object key of the embedded key
	 */
	public static long getEmbeddedObject(final Key key) {
		return getEmbeddedObject(key.getId());
	}

	/**
	 * @param A key
	 * @return True iff the two first bits are set.
	 */
	public static boolean isReferenceBitSet(Key key) {
		return isReferenceBitSet(key.getId());
	}
	
	/**
	 * @param Inner id of a key
	 * @return True iff the two first bits are set.
	 */
	public static boolean isReferenceBitSet(long id) {
		if (isIdAnEmbeddedTriple(id)) {
			if ((id & REFERENCE_BIT_MASK) == REFERENCE_BIT_MASK) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Throw IllegalArgumentException if id is not a embedded id.
	 * 
	 * @param Inner id of a key
	 * @return Return the header (4 most signigicant bits) of an embedded triple
	 */
	public static long getEmbeddedHeader(long id) {
		if (!isIdAnEmbeddedTriple(id)) {
			throw new IllegalArgumentException("expected an embedded triple (or reference), but recieved: "+String.format("%64s", Long.toBinaryString(id)).replace(' ', '0'));
		}
		return isReferenceBitSet(id) ? REFERENCE_TRIPLE_HEADER : EMBEDDED_TRIPLE_HEADER;
	}

	/**
	 * @param Inner id of a key
	 * @return Return the body (60 least significant bits) of reference key
	 */
	public static long getReferenceKeyBody(long id) {
		return (id & REFERENCE_TRIPLE_BODY_MASK );
	}
}

package dk.aau.cs.qweb.helper;

import dk.aau.cs.qweb.dictionary.NodeDictionary;
import dk.aau.cs.qweb.triple.Key;

/**
 * Helper class for parsing keys.
 */
public class BitHelper {
	private static final long OVERFLOWN_MASK =	Long.parseLong("0100000000000000000000000000000000000000000000000000000000000000", 2);
	private static final long OBJECT_MASK = 	Long.parseLong("0000000000000000000000000000000000000000000011111111111111111111", 2);
	private static final long PREDICATE_MASK = 	Long.parseLong("0000000000000000000000001111111111111111111100000000000000000000", 2);
	private static final long SUBJECT_MASK =	Long.parseLong("0000111111111111111111110000000000000000000000000000000000000000", 2);
	private static final long BODY_MASK =		Long.parseLong("0000111111111111111111111111111111111111111111111111111111111111", 2);
	public static final long LARGEST_20_BIT_NUMBER = 1048575;
	private static final long REFERENCE_TRIPLE_HEADER = 12;
	private static final long EMBEDDED_TRIPLE_HEADER = 8;

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
		return (id & SUBJECT_MASK) >>> 40;
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
		return (id & PREDICATE_MASK) >>> 20;
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
		return id & OBJECT_MASK;
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
			if ((id & OVERFLOWN_MASK) == OVERFLOWN_MASK) {
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
		return (id & BODY_MASK );
	}
}

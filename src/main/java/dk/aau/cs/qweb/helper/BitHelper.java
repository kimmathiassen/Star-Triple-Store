package dk.aau.cs.qweb.helper;

import dk.aau.cs.qweb.triple.Key;

public class BitHelper {
	private static final long OVERFLOWN_MASK =	Long.parseLong("0100000000000000000000000000000000000000000000000000000000000000", 2);
	private static final long OBJECT_MASK = 	Long.parseLong("0000000000000000000000000000000000000000000011111111111111111111", 2);
	private static final long PREDICATE_MASK = 	Long.parseLong("0000000000000000000000001111111111111111111100000000000000000000", 2);
	private static final long SUBJECT_MASK =	Long.parseLong("0000111111111111111111110000000000000000000000000000000000000000", 2);

	public static boolean isThereAnyKeysSetToZeroInEmbeddedId(final Key key) {
		return isThereAnyKeysSetToZeroInEmbeddedId(key.getId());
	}
	
	public static boolean isThereAnyKeysSetToZeroInEmbeddedId(long id) {
		if (getEmbeddedSubject(id) == 0) {
			return true;
		}
		if (getEmbeddedPredicate(id) == 0) {
			return true;
		}
		if (getEmbeddedObject(id) == 0) {
			return true;
		}
		return false;
	}

	public static boolean isIdAnEmbeddedTriple(final Key key) {
		return isIdAnEmbeddedTriple(key.getId());
	}
	
	public static boolean isIdAnEmbeddedTriple(long id) {
		return id < 0 ? true : false;
	}
	
	public static long getEmbeddedSubject(long id) {
		if (!isIdAnEmbeddedTriple(id)) {
			throw new IllegalArgumentException("expected an embedded triple, but recieved: "+String.format("%64s", Long.toBinaryString(id)).replace(' ', '0'));
		}
		return (id & SUBJECT_MASK) >>> 40;
	}
	
	public static long getEmbeddedPredicate(long id) {
		if (!isIdAnEmbeddedTriple(id)) {
			throw new IllegalArgumentException("expected an embedded triple, but recieved: "+String.format("%64s", Long.toBinaryString(id)).replace(' ', '0'));
		}
		return (id & PREDICATE_MASK) >>> 20;
	}

	public static long getEmbeddedObject(long id) {
		if (!isIdAnEmbeddedTriple(id)) {
			throw new IllegalArgumentException("expected an embedded triple, but recieved: "+String.format("%64s", Long.toBinaryString(id)).replace(' ', '0'));
		}
		return id & OBJECT_MASK;
	}
	
	public static long getEmbeddedSubject(final Key key) {
		return getEmbeddedSubject(key.getId());
	}
	
	public static long getEmbeddedPredicate(final Key key) {
		return getEmbeddedPredicate(key.getId());
	}
	
	public static long getEmbeddedObject(final Key key) {
		return getEmbeddedObject(key.getId());
	}

	public static boolean isOverflownEmbeddedTriple(Key key) {
		if (isIdAnEmbeddedTriple(key)) {
			if ((key.getId() & OVERFLOWN_MASK) == OVERFLOWN_MASK) {
				return true;
			}
		}
		return false;
	}
}

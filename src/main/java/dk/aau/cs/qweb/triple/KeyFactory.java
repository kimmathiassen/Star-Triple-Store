package dk.aau.cs.qweb.triple;

import org.apache.commons.lang3.NotImplementedException;

//First bit in key is set if key is an embedded triple.
//Second bit is set if embedded key does not fit in dictionary, otherwise it is part of the id.
public class KeyFactory {
	private static final long LARGEST_20_BIT_NUMBER = 1048575;
	private static final long EMBEDDED_BIT = Long.MIN_VALUE;
	//private static final long EMBEDDED_IDENTIFIER_TO_LARGE_BIT = Long.MIN_VALUE >>> 1;

	public static Key createKey(final Key subject,final Key predicate,final Key object) {
	
		return createKey(subject.getId(),predicate.getId(),object.getId());
	}
	
	public static Key createKey(long subject, long predicate, long object) {
		
		if (subject < 0 || 
				predicate < 0 ||
				object < 0) {
		
		
		if (subject > LARGEST_20_BIT_NUMBER || 
				predicate > LARGEST_20_BIT_NUMBER ||
				object > LARGEST_20_BIT_NUMBER) {
			throw new NotImplementedException("add code that handles embedded triples with to large ids");
		}
		
			throw new IllegalArgumentException("identifier must not be negative, (MSB is set)");
		}
		
		//The correct bits are not set, e.g. 10...62*0
		long key = EMBEDDED_BIT;
		
		//Bitshift subject and predicate to their respective places in the long.
		subject = subject << 40;
		predicate = predicate << 20;
		
		//Combine into one long.
		key = key | subject;
		key = key | predicate;
		key = key | object;
		
		return new Key(key);
	}
	
	public static Key createKey(long key) {
		if (key < 0) {
			throw new IllegalArgumentException("identifier must not be negative, (MSB is set)");
		}
		//No bits are set meaning that this is a normal key.
		return new Key((key));
	}

	public static long getOverflowLimit() {
		return LARGEST_20_BIT_NUMBER;
	}
}

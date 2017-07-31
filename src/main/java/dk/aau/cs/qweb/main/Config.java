package dk.aau.cs.qweb.main;

public class Config {

	private static boolean prefixDictionaryState = true;
	private static int subjectSize = 20;
	private static int predicateSize = 20;
	private static int objectSize = 20;
	
	public static void enablePrefixDictionary(boolean flag) {
		prefixDictionaryState = flag;
	}
	
	public static boolean isPrefixDictionaryEnabled() {
		return prefixDictionaryState;
	}

	public static long getLargestSubjectId() {
		return (long) Math.pow(2, subjectSize);
	}
	
	public static long getLargestPredicateId() {
		return (long) Math.pow(2, predicateSize);
	}
	
	public static long getLargestObjectId() {
		return (long) Math.pow(2, objectSize);
	}
}

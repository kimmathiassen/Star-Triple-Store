package dk.aau.cs.qweb.main;

public class Config {

	private static boolean prefixDictionaryState = true;
	private static int subjectSizeInBits = 20;
	private static int predicateSizeInBits = 20;
	private static int objectSizeInBits = 20;
	private static int nodeDictionaryInitialSize = 10000;
	private static int nodeReferenceDictionaryInitialSize = 10000;
	private static String dictionaryType = "DiskBTree";
	private static String indexType = "hashindex";
	
	public static void enablePrefixDictionary(boolean flag) {
		prefixDictionaryState = flag;
	}
	
	public static boolean isPrefixDictionaryEnabled() {
		return prefixDictionaryState;
	}

	public static long getLargestSubjectId() {
		return (long) Math.pow(2, subjectSizeInBits);
	}
	
	public static long getLargestPredicateId() {
		return (long) Math.pow(2, predicateSizeInBits);
	}
	
	public static long getLargestObjectId() {
		return (long) Math.pow(2, objectSizeInBits);
	}

	public static int getNodeDictionaryInitialSize() {
		return nodeDictionaryInitialSize;
	}

	public static int getReferenceNodeDictionaryInitialSize() {
		return nodeReferenceDictionaryInitialSize;
	}
	
	public static boolean ignoreFilePrefixInQueries() {
		return true;
	}

	public static String getDictionaryType() {
		return dictionaryType ;
	}
	
	public static void setDictionaryType(String value) {
		dictionaryType = value;
	}

	public static String getIndex() {
		return indexType;
	}
	
	public static void setIndex(String name) {
		indexType = name;
	}
}

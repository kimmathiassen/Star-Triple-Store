package dk.aau.cs.qweb.main;

public class Config {

	private static boolean prefixDictionaryState = true;
	private static int subjectSizeInBits = 20;
	private static int predicateSizeInBits = 10;
	private static int objectSizeInBits = 32;
	private static int nodeDictionaryInitialSize = 10000;
	private static int nodeReferenceDictionaryInitialSize = 10000;
	//private static String dictionaryType = "HybridBTree";
	private static String dictionaryType = "InMemoryHashMap";
	private static String indexType = "hashindex";
	private static String filename;
	
	public static void enablePrefixDictionary(boolean flag) {
		prefixDictionaryState = flag;
	}
	
	public static boolean isPrefixDictionaryEnabled() {
		return prefixDictionaryState;
	}

	public static void setSubjectSizeInBits(int size) {
		subjectSizeInBits = size;
	}
	
	public static void setPredicateSizeInBits(int size) {
		predicateSizeInBits =  size;
	}
	
	public static void setObjectSizeInBits(int size) {
		objectSizeInBits = size;
	}
	
	public static int getEmbeddedHeaderSize() {
		return 64-(subjectSizeInBits+predicateSizeInBits+objectSizeInBits);
	}
	
	public static int getSubjectSizeInBits() {
		return subjectSizeInBits;
	}
	
	public static int getPredicateSizeInBits() {
		return predicateSizeInBits;
	}
	
	public static int getObjectSizeInBits() {
		return objectSizeInBits;
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

	public static void setLocation(String filename) {
		Config.filename = filename;
	}
	
	public static String getLocation() {
		return filename;
	}

	public static String getLocationFileName() {
		String[] split = filename.split("/");
		return split[split.length];
	}

	
}

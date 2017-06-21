package dk.aau.cs.qweb.main;

public class Config {

	private static boolean prefixDictionaryState = true;
	
	public static void enablePrefixDictionary(boolean flag) {
		prefixDictionaryState = flag;
	}
	
	public static boolean isPrefixDictionaryEnabled() {
		return prefixDictionaryState;
	}
}

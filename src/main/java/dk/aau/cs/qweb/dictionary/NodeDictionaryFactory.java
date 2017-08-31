package dk.aau.cs.qweb.dictionary;

import dk.aau.cs.qweb.main.Config;

public class NodeDictionaryFactory {

	static public NodeDictionary getDictionary() {
		if (Config.getDictionaryType().equals("InMemoryHashMap")) {
			return HashNodeHybridDictionary.getInstance();
		} else if  (Config.getDictionaryType().equals("DiskBTree")) {
			return BTreePhysicalDictionary.getInstance(); 
		} else {
			throw new IllegalArgumentException("unknown dictionary datastructure "+Config.getDictionaryType());
		}
	}
}

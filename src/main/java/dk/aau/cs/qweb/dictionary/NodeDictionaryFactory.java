package dk.aau.cs.qweb.dictionary;

import dk.aau.cs.qweb.main.Config;

public class NodeDictionaryFactory {

	static public NodeDictionary getDictionary() {
		switch (Config.getDictionaryType()) {
		case "InMemoryHashMap":
			return HashNodeDictionary.getInstance();

		default:
			return HashNodeDictionary.getInstance();
		}
	}
}

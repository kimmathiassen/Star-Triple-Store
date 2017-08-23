package dk.aau.cs.qweb.triplestore;

import dk.aau.cs.qweb.main.Config;
import dk.aau.cs.qweb.triplestore.flatindex.FlatIndex;
import dk.aau.cs.qweb.triplestore.hashindex.HashIndex;
import dk.aau.cs.qweb.triplestore.hashindex.MapIndex.Field;
import dk.aau.cs.qweb.triplestore.treeindex.TreeIndex;

public class IndexFactory {
	
	private IndexFactory() {}
	
	public static Index getIndex(Field f1, Field f2, Field f3) {
		if (Config.getIndex().equals("hashindex")) {
			return new HashIndex(f1,f2,f3);
		} else if (Config.getIndex().equals("flatindex")) {
			return new FlatIndex(f1,f2,f3);
		} else if (Config.getIndex().equals("treeindex")) {
			return new TreeIndex(f1,f2,f3);
		} else {
			throw new IllegalArgumentException("unknown index type "+Config.getIndex());
		}
	}
}

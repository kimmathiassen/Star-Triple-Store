package dk.aau.cs.qweb.triplestore.treeindex;

import java.util.ArrayList;
import java.util.TreeMap;

import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triplestore.KeyContainer;
import dk.aau.cs.qweb.triplestore.hashindex.MapTripleBunch;

public class TreeTripleBunch  extends MapTripleBunch {
	
	public TreeTripleBunch() {
		innerMap = new TreeMap<Key,ArrayList<KeyContainer>>();
	}
}

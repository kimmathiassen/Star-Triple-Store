package dk.aau.cs.qweb.triplestore;

import java.util.ArrayList;
import java.util.TreeMap;

import dk.aau.cs.qweb.triple.Key;

public class TreeTripleBunch  extends MapTripleBunch {
	
	public TreeTripleBunch() {
		innerMap = new TreeMap<Key,ArrayList<KeyContainer>>();
	}
}

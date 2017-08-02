package dk.aau.cs.qweb.triplestore;

import java.util.ArrayList;
import java.util.HashMap;

import dk.aau.cs.qweb.triple.Key;

public class HashTripleBunch  extends MapTripleBunch {
	public HashTripleBunch() {
		innerMap = new HashMap<Key,ArrayList<KeyContainer>>();
	}
}

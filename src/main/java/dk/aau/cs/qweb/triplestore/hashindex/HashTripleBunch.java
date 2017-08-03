package dk.aau.cs.qweb.triplestore.hashindex;

import java.util.ArrayList;
import java.util.HashMap;

import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triplestore.KeyContainer;

public class HashTripleBunch  extends MapTripleBunch {
	public HashTripleBunch() {
		innerMap = new HashMap<Key,ArrayList<KeyContainer>>();
	}
}

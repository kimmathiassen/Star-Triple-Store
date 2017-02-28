package dk.aau.cs.qweb.transform;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.graph.Triple;

public class SelectivityMap {
	private static Map<Integer,Integer> score = createMap();
	
	private static Map<Integer, Integer> createMap() {
        Map<Integer, Integer> result = new HashMap<Integer, Integer>();
        result.put(7, 8); //(s,p,o)
        result.put(5, 7); //(s,?,o)
        result.put(6, 6); //(?,p,o)
        result.put(3, 5); //(s,p,?)
        result.put(4, 4); //(?,?,o)
        result.put(1, 3); //(s,?,?)
        result.put(2, 2); //(?,p,?)
        result.put(0, 1); //(?,?,?)
        return result;
    }

	public static int getSelectivityScore(Triple triple) {
		int key = 0;
		if (triple.getSubject().isConcrete()) {
			key += 1;
		}
		if (triple.getPredicate().isConcrete()) {
			key += 2;
		}
		if (triple.getObject().isConcrete()) {
			key += 4;
		}
		
		return score.get(key);
	}
}

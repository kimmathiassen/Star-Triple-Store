package dk.aau.cs.qweb.transform;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.Var;

/**
 * Class that define the triple pattern selectivity heuristics
 * High selectivity is good.
 */
public class SelectivityMap {
	private static Map<Integer,Integer> score = createMap();
	
	private static Map<Integer, Integer> createMap() {
        Map<Integer, Integer> result = new HashMap<Integer, Integer>();
        result.put(7, 16); //(s,p,o)
        result.put(5, 14); //(s,?,o)
        result.put(6, 12); //(?,p,o)
        result.put(3, 10); //(s,p,?)
        result.put(4, 8); //(?,?,o)
        result.put(1, 6); //(s,?,?)
        result.put(2, 4); //(?,p,?)
        result.put(0, 2); //(?,?,?)
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
	
	
	
	public static int getHighestSelectivity() {
		return 17;
	}

	public static int getSelectivityScore(Triple triple, Var bindVariable) {
		return getSelectivityScore(triple)+1;
	}
}

package dk.aau.cs.qweb.triplestore;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dk.aau.cs.qweb.dictionary.NodeDictionaryFactory;
import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triple.KeyFactory;
import dk.aau.cs.qweb.triple.TripleStar;
import dk.aau.cs.qweb.triplepattern.TripleStarPattern;
import dk.aau.cs.qweb.triplepattern.Variable;
import dk.aau.cs.qweb.triplestore.hashindex.HashIndex;
import dk.aau.cs.qweb.triplestore.hashindex.MapIndex.Field;

public class IndexEmbeddedTripleTest {
	static Index SPO;
	static Index POS;
	static Index OSP;
	static KeyFactory kf;
	static Variable var;
	
	@Before
	public void setup(){
		NodeDictionaryFactory.getDictionary().open();
	}
	
	@After
	public void tearDown(){
		NodeDictionaryFactory.getDictionary().clear();
		NodeDictionaryFactory.getDictionary().close();
	}
	
	
	@BeforeClass
    public static void runOnceBeforeClass() {
    	SPO = new HashIndex(Field.S,Field.P,Field.O);
    	POS = new HashIndex(Field.P,Field.O,Field.S);
    	OSP = new HashIndex(Field.P,Field.O,Field.S);
    	
    	var = new Variable(1);
    }

    @AfterClass
    public static void runOnceAfterClass() {
    	SPO.clear();
    }
	
	@Test
	public void lookupEmbeddedTripleSubjectPositionVariable() {
		
		Key embeddedTriple1 = KeyFactory.createKey(1, 2, 3);
		TripleStar t1 = new TripleStar(embeddedTriple1,KeyFactory.createKey(4),KeyFactory.createKey(5));
		POS.add(t1);
		
		TripleStarPattern triplePattern = new TripleStarPattern(var,KeyFactory.createKey(4),KeyFactory.createKey(5));
		Iterator<KeyContainer> integer = POS.iterator(triplePattern);
		int count = 0;
		
		while (integer.hasNext()) {
			integer.next();
			count++;
		}
		
		assertEquals(count,1);
		POS.clear();
	}
	
	@Test
	public void lookupEmbeddedTripleObjectPositionVariable() {
		
		Key embeddedTriple1 = KeyFactory.createKey(1, 2, 3);
		TripleStar t1 = new TripleStar(KeyFactory.createKey(5),KeyFactory.createKey(4),embeddedTriple1);
		SPO.add(t1);
		
		TripleStarPattern triplePattern = new TripleStarPattern(KeyFactory.createKey(5),KeyFactory.createKey(4),var);
		Iterator<KeyContainer> integer = SPO.iterator(triplePattern);
		int count = 0;
		
		while (integer.hasNext()) {
			integer.next();
			count++;
		}
		
		assertEquals(count,1);
		SPO.clear();
	}
	
	@Test
	public void lookupEmbeddedTripleSubjectObjectPositionVariable() {
		
		Key embeddedTriple1 = KeyFactory.createKey(1, 2, 3);
		Key embeddedTriple2 = KeyFactory.createKey(4, 5, 6);
		TripleStar t1 = new TripleStar(embeddedTriple2,KeyFactory.createKey(7),embeddedTriple1);
		OSP.add(t1);
		
		TripleStarPattern triplePattern = new TripleStarPattern(var,KeyFactory.createKey(7),var);
		Iterator<KeyContainer> integer = OSP.iterator(triplePattern);
		int count = 0;
		
		while (integer.hasNext()) {
			integer.next();
			count++;
		}
		
		assertEquals(count,1);
		OSP.clear();
	}
	
	@Test
	public void lookupEmbeddedTripleObjectWithMixedData() {
		Key embeddedTriple1 = KeyFactory.createKey(1, 2, 3);
		TripleStar t1 = new TripleStar(KeyFactory.createKey(4),KeyFactory.createKey(5),embeddedTriple1);
		TripleStar t2 = new TripleStar(KeyFactory.createKey(4),KeyFactory.createKey(6),KeyFactory.createKey(7));
		TripleStar t3 = new TripleStar(KeyFactory.createKey(4),KeyFactory.createKey(5),KeyFactory.createKey(8));
		TripleStar t4 = new TripleStar(KeyFactory.createKey(1),KeyFactory.createKey(2),KeyFactory.createKey(3));
		
		SPO.add(t1);
		SPO.add(t2);
		SPO.add(t3);
		SPO.add(t4);
		
		TripleStarPattern triplePattern = new TripleStarPattern(KeyFactory.createKey(4),KeyFactory.createKey(5),var);
		Iterator<KeyContainer> integer = SPO.iterator(triplePattern);
		int count = 0;
		
		while (integer.hasNext()) {
			integer.next();
			count++;
		}
		
		assertEquals(count,2);
		SPO.clear();
	}
}

package dk.aau.cs.qweb;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triple.KeyFactory;
import dk.aau.cs.qweb.triple.TriplePattern;
import dk.aau.cs.qweb.triple.TriplePattern.Variable;
import dk.aau.cs.qweb.triple.TripleStar;
import dk.aau.cs.qweb.triplestore.Index;
import dk.aau.cs.qweb.triplestore.Index.Field;

public class IndexEmbeddedTripleTest {
	static Index SPO;
	static Index POS;
	static Index OSP;
	static KeyFactory kf;
	
	
	@BeforeClass
    public static void runOnceBeforeClass() {
    	SPO = new Index(Field.S,Field.P,Field.O);
    	POS = new Index(Field.P,Field.O,Field.S);
    	OSP = new Index(Field.P,Field.O,Field.S);
    	
//    	KeyFactory kf = new KeyFactory();
//		Key embeddedTriple1 = kf.createKey(1, 2, 3);
//		Key embeddedTriple2 = kf.createKey(4, 5, 6);
    	
//    	IdTriple t1 = new IdTriple(embeddedTriple1,new Key(2),new Key(3));
//    	IdTriple t2 = new IdTriple(kf.createKey(1),new Key(2),embeddedTriple2);
//    	IdTriple t3 = new IdTriple(new Key(1),new Key(2),new Key(3));
//    	IdTriple t4 = new IdTriple(new Key(4),new Key(5),new Key(6));
//    	IdTriple t5 = new IdTriple(embeddedTriple1,new Key(2),embeddedTriple2);
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
		
		TriplePattern triplePattern = new TriplePattern(Variable.ANY,KeyFactory.createKey(4),KeyFactory.createKey(5));
		Iterator<TripleStar> integer = POS.iterator(triplePattern);
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
		
		TriplePattern triplePattern = new TriplePattern(KeyFactory.createKey(5),KeyFactory.createKey(4),Variable.ANY);
		Iterator<TripleStar> integer = SPO.iterator(triplePattern);
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
		
		TriplePattern triplePattern = new TriplePattern(Variable.ANY,KeyFactory.createKey(7),Variable.ANY);
		Iterator<TripleStar> integer = OSP.iterator(triplePattern);
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
		
		TriplePattern triplePattern = new TriplePattern(KeyFactory.createKey(4),KeyFactory.createKey(5),Variable.ANY);
		Iterator<TripleStar> integer = SPO.iterator(triplePattern);
		int count = 0;
		
		while (integer.hasNext()) {
			integer.next();
			count++;
		}
		
		assertEquals(count,2);
		SPO.clear();
	}

}

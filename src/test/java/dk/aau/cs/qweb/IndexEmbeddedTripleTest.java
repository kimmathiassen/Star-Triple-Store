package dk.aau.cs.qweb;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import dk.aau.cs.qweb.triple.IdTriple;
import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triple.KeyFactory;
import dk.aau.cs.qweb.triple.IdTriple.Variable;
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
    	kf = new KeyFactory();
    	
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
		
		Key embeddedTriple1 = kf.createKey(1, 2, 3);
		IdTriple t1 = new IdTriple(embeddedTriple1,kf.createKey(4),kf.createKey(5));
		POS.add(t1);
		
		IdTriple triplePattern = new IdTriple(Variable.ANY,kf.createKey(4),kf.createKey(5));
		Iterator<IdTriple> integer = POS.iterator(triplePattern);
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
		
		Key embeddedTriple1 = kf.createKey(1, 2, 3);
		IdTriple t1 = new IdTriple(kf.createKey(5),kf.createKey(4),embeddedTriple1);
		SPO.add(t1);
		
		IdTriple triplePattern = new IdTriple(kf.createKey(5),kf.createKey(4),Variable.ANY);
		Iterator<IdTriple> integer = SPO.iterator(triplePattern);
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
		
		Key embeddedTriple1 = kf.createKey(1, 2, 3);
		Key embeddedTriple2 = kf.createKey(4, 5, 6);
		IdTriple t1 = new IdTriple(embeddedTriple2,kf.createKey(7),embeddedTriple1);
		OSP.add(t1);
		
		IdTriple triplePattern = new IdTriple(Variable.ANY,kf.createKey(7),Variable.ANY);
		Iterator<IdTriple> integer = OSP.iterator(triplePattern);
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
		
		Key embeddedTriple1 = kf.createKey(1, 2, 3);
		IdTriple t1 = new IdTriple(kf.createKey(4),kf.createKey(5),embeddedTriple1);
		IdTriple t2 = new IdTriple(kf.createKey(4),kf.createKey(6),kf.createKey(7));
		IdTriple t3 = new IdTriple(kf.createKey(4),kf.createKey(5),kf.createKey(8));
		IdTriple t4 = new IdTriple(kf.createKey(1),kf.createKey(2),kf.createKey(3));
		
		SPO.add(t1);
		SPO.add(t2);
		SPO.add(t3);
		SPO.add(t4);
		
		IdTriple triplePattern = new IdTriple(kf.createKey(4),kf.createKey(5),Variable.ANY);
		Iterator<IdTriple> integer = SPO.iterator(triplePattern);
		int count = 0;
		
		while (integer.hasNext()) {
			integer.next();
			count++;
		}
		
		assertEquals(count,2);
		SPO.clear();
	}

}

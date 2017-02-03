package dk.aau.cs.qweb;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triple.TriplePattern;
import dk.aau.cs.qweb.triple.TriplePattern.Variable;
import dk.aau.cs.qweb.triple.TripleStar;
import dk.aau.cs.qweb.triplestore.Index;
import dk.aau.cs.qweb.triplestore.Index.Field;

public class IndexNormalTripleTest {
	static Index SPO;
	static Index POS;
	static Index OSP;

    @BeforeClass
    public static void runOnceBeforeClass() {
    	
    	
    	SPO = new Index(Field.S,Field.P,Field.O);
    	POS = new Index(Field.P,Field.O,Field.S);
    	OSP = new Index(Field.O,Field.S,Field.P);
    	
    	TripleStar t1 = new TripleStar(new Key(1),new Key(2),new Key(3));
    	TripleStar t2 = new TripleStar(new Key(1),new Key(2),new Key(4));
    	TripleStar t3 = new TripleStar(new Key(8),new Key(2),new Key(5));
    	TripleStar t4 = new TripleStar(new Key(3),new Key(6),new Key(7));
    	TripleStar t5 = new TripleStar(new Key(4),new Key(2),new Key(3));
    	
		SPO.add(t1);
		SPO.add(t2);
		SPO.add(t3);
		SPO.add(t4);
		SPO.add(t5);
		
		POS.add(t1);
		POS.add(t2);
		POS.add(t3);
		POS.add(t4);
		POS.add(t5);
		
		OSP.add(t1);
		OSP.add(t2);
		OSP.add(t3);
		OSP.add(t4);
		OSP.add(t5);
    }

    @AfterClass
    public static void runOnceAfterClass() {
    	SPO.clear();
    	POS.clear();
    	OSP.clear();
    }
	
	@Test
	public void subjectLookup() 
	{
		TriplePattern triplePattern = new TriplePattern(new Key(1),Variable.ANY,Variable.ANY);
		Iterator<TripleStar> spoIterator = SPO.iterator(triplePattern);
		int count = 0;
		
		while (spoIterator.hasNext()) {
			spoIterator.next();
			count++;
		}
		
		assertEquals(2,count);
	}
	
	@Test
	public void subjectPredicateLookup() 
	{
		TriplePattern triplePattern = new TriplePattern(new Key(1),new Key(2),Variable.ANY);
		Iterator<TripleStar> spoIterator = SPO.iterator(triplePattern);
		int count = 0;
		
		while (spoIterator.hasNext()) {
			spoIterator.next();
			count++;
		}
		
		assertEquals(2,count);
	}
	
	@Test
	public void predicateLookup() 
	{
		TriplePattern triplePattern = new TriplePattern(Variable.ANY,new Key(2),Variable.ANY);
		Iterator<TripleStar> posIterator = POS.iterator(triplePattern);
		int count = 0;
		
		while (posIterator.hasNext()) {
			posIterator.next();
			count++;
		}
		
		assertEquals(4,count);
	}
	
	@Test
	public void predicateObjectLookup() 
	{
		TriplePattern triplePattern = new TriplePattern(Variable.ANY,new Key(2),new Key(3));
		Iterator<TripleStar> posIterator = POS.iterator(triplePattern);
		int count = 0;
		
		while (posIterator.hasNext()) {
			posIterator.next();
			count++;
		}
		
		assertEquals(2,count);
	}
	
	@Test
	public void objectLookup() 
	{
		TriplePattern triplePattern = new TriplePattern(Variable.ANY,Variable.ANY,new Key(7));
		Iterator<TripleStar> ospIterator = OSP.iterator(triplePattern);
		int count = 0;
		
		while (ospIterator.hasNext()) {
			ospIterator.next();
			count++;
		}
		
		assertEquals(1,count);
	}
	
	@Test
	public void objectSubjectLookup() 
	{
		TriplePattern triplePattern = new TriplePattern(new Key(8),Variable.ANY,new Key(5));
		Iterator<TripleStar> ospIterator = OSP.iterator(triplePattern);
		int count = 0;
		
		while (ospIterator.hasNext()) {
			ospIterator.next();
			count++;
		}
		
		assertEquals(1,count);
	}
	
	@Test
	public void objectLookupNoMatches() 
	{
		TriplePattern triplePattern = new TriplePattern(Variable.ANY,Variable.ANY,new Key(6));
		Iterator<TripleStar> ospIterator = OSP.iterator(triplePattern);
		int count = 0;
		
		while (ospIterator.hasNext()) {
			ospIterator.next();
			count++;
		}
		
		assertEquals(0,count);
	}
	
	@Test
	public void objectSubjectLookupNoMatches() 
	{
		TriplePattern triplePattern = new TriplePattern(new Key(8),Variable.ANY,new Key(6));
		Iterator<TripleStar> ospIterator = OSP.iterator(triplePattern);
		int count = 0;
		
		while (ospIterator.hasNext()) {
			ospIterator.next();
			count++;
		}
		
		assertEquals(0,count);
	}
	
	@Test
	public void doubeInsertOfIdenticalTriples() 
	{
		Index test = new Index(Field.S,Field.P,Field.O);
		TripleStar t1 = new TripleStar(new Key(1),new Key(2),new Key(3));
		TripleStar t2 = new TripleStar(new Key(1),new Key(2),new Key(3));
		test.add(t1);
		test.add(t2);
		
		TriplePattern triplePattern = new TriplePattern(new Key(1),Variable.ANY,Variable.ANY);
		Iterator<TripleStar> iterator = test.iterator(triplePattern);
		int count = 0;
		
		while (iterator.hasNext()) {
			System.out.println(iterator.next());
			count++;
		}
		
		assertEquals(1,count);
	}
	
	
}

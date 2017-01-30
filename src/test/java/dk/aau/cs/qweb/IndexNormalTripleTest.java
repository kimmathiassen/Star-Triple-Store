package dk.aau.cs.qweb;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import dk.aau.cs.qweb.triple.IdTriple;
import dk.aau.cs.qweb.triple.IdTriple.Variable;
import dk.aau.cs.qweb.triple.Key;
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
    	
    	IdTriple t1 = new IdTriple(new Key(1),new Key(2),new Key(3));
    	IdTriple t2 = new IdTriple(new Key(1),new Key(2),new Key(4));
    	IdTriple t3 = new IdTriple(new Key(8),new Key(2),new Key(5));
    	IdTriple t4 = new IdTriple(new Key(3),new Key(6),new Key(7));
    	IdTriple t5 = new IdTriple(new Key(4),new Key(2),new Key(3));
    	
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
		IdTriple triplePattern = new IdTriple(new Key(1),Variable.ANY,Variable.ANY);
		Iterator<IdTriple> spoIterator = SPO.iterator(triplePattern);
		int count = 0;
		
		while (spoIterator.hasNext()) {
			spoIterator.next();
			count++;
		}
		
		assertEquals(count,2);
	}
	
	@Test
	public void subjectPredicateLookup() 
	{
		IdTriple triplePattern = new IdTriple(new Key(1),new Key(2),Variable.ANY);
		Iterator<IdTriple> spoIterator = SPO.iterator(triplePattern);
		int count = 0;
		
		while (spoIterator.hasNext()) {
			spoIterator.next();
			count++;
		}
		
		assertEquals(count,2);
	}
	
	@Test
	public void predicateLookup() 
	{
		IdTriple triplePattern = new IdTriple(Variable.ANY,new Key(2),Variable.ANY);
		Iterator<IdTriple> posIterator = POS.iterator(triplePattern);
		int count = 0;
		
		while (posIterator.hasNext()) {
			posIterator.next();
			count++;
		}
		
		assertEquals(count,4);
	}
	
	@Test
	public void predicateObjectLookup() 
	{
		IdTriple triplePattern = new IdTriple(Variable.ANY,new Key(2),new Key(3));
		Iterator<IdTriple> posIterator = POS.iterator(triplePattern);
		int count = 0;
		
		while (posIterator.hasNext()) {
			posIterator.next();
			count++;
		}
		
		assertEquals(count,2);
	}
	
	@Test
	public void objectLookup() 
	{
		IdTriple triplePattern = new IdTriple(Variable.ANY,Variable.ANY,new Key(7));
		Iterator<IdTriple> ospIterator = OSP.iterator(triplePattern);
		int count = 0;
		
		while (ospIterator.hasNext()) {
			ospIterator.next();
			count++;
		}
		
		assertEquals(count,1);
	}
	
	@Test
	public void objectSubjectLookup() 
	{
		IdTriple triplePattern = new IdTriple(new Key(8),Variable.ANY,new Key(5));
		Iterator<IdTriple> ospIterator = OSP.iterator(triplePattern);
		int count = 0;
		
		while (ospIterator.hasNext()) {
			ospIterator.next();
			count++;
		}
		
		assertEquals(count,1);
	}
	
	@Test
	public void objectLookupNoMatches() 
	{
		IdTriple triplePattern = new IdTriple(Variable.ANY,Variable.ANY,new Key(6));
		Iterator<IdTriple> ospIterator = OSP.iterator(triplePattern);
		int count = 0;
		
		while (ospIterator.hasNext()) {
			ospIterator.next();
			count++;
		}
		
		assertEquals(count,0);
	}
	
	@Test
	public void objectSubjectLookupNoMatches() 
	{
		IdTriple triplePattern = new IdTriple(new Key(8),Variable.ANY,new Key(6));
		Iterator<IdTriple> ospIterator = OSP.iterator(triplePattern);
		int count = 0;
		
		while (ospIterator.hasNext()) {
			ospIterator.next();
			count++;
		}
		
		assertEquals(count,0);
	}
	
	@Test
	public void doubeInsertOfIdenticalTriples() 
	{
		Index test = new Index(Field.S,Field.P,Field.O);
		IdTriple t1 = new IdTriple(new Key(1),new Key(2),new Key(3));
		IdTriple t2 = new IdTriple(new Key(1),new Key(2),new Key(3));
		test.add(t1);
		test.add(t2);
		
		IdTriple triplePattern = new IdTriple(new Key(1),Variable.ANY,Variable.ANY);
		Iterator<IdTriple> iterator = test.iterator(triplePattern);
		int count = 0;
		
		while (iterator.hasNext()) {
			System.out.println(iterator.next());
			count++;
		}
		
		assertEquals(count,1);
	}
	
	
}

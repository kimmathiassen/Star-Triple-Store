package dk.aau.cs.qweb.triplestore;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triple.KeyFactory;

public class KeyFactoryTest {
	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void normalKeyMaxNegativLong() {
		exception.expect(IllegalArgumentException.class);
		KeyFactory.createKey(Long.MIN_VALUE);
	}
	
	@Test
	public void normalKeyNegativLong() {
		exception.expect(IllegalArgumentException.class);
		KeyFactory.createKey(-15);
	}
	
	@Test
	public void normalKeyMaxLong() {
		Key key = KeyFactory.createKey(Long.MAX_VALUE);
		
		assertEquals(Long.MAX_VALUE, key.getId());
	}
	
	@Test
	public void normalKey() {
		
		Key key = KeyFactory.createKey(56);
		
		assertEquals(56, key.getId());
	}
	
	@Test
	public void embeddedKeyNegativLongOneParameter() {
		
		exception.expect(IllegalArgumentException.class);
		KeyFactory.createKey(-45,65,148451);
	}
	
	@Test
	public void embeddedKeyNegativLongThreeParameters() {
		exception.expect(IllegalArgumentException.class);
		KeyFactory.createKey(-44,-1,Long.MIN_VALUE);
	}
	
//	@Test
//	public void embeddedKeyMaxLongOneParameter() {
//		long twoFirstBitsAreSet = Long.MIN_VALUE >> 1;
//		
//		Key key = KeyFactory.createKey(4566,1531,1048576);
//		long twoFirstBitsOfKeyId = key.getId() & twoFirstBitsAreSet; 
//		
//		//Here we assert that the two first bits should be set.
//		assertEquals(twoFirstBitsAreSet, twoFirstBitsOfKeyId);
//	}
	
	@Test
	public void embeddedKeyMaxLongThreeParameter() {
		Key key = KeyFactory.createKey(1048675,200048576,Long.MAX_VALUE);
		
		long twoFirstBitsAreSet = Long.MIN_VALUE >> 1;
		long twoFirstBitsOfKeyId = key.getId() & twoFirstBitsAreSet; 
		
		//Here we assert that the two first bits should be set.
		assertEquals(twoFirstBitsAreSet, twoFirstBitsOfKeyId);
	}
	
//	@Test
//	public void embeddedKeyMaxLongNegativeLongNormalLongParameters() {
//		
//		exception.expect(IllegalArgumentException.class);
//		KeyFactory.createKey(454,Long.MIN_VALUE,Long.MAX_VALUE);
//	}
	
	@Test
	public void embeddedKey() {
		
		Key key = KeyFactory.createKey(1023,511,255);
		long id = new BigInteger("1000000000000011111111110000000000011111111100000000000011111111", 2).longValue();
		
		assertEquals(id, key.getId());
	}

}

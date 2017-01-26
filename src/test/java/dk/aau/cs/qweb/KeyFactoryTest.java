package dk.aau.cs.qweb;

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
		KeyFactory kf = new KeyFactory();
		
		exception.expect(IllegalArgumentException.class);
		kf.createKey(Long.MIN_VALUE);
	}
	
	@Test
	public void normalKeyNegativLong() {
		KeyFactory kf = new KeyFactory();
		
		exception.expect(IllegalArgumentException.class);
		kf.createKey(-15);
	}
	
	@Test
	public void normalKeyMaxLong() {
		KeyFactory kf = new KeyFactory();
		
		Key key = kf.createKey(Long.MAX_VALUE);
		
		assertEquals(Long.MAX_VALUE, key.getId());
	}
	
	@Test
	public void normalKey() {
		KeyFactory kf = new KeyFactory();
		
		Key key = kf.createKey(56);
		
		assertEquals(56, key.getId());
	}
	
	@Test
	public void embeddedKeyNegativLongOneParameter() {
		KeyFactory kf = new KeyFactory();
		
		exception.expect(IllegalArgumentException.class);
		kf.createKey(-45,65,148451);
	}
	
	@Test
	public void embeddedKeyNegativLongThreeParameters() {
		KeyFactory kf = new KeyFactory();
		
		exception.expect(IllegalArgumentException.class);
		kf.createKey(-44,-1,Long.MIN_VALUE);
	}
	
	@Test
	public void embeddedKeyMaxLongOneParameter() {
		KeyFactory kf = new KeyFactory();
		
		long twoFirstBitsAreSet = Long.MIN_VALUE >> 1;
		
		Key key = kf.createKey(4566,1531,1048576);
		long twoFirstBitsOfKeyId = key.getId() & twoFirstBitsAreSet; 
		
		//Here we assert that the two first bits should be set.
		assertEquals(twoFirstBitsAreSet, twoFirstBitsOfKeyId);
	}
	
	@Test
	public void embeddedKeyMaxLongThreeParameter() {
		KeyFactory kf = new KeyFactory();
		
		Key key = kf.createKey(1048675,200048576,Long.MAX_VALUE);
		
		long twoFirstBitsAreSet = Long.MIN_VALUE >> 1;
		long twoFirstBitsOfKeyId = key.getId() & twoFirstBitsAreSet; 
		
		//Here we assert that the two first bits should be set.
		assertEquals(twoFirstBitsAreSet, twoFirstBitsOfKeyId);
	}
	
	@Test
	public void embeddedKeyMaxLongNegativeLongNormalLongParameters() {
		KeyFactory kf = new KeyFactory();
		
		exception.expect(IllegalArgumentException.class);
		kf.createKey(454,Long.MIN_VALUE,Long.MAX_VALUE);
	}
	
	@Test
	public void embeddedKey() {
		KeyFactory kf = new KeyFactory();
		
		Key key = kf.createKey(1023,511,255);
		long id = new BigInteger("1000000000000011111111110000000000011111111100000000000011111111", 2).longValue();
		
		assertEquals(id, key.getId());
	}

}

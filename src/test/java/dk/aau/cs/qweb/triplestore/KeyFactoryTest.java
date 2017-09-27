package dk.aau.cs.qweb.triplestore;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import dk.aau.cs.qweb.dictionary.NodeDictionaryFactory;
import dk.aau.cs.qweb.main.App;
import dk.aau.cs.qweb.main.Config;
import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triple.KeyFactory;

public class KeyFactoryTest {
	
	@Before 
	public void setup() {
		NodeDictionaryFactory.getDictionary().open();
	}
	
	@After 
	public void tearDown() throws IOException {
		NodeDictionaryFactory.getDictionary().clear();
		NodeDictionaryFactory.getDictionary().close();
	}
	
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
	
	@Test
	public void embeddedKeyMaxLongThreeParameter() {
		Key key = KeyFactory.createKey(1048675,200048576,Long.MAX_VALUE);
		
		long twoFirstBitsAreSet = Long.MIN_VALUE >> 1;
		long twoFirstBitsOfKeyId = key.getId() & twoFirstBitsAreSet; 
		
		//Here we assert that the two first bits should be set.
		assertEquals(twoFirstBitsAreSet, twoFirstBitsOfKeyId);
	}
	
	@Test
	public void embeddedKeyEncoding202020() {
		Config.setSubjectSizeInBits(20);
		Config.setPredicateSizeInBits(20);
		Config.setObjectSizeInBits(20);
		
		Key key = KeyFactory.createKey(1023,511,255);
		long id = Long.MIN_VALUE + Long.parseLong("0000000000000011111111110000000000011111111100000000000011111111", 2);
		assertEquals(id, key.getId());
	}
	
	@Test
	public void embeddedKeyEncoding202022() {
		Config.setSubjectSizeInBits(20);
		Config.setPredicateSizeInBits(20);
		Config.setObjectSizeInBits(22);
		
		Key key = KeyFactory.createKey(1023,511,255);
		long id = Long.MIN_VALUE + Long.parseLong("000000000000001111111111000000000001111111110000000000000011111111", 2);
		assertEquals(id, key.getId());
	}
	
	
	@Test
	public void embeddedKeyEncoding201030() {
		Config.setSubjectSizeInBits(20);
		Config.setPredicateSizeInBits(10);
		Config.setObjectSizeInBits(30);
		
		Key key = KeyFactory.createKey(1023,511,255);
		long id = Long.MIN_VALUE + Long.parseLong("0000000000000011111111110111111111000000000000000000000011111111", 2);
		assertEquals(id, key.getId());
	}
	
	public void embeddedKeyEncoding101030() {
		Config.setSubjectSizeInBits(10);
		Config.setPredicateSizeInBits(10);
		Config.setObjectSizeInBits(30);
		
		App.validateBitEncoding();
		
		Key key = KeyFactory.createKey(1023,511,255);
		long id = Long.MIN_VALUE + Long.parseLong("11111111110111111111000000000000000000000011111111", 2);
		assertEquals(id, key.getId());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void embeddedKeyEncoding103030() {
		Config.setSubjectSizeInBits(10);
		Config.setPredicateSizeInBits(30);
		Config.setObjectSizeInBits(30);
		
		App.validateBitEncoding();
	}
}

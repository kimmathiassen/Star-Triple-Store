package dk.aau.cs.qweb.dictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PrefixDictionary
{
	private PrefixDictionary() {}
	private static PrefixDictionary instance;
	public static PrefixDictionary getInstance() {
		if(instance == null) {
	         instance = new PrefixDictionary();
	    }
	    return instance;
	}
	final protected ArrayList<String> Id2Prefix = new ArrayList<String> ();
	final protected Map<String,Integer> PrefixName2Id = new HashMap<String,Integer> ();


	/**
	 * Returns the query variable identified by the given identifier.
	 *
	 * @throws IllegalArgumentException if the given identifier is unknown to
	 *                                  this dictionary
	 */
	final public String getPrefix ( int id ) throws IllegalArgumentException
	{
		String v = Id2Prefix.get( id );
//		
//		if (Config.ignoreFilePrefixInQueries()) {
//			if (v.startsWith("file:///")) {
//				v = "";
//			} 
//		}

		if ( v == null ) {
			throw new IllegalArgumentException( "The given identifier (" + String.valueOf(id) + ") is unknown." );
		}

		return v;
	}
	
	public boolean contains(int id) {
		return Id2Prefix.contains(id);
	}

	/**
	 * Returns the identifier that identifies the given query variable.
	 *
	 * @throws IllegalArgumentException if the given variable is unknown to
	 *                                  this dictionary
	 */
	final public int getId ( String v ) throws IllegalArgumentException
	{
//		if (v.getVarName().startsWith("_")) {
//			throw new IllegalArgumentException("Illegal variable name, must not start with _");
//		}
		Integer i = PrefixName2Id.get( v );

		if ( i == null ) {
			throw new IllegalArgumentException( "The given prefix (" + v + ") is unknown." );
		}

		return  i.intValue();
	}

	/**
	 * Returns the number of query variables known by this dictionary.
	 */
	final public int size ()
	{
		return PrefixName2Id.size();
	}

	// operations

	/**
	 * Returns an identifier that identifies the given query variable.
	 * If there is no identifier for the given query variable yet this method
	 * creates a new identifier and adds it to the dictionary.
	 */
	final public int createId ( String v )
	{
		int result;
		Integer i = PrefixName2Id.get( v );

		if ( i == null )
		{
			result = Id2Prefix.size();
			Id2Prefix.add( v );

			assert result < Integer.MAX_VALUE;

			PrefixName2Id.put( v, Integer.valueOf(result) );
		}
		else {
			result = i.intValue();
		}

		return result;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Id2Prefix.size(); i++) {
			sb.append("#"+i+": "+ Id2Prefix.get(i)+"\n");
		}
		return sb.toString();
	}

	public void clear() {
		Id2Prefix.clear();
		PrefixName2Id.clear();
	}
}

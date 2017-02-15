package dk.aau.cs.qweb.queryengine;

import dk.aau.cs.qweb.triple.Key;

public class SolutionMapping {
	final protected long[] map;
	final static public int UNBOUND = 0;


	// initialization

	public SolutionMapping ( int size )
	{
		map = new long[size];
		for ( int i = 0; i < size; ++i ) {
			map[i] = SolutionMapping.UNBOUND;
		}
	}

	/**
	 * Copy constructor which assumes that the given {@link SolutionMapping} is
	 * actually a {@link FixedSizeSolutionMappingImpl}.
	 */
	public SolutionMapping ( SolutionMapping template )
	{
		SolutionMapping input = template;

		int size = input.map.length;
		map = new long[size];
		for ( int i = 0; i < size; ++i ) {
			map[i] = input.map[i];
		}
	}


	// implementation of the SolutionMapping interface

	public void set ( int varId, Key key){
		long valueId = key.getId();
		map[varId] = valueId;
	}

	public boolean contains ( int varId )
	{
		return ( map[varId] != SolutionMapping.UNBOUND );
	}

	public long get ( int varId )
	{
		return map[varId];
	}

	public int size ()
	{
		return map.length;
	}

	// redefinition of Object methods

	@Override
	public String toString ()
	{
		String s = "SolutionMapping(";

		int size = map.length;
		for ( int i = 0; i < size; ++i )
		{
			if ( map[i] != SolutionMapping.UNBOUND ) {
				s += String.valueOf(i) + "->" + String.valueOf(map[i]) + " ";
			}
		}

		s += ")";
		return s;
	}

}

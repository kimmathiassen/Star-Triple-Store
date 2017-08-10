package dk.aau.cs.qweb.dictionary;

import org.apache.jena.graph.Node;

import dk.aau.cs.qweb.triple.Key;

public interface NodeDictionary {
	public void close();
	
	public void open();

	public boolean isThereAnySpecialReferenceTripleDistributionConditions() ;
	
	public int size() ;
	
	public int getNumberOfEmbeddedTriples() ;

	public Node getNode(Key id) ;
	
	public Key getReferernceTripleKey(Key subjectId, Key predicateId, Key objectId) ;
	
	public Key createKey(Node node) ;
	
	public void setReferenceTripleDistribution(int i) ;

	public void clear() ;
	
	public int getNumberOfReferenceTriples() ;
	
	public boolean containsReferernceTripleKey(Key subjectId, Key predicateId, Key objectId) ;
}

package dk.aau.cs.qweb.dictionary;

import java.io.IOException;

import org.apache.jena.graph.Node;

import dk.aau.cs.qweb.triple.Key;

public interface NodeDictionary {
	/**
	 * Method for closing the dictionary, only relevant in case the dictionary is disk based.
	 * However, it is good practice to always close a dictionary after use.
	 */
	public void close();
	
	/**
	 * Method for opening the dictionary, this method should be called before any of the other methods are called.
	 * In theory this methods is only relevant in case the dictionary is disk based.
	 */
	public void open();

	/**
	 * see {@link NodeDictionary#setReferenceTripleDistribution(int)} for more information about this flag.
	 * @return returns true iff the special reference triple distribution condition is set.
	 */
	public boolean isThereAnySpecialReferenceTripleDistributionConditions() ;
	
	/**
	 * @return the combined size of the node dictionary and the reference dictionary.
	 */
	public int size() ;
	
	/**
	 * @return the number of embedded triples added to the system. Duplicates will be counted.
	 */
	public int getNumberOfEmbeddedTriples() ;

	/**
	 * This method is used when a key should be serialized.
	 * 
	 * @param The key encoding the Node
	 * @return The node that was encoding using the key.
	 */
	public Node getNode(Key id) ;
	
	/**
	 * Method for retrieving a referece key based on a subject, predicate, and object key.
	 * Returns null if the reference key does not exist, please use {@link #containsReferernceTripleKey(Key, Key, Key)} before invoking this method.
	 * 
	 * @param subject key
	 * @param predicate key
	 * @param object key
	 * @return a reference key
	 */
	public Key getReferernceTripleKey(Key subjectId, Key predicateId, Key objectId) ;
	
	/**
	 * This method creates a new entry in the dictionary for the node and returns the newly 
	 * create key unless the node already exist, in this case the existing key of the node is returned.
	 * 
	 * @param The node to be encoded
	 * @return The key that is the encoding.
	 */
	public Key createKey(Node node) ;
	
	/**
	 * It is possible to tell the software to fake the need for reference keys. 
	 * Traditionally a reference key is created if one of the keys in a embedded triple cannot be cast to fit in the reduced size.
	 * E.g. the subject of an embedded triple have a key that requires 21 bits to be represented, and subject of the embedded triple must only use 20 bits.
	 * In this case a reference key is craeted for the embedded key. 
	 * When the distribution is set to a precentage to higher than 0 % (default is 0%), each time a new embedded triple is about to be created,
	 * The a special check will be made to see if a certain distribution between reference keys and embedded triple keys are met, 
	 * if not a new reference key is craeted instead of an embedded key.
	 * See {@link AbstractNodeDictionary#registerOrGetEmbeddedNode(Key, Key, Key, dk.aau.cs.qweb.node.EmbeddedNode) }  
	 * 
	 * @param a percentage that determine the distribution of reference vs none-reference keys. (between 0-100)
	 */
	public void setReferenceTripleDistribution(int i) ;

	/**
	 * Empties the dicitonary
	 * @throws IOException 
	 */
	public void clear() throws IOException ;
	
	/**
	 * @return the number of entiers in the reference dictionary
	 */
	public int getNumberOfReferenceTriples() ;
	
	/**
	 * Method for checking if a reference key already exist for a embedded triple.
	 * @param subject key
	 * @param predicate key
	 * @param object key
	 * @return true iff a reference key mathcing the input parameters already exist.
	 */
	public boolean containsReferernceTripleKey(Key subjectId, Key predicateId, Key objectId) ;
	
}

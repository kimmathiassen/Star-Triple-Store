package dk.aau.cs.qweb.triplestore.flatindex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.iterators.IteratorChain;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.jena.ext.com.google.common.collect.Lists;

import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triple.TripleStar;
import dk.aau.cs.qweb.triple.TripleStarPattern;
import dk.aau.cs.qweb.triplestore.Index;
import dk.aau.cs.qweb.triplestore.IteratorWrapper;
import dk.aau.cs.qweb.triplestore.KeyContainer;
import dk.aau.cs.qweb.triplestore.hashindex.MapIndex;
import dk.aau.cs.qweb.triplestore.hashindex.MapIndex.Field;

public class FlatIndex implements Index {

	protected Map<FlatKey, ArrayList<KeyContainer>> indexMap;
	protected Field field1;
	protected Field field2;
	protected Field field3;
	
	public FlatIndex(Field field1, Field field2, Field field3) {
		this.field1 = field1;
		this.field2 = field2;
		this.field3 = field3;
		indexMap = new HashMap<FlatKey,ArrayList<KeyContainer>>();
	}

	protected Key getFieldKey(Field field,TripleStar t) {
		if (field == Field.S) {
			return t.subjectId;
		} else if (field == Field.P) {
			return t.predicateId;
		} else {
			return t.objectId;
		}
	}

	public boolean remove(TripleStar t) {
		throw new NotImplementedException("Index.remove");
	}

	public void clear() {
		indexMap.clear();
	}

	public long size() {
		return indexMap.size();
	}

	public boolean isEmpty() {
		return indexMap.isEmpty();
	}

	public boolean contains(TripleStarPattern t) {
		FlatKey key ;
		Key firstKey = t.getField(field1).getKey();
		
		if (t.isFieldConcrete(field2)) {
			key = new FlatKey(firstKey,t.getField(field2).getKey());
		} else {
			key = new FlatKey(firstKey);
		}
		
		if (indexMap.containsKey(key)) {
			return indexMap.get(key).contains(new KeyContainer(t.getObject().getKey(),Field.O));
		}
		return false;
	}

	public Iterator<KeyContainer> iterator(TripleStarPattern triple) {
		FlatKey key ;
		Key firstKey = triple.getField(field1).getKey();
		
		if (triple.isFieldConcrete(field2)) {
			key = new FlatKey(firstKey,triple.getField(field2).getKey());
		} else {
			key = new FlatKey(firstKey);
		}
		
		
		if (indexMap.containsKey(key)) {
			//Zero variables
			if (triple.isFieldConcrete(field3)) {
				throw new IllegalArgumentException("No varialbes found in triple "+triple+", use graph.contains instead");
				//return new AddKeyToIteratorWrapper(indexMap.get(firstKey).iterator(triple.getField(field2).getKey(),triple),firstKey,field1);
			} 
			else {
				if (triple.isFieldConcrete(field2)) {
					Key secondKey = triple.getField(field2).getKey();
					return new IteratorWrapper(indexMap.get(key).iterator(), firstKey, field1, secondKey, field2);
				} else {
					System.out.println(indexMap);
					for (KeyContainer iterable_element : indexMap.get(key)) {
						System.out.println(iterable_element);
					}
					return new IteratorWrapper(indexMap.get(key).iterator(), firstKey, field1);
				}
			}
		}
		return Collections.emptyIterator();
	}

	public void removedOneViaIterator() {
		throw new NotImplementedException("Index.removedOneViaIterator");
	}

	public Iterator<KeyContainer> iterateAll() {
		IteratorChain<KeyContainer> chain = new IteratorChain<KeyContainer>();
		for (Entry<FlatKey, ArrayList<KeyContainer>> iterable_element : indexMap.entrySet()) {
			Key firstKey = iterable_element.getKey().getFirstField();
			Iterator<KeyContainer> iterator = iterable_element.getValue().iterator();
			if (iterable_element.getKey().getSecondField() == null) {
				
				chain.addIterator(new IteratorWrapper(iterator,firstKey,field1));
			} else {
				Key secondKey = iterable_element.getKey().getSecondField();
				chain.addIterator(new IteratorWrapper(iterator,firstKey,field1,secondKey,field2));
			}
		}
		return chain;
	}

	public void eliminateDuplicates() {
		for (Entry<FlatKey, ArrayList<KeyContainer>> list : indexMap.entrySet()) {
			ArrayList<Integer> duplicates = new ArrayList<Integer>();
			ArrayList<KeyContainer> values = list.getValue();
			Collections.sort(values);
			KeyContainer previous = null;
			for (KeyContainer tripleStar : list.getValue()) {
				if (tripleStar.equals(previous)) {
					duplicates.add(values.indexOf(tripleStar));
				}
				previous = tripleStar;
			}
			//Remove dup in reverse order to avoid changeing the indexs
			for (Integer integer : Lists.reverse(duplicates)) {
				list.getValue().remove((int)integer);
			}
		}
	}
	
	@Override
	public String toString() {
		return indexMap.toString();
	}

	@Override
	public void add(final TripleStar t) {
		Key firstKey = getFieldKey(field1,t);
		Key secondKey = getFieldKey(field2,t);
		Key thirdKey = getFieldKey(field3,t);
		FlatKey doubleKey = new FlatKey(firstKey,secondKey);
		FlatKey singleKey = new FlatKey(firstKey);
		
		//Add general key e.g. S??
		if (indexMap.containsKey(singleKey)) {
			KeyContainer kc = new KeyContainer(thirdKey,field3);
			kc.addKey(secondKey, field2);
			indexMap.get(singleKey).add(kc);
		} else {
			ArrayList<KeyContainer> array = new ArrayList<KeyContainer>();
			KeyContainer kc = new KeyContainer(thirdKey,field3);
			kc.addKey(secondKey, field2);
			array.add(kc);
			indexMap.put(singleKey, array);
		}
		
		//Add specific key e.g. SP?
		if (indexMap.containsKey(doubleKey)) {
			indexMap.get(doubleKey).add(new KeyContainer(thirdKey,field3));
		} else {
			ArrayList<KeyContainer> array = new ArrayList<KeyContainer>();
			array.add(new KeyContainer(thirdKey,field3));
			indexMap.put(doubleKey, array);
		}
	}
}

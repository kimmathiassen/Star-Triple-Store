package dk.aau.cs.qweb.triplestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;

import dk.aau.cs.qweb.triple.TripleStar;


// maybe replace with Guava (formerly Google Collections) Iterators.concat.
public class IteratorOfIterators implements Iterator<TripleStar> {
    private final List<Iterator<TripleStar>> iterators;
    private Iterator<TripleStar> currentIterator;
    private Iterator<Iterator<TripleStar>> listIterator;


    public IteratorOfIterators(Collection<HashSet<TripleStar>> collection) {
    	iterators = new ArrayList<Iterator<TripleStar>>();
    	for (Collection<TripleStar> container : collection) {
			iterators.add(container.iterator());
		}
    	listIterator = iterators.iterator();
    	currentIterator = listIterator.next();
	}

	public IteratorOfIterators(Iterator<TripleBunch> iterator) {
		iterators = new ArrayList<Iterator<TripleStar>>();
		while (iterator.hasNext()) {
			TripleBunch tripleBunch = iterator.next();
			iterators.add(tripleBunch.iterator());
		}
			
    	listIterator = iterators.iterator();
    	currentIterator = listIterator.next();
	}



	public boolean hasNext() {
		if (currentIterator.hasNext()) {
			return true;
		} else {
			if (listIterator.hasNext()) {
				currentIterator = listIterator.next();
				//Here we assume that the next iterator always has at least one element (Which is always true at the time of writing this method).
				return true;
			} else {
				return false;
			}
		}
	}

    public TripleStar next() {
    	return currentIterator.next();
    }

    public void remove() { 
    	throw new NotImplementedException("IteratorOfIterator.remove");
    }
}
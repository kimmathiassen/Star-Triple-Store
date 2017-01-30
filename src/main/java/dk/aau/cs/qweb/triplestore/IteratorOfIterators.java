package dk.aau.cs.qweb.triplestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;

import dk.aau.cs.qweb.triple.IdTriple;


// maybe replace with Guava (formerly Google Collections) Iterators.concat.
public class IteratorOfIterators implements Iterator<IdTriple> {
    private final List<Iterator<IdTriple>> iterators;
    private Iterator<IdTriple> currentIterator;
    private Iterator<Iterator<IdTriple>> listIterator;


    public IteratorOfIterators(Collection<HashSet<IdTriple>> collection) {
    	iterators = new ArrayList<Iterator<IdTriple>>();
    	for (Collection<IdTriple> container : collection) {
			iterators.add(container.iterator());
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

    public IdTriple next() {
    	return currentIterator.next();
    }

    public void remove() { 
    	throw new NotImplementedException("IteratorOfIterator.remove");
    }
}
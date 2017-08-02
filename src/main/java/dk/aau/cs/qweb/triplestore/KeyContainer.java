package dk.aau.cs.qweb.triplestore;

import org.apache.jena.reasoner.IllegalParameterException;

import dk.aau.cs.qweb.triple.Key;
import dk.aau.cs.qweb.triplestore.MapIndex.Field;

public class KeyContainer implements Comparable<KeyContainer> {
	

	private Key subject;
	private Key predicate;
	private Key object;

	public KeyContainer(Key subject, Key predicate, Key object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	public KeyContainer(Key key,Field f) {
		addKey(key,f);
	}

	public KeyContainer(KeyContainer keyContainer) {
		if (keyContainer.containsSubject()) {
			subject = keyContainer.subject;
		}
		if (keyContainer.containsPredicate()) {
			predicate = keyContainer.predicate;
		}
		if (keyContainer.containsObject()) {
			object = keyContainer.object;
		}
	}

	@Override
	public int compareTo(KeyContainer o) {
		//CompareTo in only used to sort database entries and there is precisely one key in each keyContainer.
		//Note that the case (s,p) compareTo (s), is not handled 
		if (containsSubject() == true && o.containsSubject() == true) {
			return subject.compareTo(o.getSubject());
		}
		if (containsPredicate() == true && o.containsPredicate() == true) {
			return predicate.compareTo(o.getPredicate());
		}
		return object.compareTo(o.getObject());
	}

	public boolean containsSubject() {
		return subject == null ? false : true;
	}

	public Key getSubject() {
		return subject;
	}

	public boolean containsPredicate() {
		return predicate == null ? false : true;
	}

	public Key getPredicate() {
		return predicate;
	}

	public boolean containsObject() {
		return object == null ? false : true;
	}

	public Key getObject() {
		return object;
	}

	public void addKey(Key key,Field f) {
		if (f == Field.S) {
			subject = key;
		} else if (f == Field.P) {
			predicate = key;
		} else if (f == Field.O) {
			object = key;
		} else {
			throw new IllegalParameterException("Field "+f+", did not match any know fields");
		}
	}
	@Override
	public String toString() {
		return (containsSubject() ? "S: " + subject+", " : "")+(containsPredicate() ? "P: " + predicate+", " : "")+(containsObject() ? "O: " + object : "");
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof KeyContainer) {
			KeyContainer o = (KeyContainer) other;
			boolean subjectEq = true;
			boolean predicateEq = true;
			boolean objectEq = true;
			
			//Handle null cases
			if (containsSubject() && o.containsSubject()) {
				subjectEq = subject.equals(o.subject);
			} else {
				subjectEq = (subject == o.subject) ? true : false;
			}
			
			if (containsPredicate() && o.containsPredicate()) {
				predicateEq = predicate.equals(o.predicate);
			} else {
				predicateEq = (predicate == o.predicate) ? true : false;
			}
			
			if (containsObject() && o.containsObject()) {
				objectEq = object.equals(o.object);
			} else {
				objectEq = (object == o.object) ? true : false;
			}
			
			return subjectEq && predicateEq && objectEq;
		} 
		return false;
	}
}

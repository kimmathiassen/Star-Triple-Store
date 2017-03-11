package dk.aau.cs.qweb.triple;

import org.apache.jena.reasoner.IllegalParameterException;

import dk.aau.cs.qweb.triplestore.Index.Field;

public class TripleStarPattern implements StarNode{
	
	private final StarNode subject;
	private final StarNode predicate;
	private final StarNode object;

	public StarNode getSubject() { return subject;}
	public StarNode getPredicate() { return predicate;}
	public StarNode getObject() { return object;}
	

	public TripleStarPattern(StarNode subject, StarNode predicate, StarNode object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	public boolean isFieldConcrete(Field field) {
		if (field == Field.S) {
			return subject.isConcreate();
		} else if (field == Field.P) {
			return predicate.isConcreate();
		} else if (field == Field.O) {
			return object.isConcreate();
		}
		throw new IllegalParameterException("unknown Field " + field +" expected S, P or O.");
	}
	
	public String toString () {
		return "("+subject + ", " + predicate + ", " + object+")";
	}
	

	
	@Override
	public boolean equals(Object other) {
		if (other instanceof TripleStar) {
			TripleStar casted = (TripleStar)other;
			return (this.hashCode() == casted.hashCode());
		} else {
			return super.equals(other);
		}
	}

	public StarNode getField(Field field) {
		if (field == Field.S) {
			if (subject.isConcreate()) {
				return subject;
			} else {
				throw new IllegalStateException("The field "+field+" contains variables and should not be requested at this point in the code");
			}
		} else if (field == Field.P) {
			return predicate;
		} else if (field == Field.O) {
				if (object.isConcreate()) {
					return object;
				} else {
					throw new IllegalStateException("The field "+field+" contains variables and should not be requested at this point in the code");
				}
			}
		throw new IllegalParameterException("unknown Field " + field +" expected S, P or O.");
	}

	public boolean doesAllKeysExistInDictionary() {
		return !check();
	}

	private boolean check() {
		boolean subjectExistInDict = false;
		boolean predicateExistInDict = false;
		boolean objectExistInDict = false;
		if (subject.isConcreate()) {
			if (subject.isEmbeddedTriplePattern()) {
				subjectExistInDict = subject.getTriplePattern().check();
			} else {
				subjectExistInDict = subject.getKey().getId() == 0 ? true : false;
			}
		}
		if (predicate.isConcreate()) {
			predicateExistInDict = predicate.getKey().getId() == 0 ? true : false;
		}
		if (object.isConcreate()) {
			if (object.isEmbeddedTriplePattern()) {
				objectExistInDict = object.getTriplePattern().check();
			} else {
				objectExistInDict = object.getKey().getId() == 0 ? true : false;
			}
		}
		return subjectExistInDict && predicateExistInDict && objectExistInDict;
	}


	@Override
	public boolean isKey() {
		return false;
	}

	@Override
	public Key getKey() {
		throw new IllegalArgumentException("Is not of the type Key");
	}

	@Override
	public boolean isEmbeddedTriplePattern() {
		return true;
	}

	@Override
	public TripleStarPattern getTriplePattern() {
		return this;
	}

	@Override
	public boolean isVariable() {
		return false;
	}

	@Override
	public dk.aau.cs.qweb.triple.Variable getVariable() {
		throw new IllegalArgumentException("Is not of the type TripleStarPattern");
	}


	@Override
	public boolean isConcreate() {
		return (subject.isConcreate() && predicate.isConcreate() && object.isConcreate() ? true : false);
	}
}

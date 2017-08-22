package dk.aau.cs.qweb.triple;

import org.apache.jena.reasoner.IllegalParameterException;

import dk.aau.cs.qweb.triplestore.hashindex.MapIndex.Field;

public class TripleStarPattern implements Element{
	
	private final Element subject;
	private final Element predicate;
	private final Element object;

	public Element getSubject() { return subject;}
	public Element getPredicate() { return predicate;}
	public Element getObject() { return object;}
	

	public TripleStarPattern(Element subject, Element predicate, Element object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	public boolean isFieldConcrete(Field field) {
		if (field == Field.S) {
			return subject.isConcrete();
		} else if (field == Field.P) {
			return predicate.isConcrete();
		} else if (field == Field.O) {
			return object.isConcrete();
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

	public Element getField(Field field) {
		if (field == Field.S) {
			if (subject.isConcrete()) {
				return subject;
			} else {
				throw new IllegalStateException("The field "+field+" contains variables and should not be requested at this point in the code");
			}
		} else if (field == Field.P) {
			return predicate;
		} else if (field == Field.O) {
				if (object.isConcrete()) {
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
		if (subject.isConcrete()) {
			if (subject.isEmbeddedTriplePattern()) {
				subjectExistInDict = subject.getTriplePattern().check();
			} else {
				subjectExistInDict = subject.getKey().getId() == 0 ? true : false;
			}
		}
		if (predicate.isConcrete()) {
			predicateExistInDict = predicate.getKey().getId() == 0 ? true : false;
		}
		if (object.isConcrete()) {
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
	public boolean isConcrete() {
		return (subject.isConcrete() && predicate.isConcrete() && object.isConcrete() ? true : false);
	}
}

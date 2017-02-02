package dk.aau.cs.qweb.triple;

import java.util.Objects;

import org.apache.jena.reasoner.IllegalParameterException;

import dk.aau.cs.qweb.triplestore.Index.Field;

public class TriplePattern {

	private Key subjectId;
	private Key predicateId;
	private Key objectId;
	private boolean subjectIsConcrete = false;
	private boolean predicateIsConcrete = false;
	private boolean objectIsConcrete = false;
	
	public enum Variable {
		ANY;
	}
	
	private final void setSubject(final Key key) {
		subjectId = key;
		subjectIsConcrete = true;
	}
	
	private void setPredicate(Key key) {
		predicateId = key;
		predicateIsConcrete = true;
	}
	
	private final void setObject(Key key) {
		objectId = key;
		objectIsConcrete = true;
	}

	public TriplePattern(Variable variableA, Variable variableB, Variable variableC) {
	}

	public TriplePattern(Variable variableA, Variable variableB, Key object) {
		setObject(object);
	}

	public TriplePattern(Variable subjectVariableName, Key predicate, Variable objectVariableName) {
		setPredicate(predicate);
	}

	public TriplePattern(Variable subjectVariableName, Key predicate, Key object) {
		setPredicate(predicate);
		setObject(object);
	}

	public TriplePattern(final Key subject, Variable predicateVariableName, Variable objectVariableName) {
		setSubject(subject);
	}

	public TriplePattern(Key subject, Variable predicateVariableName, Key object) {
		setSubject(subject);
		setObject(object);
	}

	public TriplePattern(Key subject, Key predicate, Variable objectVariableName) {
		setPredicate(predicate);
		setSubject(subject);
	}

	public TriplePattern(Key subject, Key predicate, Key object) {
		setSubject(subject);
		setPredicate(predicate);
		setObject(object);
	}

	public Key getPredicate() {
		if (predicateIsConcrete) {
			return predicateId;
		}
		throw new UnsupportedOperationException("Predicate is not concrete");
	}

	public Key getObject() {
		if (objectIsConcrete) {
			return objectId;
		}
		throw new UnsupportedOperationException("Object is not concrete");
	}

	public Key getSubject() {
		if (subjectIsConcrete) {
			return subjectId;
		}
		throw new UnsupportedOperationException("Subject is not concrete");
	}

	public boolean isSubjectConcrete() {
		return subjectIsConcrete;
	}

	public boolean isPredicateConcrete() {
		return predicateIsConcrete;
	}

	public boolean isObjectConcrete() {
		return objectIsConcrete;
	}

	public boolean isConcrete(Field field) {
		if (field == Field.S) {
			return isSubjectConcrete();
		} else if (field == Field.P) {
			return isPredicateConcrete();
		} else if (field == Field.O) {
			return isObjectConcrete();
		}
		throw new IllegalParameterException("unknown Field " + field +" expected S, P or O.");
	}
	
	public String toString () {
		
		if (!subjectIsConcrete) {
			if (!predicateIsConcrete) {
				if (!objectIsConcrete) {
					return "(ANY,ANY,ANY)";
				} else {
					return "(ANY,ANY,"+getObject()+")";
				}
			} else {
				if (!objectIsConcrete) {
					return "(ANY,"+getPredicate()+",ANY)";
				} else {
					return "(ANY,"+getPredicate()+","+getObject()+")";
				}
			}
		} else {
			if (!predicateIsConcrete) {
				if (!objectIsConcrete) {
					return "("+getSubject()+",ANY,ANY)";
				} else {
					return "("+getSubject()+",ANY,"+getObject()+")";
				}
			} else {
				if (!objectIsConcrete) {
					return "("+getSubject()+","+getPredicate()+",ANY)";
				} else {
					return "("+getSubject()+","+getPredicate()+","+getObject()+")";
				}
			}
		}
	}
	
	@Override
    public int hashCode() {
		if (!subjectIsConcrete) {
			if (!predicateIsConcrete) {
				if (!objectIsConcrete) {
					return Objects.hash(false,false,false);
				} else {
					return Objects.hash(false,false,getObject());
				}
			} else {
				if (!objectIsConcrete) {
					return Objects.hash(false,getPredicate(),false);
				} else {
					return Objects.hash(false,getPredicate(),getObject());
				}
			}
		} else {
			if (!predicateIsConcrete) {
				if (!objectIsConcrete) {
					return Objects.hash(getSubject(),false,false);
				} else {
					return Objects.hash(getSubject(),false,getObject());
				}
			} else {
				if (!objectIsConcrete) {
					return Objects.hash(getSubject(),getPredicate(),false);
				} else {
					return Objects.hash(getSubject(),getPredicate(),getObject());
				}
			}
		}
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

	public Key getKey(Field field) {
		if (field == Field.S) {
			return subjectId;
		} else if (field == Field.P) {
			return predicateId;
		} else if (field == Field.O) {
			return objectId;
		}
		throw new IllegalParameterException("unknown Field " + field +" expected S, P or O.");
	}
}

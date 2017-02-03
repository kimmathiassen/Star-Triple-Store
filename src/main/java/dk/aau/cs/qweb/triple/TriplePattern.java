package dk.aau.cs.qweb.triple;

import java.util.Objects;

import org.apache.jena.reasoner.IllegalParameterException;

import dk.aau.cs.qweb.helper.BitHelper;
import dk.aau.cs.qweb.triplestore.Index.Field;

public class TriplePattern {

	private Key subjectId;
	private Key predicateId;
	private Key objectId;
	private TriplePattern subjectTriplePattern;
	private TriplePattern objectTriplePattern;
	private boolean subjectIsConcrete = false;
	private boolean predicateIsConcrete = false;
	private boolean objectIsConcrete = false;
	private boolean subjectIsTriplePattern = false;
	private boolean objectIsTriplePattern = false;
	
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
	
	public TriplePattern(TriplePattern subject, Key predicate, Key object) {
		setSubjectTriplePattern(subject);
		setPredicate(predicate);
		setObject(object);
	}
	
	public TriplePattern(TriplePattern subject, Key predicate, Variable objectVariableName) {
		setSubjectTriplePattern(subject);
		setPredicate(predicate);
	}
	
	public TriplePattern(TriplePattern subject, Variable predicateVariableName, Variable objectVariableName) {
		setSubjectTriplePattern(subject);
	}
	
	public TriplePattern(TriplePattern subject, Variable predicateVariableNam, Key object) {
		setSubjectTriplePattern(subject);
		setObject(object);
	}
	
	public TriplePattern(TriplePattern subject, Key predicate, TriplePattern object) {
		setSubjectTriplePattern(subject);
		setPredicate(predicate);
		setObjectTriplePattern(object);
	}
	public TriplePattern(TriplePattern subject, Variable any, TriplePattern object) {
		setSubjectTriplePattern(subject);
		setObjectTriplePattern(object);
	}
	
	public TriplePattern(Key subject, Key predicate, TriplePattern object) {
		setSubject(subject);
		setPredicate(predicate);
		setObjectTriplePattern(object);
	}
	
	public TriplePattern(Variable subject, Key predicate, TriplePattern object) {
		setPredicate(predicate);
		setObjectTriplePattern(object);
	}
	
	public TriplePattern(Variable subject, Variable predicate, TriplePattern object) {
		setObjectTriplePattern(object);
	}
	
	public TriplePattern(Key subject, Variable predicate, TriplePattern object) {
		setSubject(subject);
		setObjectTriplePattern(object);
	}

	public Key getPredicateKey() {
		if (predicateIsConcrete) {
			return predicateId;
		}
		throw new UnsupportedOperationException("Predicate is not concrete");
	}

	public Key getObjectKey() {
		if (objectIsConcrete) {
			return objectId;
		}
		throw new UnsupportedOperationException("Object is not concrete");
	}

	public Key getSubjectKey() {
		if (subjectIsConcrete) {
			return subjectId;
		}
		throw new UnsupportedOperationException("Subject is not concrete");
	}
	
	public TriplePattern getObjectTriplePattern() {
		if (objectIsConcrete) {
			if (objectIsTriplePattern) {
				return objectTriplePattern;
			}
			throw new UnsupportedOperationException("Object is not a TriplePattern");
		}
		throw new UnsupportedOperationException("Object is not concrete");
	}

	public TriplePattern getSubjectTriplePattern() {
		if (subjectIsConcrete) {
			if (subjectIsTriplePattern) {
				return subjectTriplePattern;
			}
			throw new UnsupportedOperationException("Subject is not a TriplePattern");
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
					if (objectIsTriplePattern) {
						return "(ANY,ANY,"+getObjectTriplePattern()+")";
					} else {
						return "(ANY,ANY,"+getObjectKey()+")";
					}
				}
			} else {
				if (!objectIsConcrete) {
					return "(ANY,"+getPredicateKey()+",ANY)";
				} else {
					if (objectIsTriplePattern) {
						return "(ANY,"+getPredicateKey()+","+getObjectTriplePattern()+")";
					} else {
						return "(ANY,"+getPredicateKey()+","+getObjectKey()+")";
					}
				}
			}
		} else {
			if (!predicateIsConcrete) {
				if (!objectIsConcrete) {
					if (subjectIsTriplePattern) {
						return "("+getSubjectTriplePattern()+",ANY,ANY)";
					} else {
						return "("+getSubjectKey()+",ANY,ANY)";
					}
					
				} else {
					if (objectIsTriplePattern && subjectIsTriplePattern) {
						return "("+getSubjectTriplePattern()+",ANY,"+getObjectTriplePattern()+")";
					} else if (subjectIsTriplePattern) {
						return "("+getSubjectTriplePattern()+",ANY,"+getObjectKey()+")";
					} else if (objectIsTriplePattern) {
						return "("+getSubjectKey()+",ANY,"+getObjectTriplePattern()+")";
					} else {
						return "("+getSubjectKey()+",ANY,"+getObjectKey()+")";
					}
				}
			} else {
				if (!objectIsConcrete) {
					if (subjectIsTriplePattern) {
						return "("+getSubjectTriplePattern()+","+getPredicateKey()+",ANY)";
					} else {
						return "("+getSubjectKey()+","+getPredicateKey()+",ANY)";
					}
				} else {
					if (objectIsTriplePattern && subjectIsTriplePattern) {
						return "("+getSubjectTriplePattern()+","+getPredicateKey()+","+getObjectTriplePattern()+")";
					} else if (subjectIsTriplePattern) {
						return "("+getSubjectTriplePattern()+","+getPredicateKey()+","+getObjectKey()+")";
					} else if (objectIsTriplePattern) {
						return "("+getSubjectKey()+","+getPredicateKey()+","+getObjectTriplePattern()+")";
					} else {
						return "("+getSubjectKey()+","+getPredicateKey()+","+getObjectKey()+")";
					}
				}
			}
		}
	}
	

	
	public int hashCode () {
		if (!subjectIsConcrete) {
			if (!predicateIsConcrete) {
				if (!objectIsConcrete) {
					return Objects.hash(false,false,false);
				} else {
					if (objectIsTriplePattern) {
						return Objects.hash(false,false,getObjectTriplePattern().hashCode());
					} else {
						return Objects.hash(false,false,getObjectKey());
					}
				}
			} else {
				if (!objectIsConcrete) {
					return Objects.hash(false,getPredicateKey(),false);
				} else {
					if (objectIsTriplePattern) {
						return Objects.hash(false,getPredicateKey(),getObjectTriplePattern().hashCode());
					} else {
						return Objects.hash(false,getPredicateKey(),getObjectKey());
					}
				}
			}
		} else {
			if (!predicateIsConcrete) {
				if (!objectIsConcrete) {
					if (subjectIsTriplePattern) {
						return Objects.hash(getSubjectTriplePattern().hashCode(),false,false);
					} else {
						return Objects.hash(getSubjectKey(),false,false);
					}
					
				} else {
					if (objectIsTriplePattern && subjectIsTriplePattern) {
						return Objects.hash(getSubjectTriplePattern().hashCode(),false,getObjectTriplePattern().hashCode());
					} else if (subjectIsTriplePattern) {
						return Objects.hash(getSubjectTriplePattern().hashCode(),false,getObjectKey());
					} else if (objectIsTriplePattern) {
						return Objects.hash(getSubjectKey(),false,getObjectTriplePattern().hashCode());
					} else {
						return Objects.hash(getSubjectKey(),false,getObjectKey());
					}
				}
			} else {
				if (!objectIsConcrete) {
					if (subjectIsTriplePattern) {
						return Objects.hash(getSubjectTriplePattern().hashCode(),getPredicateKey(),false);
					} else {
						return Objects.hash(getSubjectKey(),getPredicateKey(),false);
					}
				} else {
					if (objectIsTriplePattern && subjectIsTriplePattern) {
						return Objects.hash(getSubjectTriplePattern().hashCode(),getPredicateKey(),getObjectTriplePattern().hashCode());
					} else if (subjectIsTriplePattern) {
						return Objects.hash(getSubjectTriplePattern().hashCode(),getPredicateKey(),getObjectKey());
					} else if (objectIsTriplePattern) {
						return Objects.hash(getSubjectKey(),getPredicateKey(),getObjectTriplePattern().hashCode());
					} else {
						return Objects.hash(getSubjectKey(),getPredicateKey(),getObjectKey());
					}
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

	public boolean doesAllKeysExistInDictionary() {
		if (subjectId.equals(0) || predicateId.equals(0) || objectId.equals(0)) {
			return false;
		} else if (BitHelper.isIdAnEmbeddedTriple(subjectId)) { // check if any elements in an embedded triple is zero.
			if (BitHelper.isThereAnyKeysSetToZeroInEmbeddedId(subjectId)) {
				return false;
			}
		} else if (BitHelper.isIdAnEmbeddedTriple(objectId)) {
			if (BitHelper.isThereAnyKeysSetToZeroInEmbeddedId(objectId)) {
				return false;
			}
		}
		return true;
	}


	public void setSubjectTriplePattern(TriplePattern subjectTriplePattern) {
		this.subjectTriplePattern = subjectTriplePattern;
		subjectIsConcrete = true;
		subjectIsTriplePattern = true;
	}

	public void setObjectTriplePattern(TriplePattern objectTriplePattern) {
		this.objectTriplePattern = objectTriplePattern;
		objectIsConcrete = true;
		objectIsTriplePattern = true;
	}

	public boolean isSubjectIsTriplePattern() {
		return subjectIsTriplePattern;
	}

	public void setSubjectIsTriplePattern(boolean subjectIsTriplePattern) {
		this.subjectIsTriplePattern = subjectIsTriplePattern;
	}

	public boolean isObjectIsTriplePattern() {
		return objectIsTriplePattern;
	}

	public void setObjectIsTriplePattern(boolean objectIsTriplePattern) {
		this.objectIsTriplePattern = objectIsTriplePattern;
	}
}

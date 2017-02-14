package dk.aau.cs.qweb.triple;

import java.util.Objects;

import org.apache.jena.reasoner.IllegalParameterException;

import dk.aau.cs.qweb.triplestore.Index.Field;

public class TripleStarPattern implements StarNode{

	private Key subjectId;
	private Key predicateId;
	private Key objectId;
	private TripleStarPattern subjectTriplePattern;
	private TripleStarPattern objectTriplePattern;
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

	public TripleStarPattern(Variable variableA, Variable variableB, Variable variableC) {
	}

	public TripleStarPattern(Variable variableA, Variable variableB, Key object) {
		setObject(object);
	}

	public TripleStarPattern(Variable subjectVariableName, Key predicate, Variable objectVariableName) {
		setPredicate(predicate);
	}

	public TripleStarPattern(Variable subjectVariableName, Key predicate, Key object) {
		setPredicate(predicate);
		setObject(object);
	}

	public TripleStarPattern(final Key subject, Variable predicateVariableName, Variable objectVariableName) {
		setSubject(subject);
	}

	public TripleStarPattern(Key subject, Variable predicateVariableName, Key object) {
		setSubject(subject);
		setObject(object);
	}

	public TripleStarPattern(Key subject, Key predicate, Variable objectVariableName) {
		setPredicate(predicate);
		setSubject(subject);
	}

	public TripleStarPattern(Key subject, Key predicate, Key object) {
		setSubject(subject);
		setPredicate(predicate);
		setObject(object);
	}
	
	public TripleStarPattern(TripleStarPattern subject, Key predicate, Key object) {
		setSubjectTriplePattern(subject);
		setPredicate(predicate);
		setObject(object);
	}
	
	public TripleStarPattern(TripleStarPattern subject, Key predicate, Variable objectVariableName) {
		setSubjectTriplePattern(subject);
		setPredicate(predicate);
	}
	
	public TripleStarPattern(TripleStarPattern subject, Variable predicateVariableName, Variable objectVariableName) {
		setSubjectTriplePattern(subject);
	}
	
	public TripleStarPattern(TripleStarPattern subject, Variable predicateVariableNam, Key object) {
		setSubjectTriplePattern(subject);
		setObject(object);
	}
	
	public TripleStarPattern(TripleStarPattern subject, Key predicate, TripleStarPattern object) {
		setSubjectTriplePattern(subject);
		setPredicate(predicate);
		setObjectTriplePattern(object);
	}
	public TripleStarPattern(TripleStarPattern subject, Variable any, TripleStarPattern object) {
		setSubjectTriplePattern(subject);
		setObjectTriplePattern(object);
	}
	
	public TripleStarPattern(Key subject, Key predicate, TripleStarPattern object) {
		setSubject(subject);
		setPredicate(predicate);
		setObjectTriplePattern(object);
	}
	
	public TripleStarPattern(Variable subject, Key predicate, TripleStarPattern object) {
		setPredicate(predicate);
		setObjectTriplePattern(object);
	}
	
	public TripleStarPattern(Variable subject, Variable predicate, TripleStarPattern object) {
		setObjectTriplePattern(object);
	}
	
	public TripleStarPattern(Key subject, Variable predicate, TripleStarPattern object) {
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
	
	public TripleStarPattern getObjectTriplePattern() {
		if (objectIsConcrete) {
			if (objectIsTriplePattern) {
				return objectTriplePattern;
			}
			throw new UnsupportedOperationException("Object is not a TriplePattern");
		}
		throw new UnsupportedOperationException("Object is not concrete");
	}

	public TripleStarPattern getSubjectTriplePattern() {
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
			if (subjectIsTriplePattern) {
				return KeyFactory.createKey(subjectTriplePattern.getSubjectKey(), subjectTriplePattern.getPredicateKey(), subjectTriplePattern.getObjectKey());
			} else {
				return subjectId;
			}
		} else if (field == Field.P) {
			return predicateId;
		} else if (field == Field.O) {
			if (subjectIsTriplePattern) {
				return KeyFactory.createKey(objectTriplePattern.getSubjectKey(), objectTriplePattern.getPredicateKey(), objectTriplePattern.getObjectKey());
			} else {
				return objectId;
			}
		}
		throw new IllegalParameterException("unknown Field " + field +" expected S, P or O.");
	}

	public boolean doesAllKeysExistInDictionary() {
		return !check(this);
	}

	private boolean check(TripleStarPattern triplePattern) {
		boolean subjectExistInDict = false;
		boolean predicateExistInDict = false;
		boolean objectExistInDict = false;
		if (triplePattern.subjectIsConcrete) {
			if (triplePattern.subjectIsTriplePattern) {
				subjectExistInDict = check(triplePattern.subjectTriplePattern);
			} else {
				subjectExistInDict = triplePattern.subjectId.getId() == 0 ? true : false;
			}
		}
		if (triplePattern.predicateIsConcrete) {
			predicateExistInDict = triplePattern.predicateId.getId() == 0 ? true : false;
		}
		if (triplePattern.objectIsConcrete) {
			if (triplePattern.objectIsTriplePattern) {
				objectExistInDict = check(triplePattern.objectTriplePattern);
			} else {
				objectExistInDict = triplePattern.objectId.getId() == 0 ? true : false;
			}
		}
		return subjectExistInDict && predicateExistInDict && objectExistInDict;
	}

	public void setSubjectTriplePattern(TripleStarPattern subjectTriplePattern) {
		this.subjectTriplePattern = subjectTriplePattern;
		subjectIsConcrete = true;
		subjectIsTriplePattern = true;
	}

	public void setObjectTriplePattern(TripleStarPattern objectTriplePattern) {
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

	@Override
	public boolean isKey() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Key getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEmbeddedTriplePattern() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TripleStarPattern getTriplePattern() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isVariable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public dk.aau.cs.qweb.triple.Variable getVariable() {
		// TODO Auto-generated method stub
		return null;
	}
}

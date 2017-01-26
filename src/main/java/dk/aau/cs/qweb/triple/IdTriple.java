package dk.aau.cs.qweb.triple;

import org.apache.jena.reasoner.IllegalParameterException;

import dk.aau.cs.qweb.triplestore.Index.Field;

//This class is a triple and a triple pattern
public class IdTriple {
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

	public IdTriple(Variable variableA, Variable variableB, Variable variableC) {
	}

	public IdTriple(Variable variableA, Variable variableB, Key object) {
		setObject(object);
	}

	public IdTriple(Variable subjectVariableName, Key predicate, Variable objectVariableName) {
		setPredicate(predicate);
	}

	public IdTriple(Variable subjectVariableName, Key predicate, Key object) {
		setPredicate(predicate);
		setObject(object);
	}

	public IdTriple(final Key subject, Variable predicateVariableName, Variable objectVariableName) {
		setSubject(subject);
	}

	public IdTriple(Key subject, Variable predicateVariableName, Key object) {
		setSubject(subject);
		setObject(object);
	}

	public IdTriple(Key subject, Key predicate, Variable objectVariableName) {
		setPredicate(predicate);
		setSubject(subject);
	}

	public IdTriple(Key subject, Key predicate, Key object) {
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
}

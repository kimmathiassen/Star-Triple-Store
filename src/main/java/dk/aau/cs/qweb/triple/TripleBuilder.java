package dk.aau.cs.qweb.triple;

import org.apache.jena.graph.Node;

public class TripleBuilder {

	private Key subject;
	private Key predicate;
	private Key object;
	private boolean subjectIsVariable;
	private boolean predicateIsVariable;
	private boolean objectIsVariable;
	private String subjectVariableName;
	private String predicateVariableName;
	private String objectVariableName;

	public TripleBuilder() {
		subjectIsVariable = false;
		predicateIsVariable = false;
		objectIsVariable = false;
		subjectVariableName = "";
		predicateVariableName = "";
		objectVariableName = "";
		subject = new Key();
		predicate = new Key();
		object = new Key();
		
	}
	
	public void setSubjectIsVariable(Node node) {
		subjectIsVariable = true;
		subjectVariableName = node.getName();
	}

	public void setPredicateIsVariable(Node node) {
		predicateIsVariable = true;
		predicateVariableName = node.getName();
	}

	public void setSubject(Key i) {
		subject = i;
	}

	public void setPredicate(Key i) {
		predicate = i;
	}

	public void setObject(Key i) {
		object = i;
	}

	public void setObjectIsVariable(Node node) {
		objectIsVariable = true;
		objectVariableName = node.getName();
	}

	public IdTriple createTriple() {
		canTripleBeConstructed();
		IdTriple triple;
		
		if (subjectIsVariable) {
			if (predicateIsVariable) {
				if (objectIsVariable) {
					triple = new IdTriple(subjectVariableName,predicateVariableName,objectVariableName);
				} else {
					triple = new IdTriple(subjectVariableName, predicateVariableName, object);
				}
			} else {
				if (objectIsVariable) {
					triple = new IdTriple(subjectVariableName,predicate,objectVariableName);
				} else {
					triple = new IdTriple(subjectVariableName,predicate,object);
				}
			}
		} else {
			if (predicateIsVariable) {
				if (objectIsVariable) {
					triple = new IdTriple(subject,predicateVariableName,objectVariableName);
				} else {
					triple = new IdTriple(subject, predicateVariableName, object);
				}
			} else {
				if (objectIsVariable) {
					triple = new IdTriple(subject,predicate,objectVariableName);
				} else {
					triple = new IdTriple(subject,predicate,object);
				}
			}
		}
		
		return triple;
	}

	private void canTripleBeConstructed() {
		if (subject.id == 0 && subjectVariableName.equals("")) {
			throw new IllegalArgumentException("TripleBuilder must recieve a valid subject");
		}
		if (predicate.id == 0 && predicateVariableName.equals("")) {
			throw new IllegalArgumentException("TripleBuilder must recieve a valid predicate");
		}
		if (object.id == 0 && objectVariableName.equals("")) {
			throw new IllegalArgumentException("TripleBuilder must recieve a valid object");
		}
		// TODO Auto-generated method stub
		
	}

}

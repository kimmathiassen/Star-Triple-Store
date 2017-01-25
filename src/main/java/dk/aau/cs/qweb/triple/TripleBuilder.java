package dk.aau.cs.qweb.triple;

import org.apache.jena.graph.Node;

public class TripleBuilder {

	private int subject;
	private int predicate;
	private int object;
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
		subject = 0;
		predicate = 0;
		object = 0;
		
	}
	
	public void setSubjectIsVariable(Node node) {
		subjectIsVariable = true;
		subjectVariableName = node.getName();
	}

	public void setPredicateIsVariable(Node node) {
		predicateIsVariable = true;
		predicateVariableName = node.getName();
	}

	public void setSubject(int i) {
		subject = i;
	}

	public void setPredicate(int i) {
		predicate = i;
	}

	public void setObject(int i) {
		object = i;
	}

	public void setObjectIsVariable(Node node) {
		objectIsVariable = true;
		objectVariableName = node.getName();
	}

	public MyTriple createTriple() {
		canTripleBeConstructed();
		MyTriple triple;
		
		if (subjectIsVariable) {
			if (predicateIsVariable) {
				if (objectIsVariable) {
					triple = new MyTriple(subjectVariableName,predicateVariableName,objectVariableName);
				} else {
					triple = new MyTriple(subjectVariableName, predicateVariableName, object);
				}
			} else {
				if (objectIsVariable) {
					triple = new MyTriple(subjectVariableName,predicate,objectVariableName);
				} else {
					triple = new MyTriple(subjectVariableName,predicate,object);
				}
			}
		} else {
			if (predicateIsVariable) {
				if (objectIsVariable) {
					triple = new MyTriple(subject,predicateVariableName,objectVariableName);
				} else {
					triple = new MyTriple(subject, predicateVariableName, object);
				}
			} else {
				if (objectIsVariable) {
					triple = new MyTriple(subject,predicate,objectVariableName);
				} else {
					triple = new MyTriple(subject,predicate,object);
				}
			}
		}
		
		return triple;
	}

	private void canTripleBeConstructed() {
		if (subject == 0 && subjectVariableName.equals("")) {
			throw new IllegalArgumentException("TripleBuilder must recieve a valid subject");
		}
		if (predicate == 0 && predicateVariableName.equals("")) {
			throw new IllegalArgumentException("TripleBuilder must recieve a valid predicate");
		}
		if (object == 0 && objectVariableName.equals("")) {
			throw new IllegalArgumentException("TripleBuilder must recieve a valid object");
		}
		// TODO Auto-generated method stub
		
	}

}

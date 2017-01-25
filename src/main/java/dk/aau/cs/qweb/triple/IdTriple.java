package dk.aau.cs.qweb.triple;

public class IdTriple {
	private Key subjectId;
	private Key propertyId;
	private Key objectId;
	

	public IdTriple(String variableA, String variableB, String variableC) {
		// TODO Auto-generated constructor stub
	}

	public IdTriple(String variableA, String variableB, Key object) {
		// TODO Auto-generated constructor stub
	}

	public IdTriple(String subjectVariableName, Key predicate, String objectVariableName) {
		// TODO Auto-generated constructor stub
	}

	public IdTriple(String subjectVariableName, Key predicate, Key object) {
		// TODO Auto-generated constructor stub
	}

	public IdTriple(Key subject, String predicateVariableName, String objectVariableName) {
		// TODO Auto-generated constructor stub
	}

	public IdTriple(Key subject, String predicateVariableName, Key object) {
		// TODO Auto-generated constructor stub
	}

	public IdTriple(Key subject, Key predicate, String objectVariableName) {
		// TODO Auto-generated constructor stub
	}

	public IdTriple(Key subject, Key predicate, Key object) {
		// TODO Auto-generated constructor stub
	}

	public Key getPredicate() {
		return propertyId;
	}

	public Key getObject() {
		return objectId;
	}

	public Key getSubject() {
		return subjectId;
	}

	public boolean isSubjectConcrete() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isPredicateConcrete() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isObjectConcrete() {
		// TODO Auto-generated method stub
		return false;
	}

}

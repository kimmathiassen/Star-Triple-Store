package dk.aau.cs.qweb.triple;

import dk.aau.cs.qweb.triple.IdTriple.Variable;

public class TripleBuilder {

	private Key subject;
	private Key predicate;
	private Key object;
	private boolean subjectIsConcrete;
	private boolean predicateIsConcrete;
	private boolean objectIsConcrete;

	public TripleBuilder() {
		subjectIsConcrete = false;
		predicateIsConcrete = false;
		objectIsConcrete = false;
	}

	public void setSubject(Key i) {
		subject = i;
		subjectIsConcrete = true;
	}

	public void setPredicate(Key i) {
		predicate = i;
		predicateIsConcrete = true;
	}

	public void setObject(Key i) {
		object = i;
		objectIsConcrete = true;
	}

	public IdTriple createTriple() {
		IdTriple triple;
		
		if (subjectIsConcrete) {
			if (predicateIsConcrete) {
				if (objectIsConcrete) {
					triple = new IdTriple(Variable.ANY,Variable.ANY,Variable.ANY);
				} else {
					triple = new IdTriple(Variable.ANY, Variable.ANY, object);
				}
			} else {
				if (objectIsConcrete) {
					triple = new IdTriple(Variable.ANY,predicate,Variable.ANY);
				} else {
					triple = new IdTriple(Variable.ANY,predicate,object);
				}
			}
		} else {
			if (predicateIsConcrete) {
				if (objectIsConcrete) {
					triple = new IdTriple(subject,Variable.ANY,Variable.ANY);
				} else {
					triple = new IdTriple(subject, Variable.ANY, object);
				}
			} else {
				if (objectIsConcrete) {
					triple = new IdTriple(subject,predicate,Variable.ANY);
				} else {
					triple = new IdTriple(subject,predicate,object);
				}
			}
		}
		return triple;
	}
}

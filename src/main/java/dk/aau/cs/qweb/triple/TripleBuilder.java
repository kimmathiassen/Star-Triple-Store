package dk.aau.cs.qweb.triple;

import dk.aau.cs.qweb.triple.TripleStar.Variable;

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

	public TripleStar createTriple() {
		TripleStar triple;
		
		if (!subjectIsConcrete) {
			if (!predicateIsConcrete) {
				if (!objectIsConcrete) {
					triple = new TripleStar(Variable.ANY,Variable.ANY,Variable.ANY);
				} else {
					triple = new TripleStar(Variable.ANY, Variable.ANY, object);
				}
			} else {
				if (!objectIsConcrete) {
					triple = new TripleStar(Variable.ANY,predicate,Variable.ANY);
				} else {
					triple = new TripleStar(Variable.ANY,predicate,object);
				}
			}
		} else {
			if (!predicateIsConcrete) {
				if (!objectIsConcrete) {
					triple = new TripleStar(subject,Variable.ANY,Variable.ANY);
				} else {
					triple = new TripleStar(subject, Variable.ANY, object);
				}
			} else {
				if (!objectIsConcrete) {
					triple = new TripleStar(subject,predicate,Variable.ANY);
				} else {
					triple = new TripleStar(subject,predicate,object);
				}
			}
		}
		return triple;
	}
}

package dk.aau.cs.qweb.triple;

import dk.aau.cs.qweb.triple.TriplePattern.Variable;

public class TriplePatternBuilder {

	private Key subject;
	private Key predicate;
	private Key object;
	private boolean subjectIsConcrete;
	private boolean predicateIsConcrete;
	private boolean objectIsConcrete;

	public TriplePatternBuilder() {
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

	public TriplePattern createTriplePatter() {
		TriplePattern triple;
		
		if (!subjectIsConcrete) {
			if (!predicateIsConcrete) {
				if (!objectIsConcrete) {
					triple = new TriplePattern(Variable.ANY,Variable.ANY,Variable.ANY);
				} else {
					triple = new TriplePattern(Variable.ANY, Variable.ANY, object);
				}
			} else {
				if (!objectIsConcrete) {
					triple = new TriplePattern(Variable.ANY,predicate,Variable.ANY);
				} else {
					triple = new TriplePattern(Variable.ANY,predicate,object);
				}
			}
		} else {
			if (!predicateIsConcrete) {
				if (!objectIsConcrete) {
					triple = new TriplePattern(subject,Variable.ANY,Variable.ANY);
				} else {
					triple = new TriplePattern(subject, Variable.ANY, object);
				}
			} else {
				if (!objectIsConcrete) {
					triple = new TriplePattern(subject,predicate,Variable.ANY);
				} else {
					triple = new TriplePattern(subject,predicate,object);
				}
			}
		}
		return triple;
	}
}

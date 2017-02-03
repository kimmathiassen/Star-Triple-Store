package dk.aau.cs.qweb.triple;

import dk.aau.cs.qweb.triple.TriplePattern.Variable;

public class TriplePatternBuilder {

	private Key subject;
	private Key predicate;
	private Key object;
	private TriplePattern subjectTriplePattern;
	private TriplePattern objectTriplePattern;
	private boolean subjectIsConcrete;
	private boolean predicateIsConcrete;
	private boolean objectIsConcrete;
	private boolean subjectIsTriplePattern;
	private boolean objectIsTriplePattern;

	public TriplePatternBuilder() {
		subjectIsConcrete = false;
		predicateIsConcrete = false;
		objectIsConcrete = false;
		subjectIsTriplePattern = false;
		objectIsTriplePattern = false;
	}

	public void setSubject(Key i) {
		subject = i;
		subjectIsConcrete = true;
	}
	
	public void setSubject(TriplePattern i) {
		subjectTriplePattern = i;
		subjectIsConcrete = true;
		subjectIsTriplePattern = true;
	}

	public void setPredicate(Key i) {
		predicate = i;
		predicateIsConcrete = true;
	}

	public void setObject(Key i) {
		object = i;
		objectIsConcrete = true;
	}
	
	public void setObject(TriplePattern i) {
		objectTriplePattern = i;
		objectIsConcrete = true;
		objectIsTriplePattern = true;
	}

	public TriplePattern createTriplePatter() {
		if (!subjectIsConcrete) {
			if (!predicateIsConcrete) {
				if (!objectIsConcrete) {
					return new TriplePattern(Variable.ANY,Variable.ANY,Variable.ANY);
				} else {
					if (objectIsTriplePattern) {
						return new TriplePattern(Variable.ANY, Variable.ANY, objectTriplePattern);
					} else {
						return new TriplePattern(Variable.ANY, Variable.ANY, object);
					}
				}
			} else {
				if (!objectIsConcrete) {
					return new TriplePattern(Variable.ANY,predicate,Variable.ANY);
				} else {
					if (objectIsTriplePattern) {
						return new TriplePattern(Variable.ANY,predicate,objectTriplePattern);
					} else {
						return new TriplePattern(Variable.ANY,predicate,object);
					}
				}
			}
		} else {
			if (!predicateIsConcrete) {
				if (!objectIsConcrete) {
					if (subjectIsTriplePattern) {
						return new TriplePattern(subjectTriplePattern,Variable.ANY,Variable.ANY);
					} else {
						return new TriplePattern(subject,Variable.ANY,Variable.ANY);
					}
					
				} else {
					if (objectIsTriplePattern && subjectIsTriplePattern) {
						return new TriplePattern(subjectTriplePattern, Variable.ANY, objectTriplePattern);
					} else if (subjectIsTriplePattern) {
						return new TriplePattern(subjectTriplePattern, Variable.ANY, object);
					} else if (objectIsTriplePattern) {
						return new TriplePattern(subject, Variable.ANY, objectTriplePattern);
					} else {
						return new TriplePattern(subject, Variable.ANY, object);
					}
				}
			} else {
				if (!objectIsConcrete) {
					if (subjectIsTriplePattern) {
						return new TriplePattern(subjectTriplePattern,predicate,Variable.ANY);
					} else {
						return new TriplePattern(subject,predicate,Variable.ANY);
					}
				} else {
					if (objectIsTriplePattern && subjectIsTriplePattern) {
						return new TriplePattern(subjectTriplePattern,predicate,objectTriplePattern);
					} else if (subjectIsTriplePattern) {
						return new TriplePattern(subjectTriplePattern,predicate,object);
					} else if (objectIsTriplePattern) {
						return new TriplePattern(subject,predicate,objectTriplePattern);
					} else {
						return new TriplePattern(subject,predicate,object);
					}
				}
			}
		}
	}
}
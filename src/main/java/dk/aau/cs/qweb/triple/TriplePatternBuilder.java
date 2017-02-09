package dk.aau.cs.qweb.triple;

import dk.aau.cs.qweb.triple.TripleStarPattern.Variable;

public class TriplePatternBuilder {

	private Key subject;
	private Key predicate;
	private Key object;
	private TripleStarPattern subjectTriplePattern;
	private TripleStarPattern objectTriplePattern;
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
	
	public void setSubject(TripleStarPattern i) {
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
	
	public void setObject(TripleStarPattern i) {
		objectTriplePattern = i;
		objectIsConcrete = true;
		objectIsTriplePattern = true;
	}

	public TripleStarPattern createTriplePatter() {
		if (!subjectIsConcrete) {
			if (!predicateIsConcrete) {
				if (!objectIsConcrete) {
					return new TripleStarPattern(Variable.ANY,Variable.ANY,Variable.ANY);
				} else {
					if (objectIsTriplePattern) {
						return new TripleStarPattern(Variable.ANY, Variable.ANY, objectTriplePattern);
					} else {
						return new TripleStarPattern(Variable.ANY, Variable.ANY, object);
					}
				}
			} else {
				if (!objectIsConcrete) {
					return new TripleStarPattern(Variable.ANY,predicate,Variable.ANY);
				} else {
					if (objectIsTriplePattern) {
						return new TripleStarPattern(Variable.ANY,predicate,objectTriplePattern);
					} else {
						return new TripleStarPattern(Variable.ANY,predicate,object);
					}
				}
			}
		} else {
			if (!predicateIsConcrete) {
				if (!objectIsConcrete) {
					if (subjectIsTriplePattern) {
						return new TripleStarPattern(subjectTriplePattern,Variable.ANY,Variable.ANY);
					} else {
						return new TripleStarPattern(subject,Variable.ANY,Variable.ANY);
					}
					
				} else {
					if (objectIsTriplePattern && subjectIsTriplePattern) {
						return new TripleStarPattern(subjectTriplePattern, Variable.ANY, objectTriplePattern);
					} else if (subjectIsTriplePattern) {
						return new TripleStarPattern(subjectTriplePattern, Variable.ANY, object);
					} else if (objectIsTriplePattern) {
						return new TripleStarPattern(subject, Variable.ANY, objectTriplePattern);
					} else {
						return new TripleStarPattern(subject, Variable.ANY, object);
					}
				}
			} else {
				if (!objectIsConcrete) {
					if (subjectIsTriplePattern) {
						return new TripleStarPattern(subjectTriplePattern,predicate,Variable.ANY);
					} else {
						return new TripleStarPattern(subject,predicate,Variable.ANY);
					}
				} else {
					if (objectIsTriplePattern && subjectIsTriplePattern) {
						return new TripleStarPattern(subjectTriplePattern,predicate,objectTriplePattern);
					} else if (subjectIsTriplePattern) {
						return new TripleStarPattern(subjectTriplePattern,predicate,object);
					} else if (objectIsTriplePattern) {
						return new TripleStarPattern(subject,predicate,objectTriplePattern);
					} else {
						return new TripleStarPattern(subject,predicate,object);
					}
				}
			}
		}
	}
}

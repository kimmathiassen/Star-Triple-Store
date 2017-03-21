package dk.aau.cs.qweb.triple;

import java.util.Objects;

//This class is a triple and a triple pattern
public class TripleStar implements Comparable<TripleStar> {
	public Key subjectId;
	public Key predicateId;
	public Key objectId;
	
	public TripleStar(Key subject, Key predicate, Key object) {
		subjectId = subject;
		predicateId = predicate;
		objectId = object;
	}

	public String toString () {
		return "("+subjectId+", "+predicateId+", "+objectId+")";
	}
	
	@Override
    public int hashCode() {
		return Objects.hash(subjectId,predicateId,objectId);
    }
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof TripleStar) {
			TripleStar casted = (TripleStar)other;
			return (this.subjectId.equals(casted.subjectId) && this.predicateId.equals(casted.predicateId) && this.objectId.equals(casted.objectId));
		} else {
			return super.equals(other);
		}
	}

	@Override
	public int compareTo(TripleStar arg0) {
		int subject = subjectId.compareTo(arg0.subjectId);
		if (subject != 0) {
			return subject;
		} else {
			int predicate = predicateId.compareTo(arg0.predicateId);
			if (predicate != 0) {
				return predicate;
			} else {
				return (objectId.compareTo(arg0.objectId));
			}
		}
	}
}

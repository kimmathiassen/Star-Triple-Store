package dk.aau.cs.qweb.triple;

import java.util.Objects;

//This class is a triple and a triple pattern
public class TripleStar {
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
}

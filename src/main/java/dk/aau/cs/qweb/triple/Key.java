package dk.aau.cs.qweb.triple;

import java.util.Objects;

import dk.aau.cs.qweb.helper.BitHelper;

public class Key implements StarNode, Comparable<Key>{
	@SuppressWarnings("unused")
	private long maxReferenceTripleId = -5764607523034234879l;
	private long maxEmbeddedTripleId = -1152921504606846975l;
	private final long id;
	
	public Key(final long id) {
		this.id=id;
	}
	
	public long getId(){
		return id;
	}
	
	@Override
	public String toString() {
		if (id < maxEmbeddedTripleId) {
			String header = String.format("%4s",Long.toBinaryString(BitHelper.getEmbeddedHeader(id))).replace(' ', '0');
			String body = String.format("%60s",Long.toBinaryString(BitHelper.getOverflowBody(id))).replace(' ', '0');
			return header + "-"+body;
		}
		else if (id < 0) { //is embedded triple
			String header = String.format("%4s",Long.toBinaryString(BitHelper.getEmbeddedHeader(id))).replace(' ', '0');
			String subject = String.format("%20s",Long.toBinaryString(BitHelper.getEmbeddedSubject(id))).replace(' ', '0');
			String predicate = String.format("%20s",Long.toBinaryString(BitHelper.getEmbeddedPredicate(id))).replace(' ', '0');
			String object = String.format("%20s",Long.toBinaryString(BitHelper.getEmbeddedObject(id))).replace(' ', '0');
			return header + "-"+subject+"-"+predicate+"-"+object;
		}
		return Long.toBinaryString(id);
	}
	
	@Override
	public boolean equals(final Object other) {
		if (other instanceof Key) {
			Key casted = (Key)other;
			return (this.id == casted.id);
		} else {
			return super.equals(other);
		}
	}
	
	@Override
    public int hashCode() {
        return Objects.hash(id);
    }

	@Override
	public boolean isKey() {
		return true;
	}

	@Override
	public Key getKey() {
		return this;
	}

	@Override
	public boolean isEmbeddedTriplePattern() {
		return false;
	}

	@Override
	public TripleStarPattern getTriplePattern() {
		throw new IllegalArgumentException("Is not of the type TripleStarPattern");
	}

	@Override
	public boolean isVariable() {
		return false;
	}

	@Override
	public Variable getVariable() {
		throw new IllegalArgumentException("Is not of the type Variable");
	}

	@Override
	public boolean isConcrete() {
		return true;
	}

	@Override
	public int compareTo(Key other) {
		if (BitHelper.isIdAnEmbeddedTriple(id) && BitHelper.isIdAnEmbeddedTriple(other.id)) {
			
			long subject = Long.compare( BitHelper.getEmbeddedSubject(id), BitHelper.getEmbeddedSubject(other.getId()));
			if (subject != 0) {
				return (int)subject;
			} else {
				long predicate = Long.compare( BitHelper.getEmbeddedPredicate(id), BitHelper.getEmbeddedPredicate(other.getId()));
				if (predicate != 0) {
					return (int)predicate;
				} else {
					return (int) Long.compare( BitHelper.getEmbeddedObject(id), BitHelper.getEmbeddedObject(other.getId()));
				}
			}
		} else if (BitHelper.isIdAnEmbeddedTriple(id)) {
			return 1;
		} else if (BitHelper.isIdAnEmbeddedTriple(other.id)) {
			return -1;
		} else {
			return (int) (this.id - other.id);
		}
	}
}

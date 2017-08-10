package dk.aau.cs.qweb.triple;

import java.util.Objects;

import dk.aau.cs.qweb.helper.BitHelper;

public class Key implements StarElement, Comparable<Key>{
	private long maxReferenceTripleId = -3458764513820540929l; // 1100-111111111111111111111111111111111111111111111111111111111111 
	private long minReferenceTripleId = -4611686018427387904l; // 1100-000000000000000000000000000000000000000000000000000000000000
	private long maxEmbeddedTripleId  = -8070450532247928833l; // 1000-11111111111111111111-11111111111111111111-11111111111111111111
	private long minEmbeddedTripleId  = -9223372036854775808l; // 1000-00000000000000000000-00000000000000000000-00000000000000000000
	private final long id;
	
	public Key(final long id) {
		this.id=id;
	}
	
	public long getId(){
		return id;
	}
	
	@Override
	public String toString() {
		if (id > minReferenceTripleId && id < maxReferenceTripleId) {
			String header = String.format("%4s",Long.toBinaryString(BitHelper.getEmbeddedHeader(id))).replace(' ', '0');
			String body = Long.toBinaryString(BitHelper.getOverflowBody(id));
			return header + "-"+body;
		}
		else if (id > minEmbeddedTripleId && id < maxEmbeddedTripleId) { //is embedded triple
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

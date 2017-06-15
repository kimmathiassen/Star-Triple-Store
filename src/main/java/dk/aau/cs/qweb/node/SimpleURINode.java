package dk.aau.cs.qweb.node;

import dk.aau.cs.qweb.dictionary.PrefixDictionary;

public class SimpleURINode extends SimpleNode {
	
	private int urlBodyId;
	private boolean hasPrefix = false;

	protected SimpleURINode(String label) {
		super(label);
	}
	
	public SimpleURINode(int bodyId, String head) {
		super(head);
		urlBodyId = bodyId;
		hasPrefix = true;
	}

	@Override
	public boolean isURI() {
		return true;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof SimpleURINode) {
			SimpleURINode other = (SimpleURINode) o;
			return urlBodyId == other.urlBodyId && label.equals(other.label);
		}
		return false;
	}
	
	@Override 
	public String getURI() {
		if (hasPrefix) {
			String prefix = PrefixDictionary.getInstance().getPrefix(urlBodyId);
			return prefix+label;
		}
		return (String) label;
	}
	
	@Override 
	public String toString() {
		return getURI();
	}
}

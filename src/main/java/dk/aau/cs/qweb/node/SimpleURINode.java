package dk.aau.cs.qweb.node;

import dk.aau.cs.qweb.dictionary.PrefixDictionary;

public class SimpleURINode extends SimpleNode {
	
	private int urlBody;

	protected SimpleURINode(String label) {
		super(label);
	}
	
	public SimpleURINode(int body, String head) {
		super(head);
		urlBody = body;
	}

	@Override
	public boolean isURI() {
		return true;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof SimpleURINode) {
			SimpleURINode other = (SimpleURINode) o;
			return urlBody == other.urlBody && label.equals(other.label);
		}
		return false;
	}
	
	@Override 
	public String getURI() {
		String prefix = PrefixDictionary.getInstance().getPrefix(urlBody);
		return prefix+label;
	}
	
	@Override 
	public String toString() {
		return getURI();
	}
}

package dk.aau.cs.qweb.node;

public class SimpleLiteralNode extends SimpleNode {

	protected SimpleLiteralNode(String label) {
		super(label);
	}

	@Override
	public boolean isLiteral() {
		return true;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof SimpleLiteralNode) {
			SimpleLiteralNode other = (SimpleLiteralNode) o;
			return label.equals(other.label);
		}
		return false;
	}
	
	@Override
	public String getLiteralLexicalForm() {
		return (String)label;
	}
}

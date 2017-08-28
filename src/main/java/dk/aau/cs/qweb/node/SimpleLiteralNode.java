package dk.aau.cs.qweb.node;

/**
 * Literal Node
 *
 */
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
	
	@Override
	public String toString() {
		return getLiteralLexicalForm();
	}
	
	@Override
	public String serialize() {
		return "L"+toString();
	}
}

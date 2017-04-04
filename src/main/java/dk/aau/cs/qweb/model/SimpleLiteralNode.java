package dk.aau.cs.qweb.model;

public class SimpleLiteralNode extends SimpleNode {

	protected SimpleLiteralNode(String label) {
		super(label);
	}

	@Override
	public boolean isLiteral() {
		return true;
	}
}

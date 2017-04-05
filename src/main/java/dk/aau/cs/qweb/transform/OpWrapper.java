package dk.aau.cs.qweb.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.jena.graph.Triple;
import org.apache.jena.reasoner.IllegalParameterException;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.OpExtend;
import org.apache.jena.sparql.algebra.op.OpJoin;
import org.apache.jena.sparql.algebra.op.OpTriple;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.nodevalue.NodeValueNode;

import dk.aau.cs.qweb.dictionary.NodeDictionary;
import dk.aau.cs.qweb.helper.BitHelper;
import dk.aau.cs.qweb.node.Node_Triple;

public class OpWrapper {
	
	private List<Var> variables = new ArrayList<Var>();
	private int selectivity = 0;
	private Op op;
	private boolean isOpExtend= false;

	public Op asOp() {
		return op;
	}

	public OpWrapper(Op op) {
		this.op = op;
		
		if (op instanceof OpTriple) {
			calculateSelectivity((OpTriple) op);
			extractVariables((OpTriple) op);
		} else if (op instanceof OpJoin) {
			extractVariables((OpJoin) op);
		} else if (op instanceof OpExtend) {
			calculateSelectivity((OpExtend) op);
			extractVariables((OpExtend) op);
			isOpExtend = true;
		} else {
			throw new NotImplementedException("support of op "+op.getClass()+" has not been implemented");
		}
	}
	
	public boolean isTripleOverflown() {
		Triple triple =  getTriple(op);
		NodeDictionary dict = NodeDictionary.getInstance();
		if (triple != null) {
			if(triple.getSubject() instanceof Node_Triple) {
				if (BitHelper.isOverflownEmbeddedTriple(dict.createKey(triple.getSubject()))) {
					return true;
				}
			}
			
			if(triple.getObject() instanceof Node_Triple) {
				if (BitHelper.isOverflownEmbeddedTriple(dict.createKey(triple.getObject()))) {
					return true;
				}
			}
		}
		return false;
	}
	
	private Triple getTriple(Op op) {
		if (op instanceof OpTriple) {
			OpTriple triple = (OpTriple)op;
			return triple.getTriple();
		} else if (op instanceof OpJoin) {
			return null;
		} else if (op instanceof OpExtend) {
			OpExtend bind = (OpExtend)op;
			Collection<Expr> expressions = bind.getVarExprList().getExprs().values();
			if (expressions.size() != 1) {
				throw new IllegalArgumentException("OpExtend contains multiple or zero expressions, expected one: "+op);
			}
			for (Expr expr : expressions) {
				//This only works for embedded triples
				NodeValueNode node = (NodeValueNode) expr;
				Node_Triple t = (Node_Triple) node.getNode();
				return new Triple(t.getSubject(),t.getPredicate(),t.getObject());
			}
			return null; //Can never be called
		} else {
			return null;
		}
	}

	private void calculateSelectivity(OpTriple op) {
		selectivity = SelectivityMap.getSelectivityScore(op.getTriple());
	}

	private void calculateSelectivity(OpExtend op) {
		Collection<Expr> expressions = op.getVarExprList().getExprs().values();
		if (expressions.size() != 1) {
			throw new IllegalArgumentException("OpExtend contains multiple or zero expressions, expected one: "+op);
		}
		for (Expr expr : expressions) {
			NodeValueNode node = (NodeValueNode) expr;
			Node_Triple t = (Node_Triple) node.getNode();
			selectivity = SelectivityMap.getSelectivityScore(new Triple(t.getSubject(),t.getPredicate(),t.getObject()));
		}
	}

	private void extractVariables(OpTriple op) {
		Triple triple = op.getTriple();
		if (!triple.getSubject().isConcrete()) {
			variables.add((Var)triple.getSubject());
		}
		if (!triple.getPredicate().isConcrete()) {
			variables.add((Var)triple.getPredicate());
		}
		if (!triple.getObject().isConcrete()) {
			variables.add((Var)triple.getObject());
		}
	}
	
	private void extractVariables(OpExtend op) {
		variables.addAll(op.getVarExprList().getVars());
	}

	private void extractVariables(OpJoin op) {
		OpJoin join = (OpJoin)op;
		Op left = join.getLeft();
		if (left instanceof OpJoin) {
			extractVariables((OpJoin) left);
		} else if (left instanceof OpTriple) {
			extractVariables((OpTriple) left);
		} else if (left instanceof OpExtend) {
			extractVariables((OpExtend)left);
		} else {
			throw new NotImplementedException("support of op "+left.getClass()+" has not been implemented");
		}
		
		if (join.getRight() instanceof OpExtend) {
			extractVariables((OpExtend) join.getRight());
		} else if (join.getRight() instanceof OpTriple) {
			extractVariables((OpTriple) join.getRight());
		} else {
			throw new IllegalParameterException("Right leaf node must either an OpTriple or OpExtend, was "+join.getRight().toString());
		}
	}

	public int getSelectivity() {
		return selectivity;
	}

	public List<Var> getVariables() {
		return variables;
	}
	
	@Override
	public String toString() {
		return op.toString();
	}

	public boolean onlyContainsVariables() {
		return variables.size() == 3 ? true : false;
	}

	public boolean isOpExtend() {
		return isOpExtend;
	}
}

package dk.aau.cs.qweb.Transform;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.OpExtend;
import org.apache.jena.sparql.algebra.op.OpJoin;
import org.apache.jena.sparql.algebra.op.OpTriple;
import org.apache.jena.sparql.core.Var;

public class OpWrapper {
	
	private List<Var> variables = new ArrayList<Var>();
	private int selectivity = 0;
	private Op op;

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
		} else {
			throw new NotImplementedException("support of op "+op.getClass()+" has not been implemented");
		}
	}

	private void calculateSelectivity(OpTriple op) {
		selectivity = SelectivityMap.getSelectivityScore(op.getTriple());
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
	
	private void extractVariables(OpJoin op) {
		OpJoin join = (OpJoin)op;
		Op left = join.getLeft();
		if (left instanceof OpJoin) {
			extractVariables((OpJoin) left);
		} else if (left instanceof OpTriple) {
			extractVariables((OpTriple) left);
		} else if (left instanceof OpExtend) {
			throw new NotImplementedException("support for OpExtend not yet implemebnted");
			//extractVariables((OpAssign) left);
		} else {
			throw new NotImplementedException("support of op "+left.getClass()+" has not been implemented");
		}
		extractVariables((OpTriple) join.getRight());
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
}

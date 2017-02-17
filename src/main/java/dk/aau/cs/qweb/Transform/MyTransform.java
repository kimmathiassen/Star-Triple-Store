package dk.aau.cs.qweb.Transform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.TransformCopy;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpJoin;
import org.apache.jena.sparql.algebra.op.OpTriple;
import org.apache.jena.sparql.core.Var;

public class MyTransform extends TransformCopy {

	@Override
	public Op transform(OpBGP opBGP) {
		Op op = createJoinTree(opBGP);
		return op;
	}

	private Op createJoinTree(Op op) {
		List<OpWrapper> triplePatterns = addToList(op);
		
		OpWrapper tree = getOpWithHighestSelectivity(triplePatterns);
		while(triplePatterns.size() > 0) {
			tree = opJoin(tree,getOpWithHighestSelectivityThatJoins(tree,triplePatterns));
		}
		return tree.asOp();
	}

	private OpWrapper opJoin(OpWrapper left, OpWrapper right) {
		return new OpWrapper(OpJoin.create(left.asOp(),right.asOp()));
	}

	private OpWrapper getOpWithHighestSelectivityThatJoins(OpWrapper tree,List<OpWrapper> triplePatterns) {
		List<Var> variables = new ArrayList<Var>();
		OpWrapper highestSelectivity = null;
		variables.addAll(tree.getVariables());
		for (OpWrapper opWrapper : triplePatterns) {
			if (!Collections.disjoint(variables,opWrapper.getVariables())) {
				if (highestSelectivity == null) {
					highestSelectivity = opWrapper;
				}
				if (opWrapper.getSelectivity() > highestSelectivity.getSelectivity()) {
					highestSelectivity = opWrapper;
				}
			}
		}
		triplePatterns.remove(highestSelectivity);
		return highestSelectivity;
	}

	private OpWrapper getOpWithHighestSelectivity(List<OpWrapper> triplePatterns) {
		OpWrapper highestSelectivity = triplePatterns.get(0);
		for (OpWrapper opWrapper : triplePatterns) {
			if (opWrapper.getSelectivity() > highestSelectivity.getSelectivity()) {
				highestSelectivity = opWrapper;
			}
		}
		triplePatterns.remove(highestSelectivity);
		return highestSelectivity;
	}

	private List<OpWrapper> addToList(Op op) {
		List<OpWrapper> wrappedOp = new ArrayList<OpWrapper>();
		if (op instanceof OpBGP) {
			for (Op element : splitBGP(op)) {
				wrappedOp.add(new OpWrapper(element));
			}
		}
		
		//TODO add cases for other the OpBGP
		return wrappedOp;
	}

	private List<Op> splitBGP(Op op) {
		List<Op> result = new ArrayList<Op>();
		OpBGP bgp = (OpBGP)op;
		for (Triple triple : bgp.getPattern()) {
			result.add(new OpTriple(triple));
		}
		return result;
	}


}

package dk.aau.cs.qweb.transform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.TransformCopy;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpExtend;
import org.apache.jena.sparql.algebra.op.OpJoin;
import org.apache.jena.sparql.algebra.op.OpSequence;
import org.apache.jena.sparql.algebra.op.OpTable;
import org.apache.jena.sparql.algebra.op.OpTriple;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.nodevalue.NodeValueNode;

import dk.aau.cs.qweb.dictionary.VarDictionary;
import dk.aau.cs.qweb.node.Node_Triple;

public class MyTransform extends TransformCopy {

	@Override
	public Op transform(OpBGP opBGP) {
		Op op = createJoinTree(opBGP);
	//	System.out.println(op);
		return op;
	}
	
	@Override
    public Op transform(OpSequence opSequence, List<Op> elts) {
		Op op = createJoinTree(opSequence);
		return op;
    }
	
	private List<Op> splitTripleWithEmbeddedTriple(Triple triple) {
		List<Op> split = new ArrayList<>();
		Node subject = triple.getSubject();
		Node predicate = triple.getPredicate();
		Node object = triple.getObject();
		if (triple.getSubject() instanceof Node_Triple) {
			
			Node_Triple s = (Node_Triple)triple.getSubject();
			NodeValueNode exp = new NodeValueNode(s);
			VarDictionary varDict = VarDictionary.getInstance();
			
			subject = varDict.getFreshVariable();
			split.add(OpExtend.create(OpTable.empty() , (Var) subject, exp));
		} 
		if (triple.getObject() instanceof Node_Triple) {
			Node_Triple o = (Node_Triple)triple.getObject();
			NodeValueNode exp = new NodeValueNode(o);
			VarDictionary varDict = VarDictionary.getInstance();
			
			object = varDict.getFreshVariable();
			split.add(OpExtend.create(OpTable.empty() , (Var) object, exp));
		}
		split.add(new OpTriple(new Triple(subject,predicate,object)));
		
		return split;
	}

	private boolean containsEmbeddedTriple(Triple triple) {
		if (triple.getSubject() instanceof Node_Triple) {
			return true;
		} else if (triple.getObject() instanceof Node_Triple) {
			return true;
		} else {
			return false;
		}
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
				if (compareSelectivity(highestSelectivity, opWrapper)) {
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
			if (compareSelectivity(highestSelectivity, opWrapper)) {
				highestSelectivity = opWrapper;
			}
		}
		triplePatterns.remove(highestSelectivity);
		return highestSelectivity;
	}

	private boolean compareSelectivity(OpWrapper highestSelectivity, OpWrapper opWrapper) {
		//True means that opWrapper is higher
		if (opWrapper.getSelectivity() == highestSelectivity.getSelectivity()) {
			if (opWrapper.isTripleOverflown() || highestSelectivity.isTripleOverflown()) {
				return opWrapper.isTripleOverflown() ? false : true;
			}
			if (opWrapper.onlyContainsVariables() && highestSelectivity.onlyContainsVariables()) {
				
				if (opWrapper.isOpExtend()) {
					return true;
				} else if (highestSelectivity.isOpExtend()) {
					return false;
				}
			}
		}
		
		return opWrapper.getSelectivity() > highestSelectivity.getSelectivity();
	}

	private List<OpWrapper> addToList(Op op) {
		List<OpWrapper> wrappedOp = new ArrayList<OpWrapper>();
		if (op instanceof OpBGP) {
			for (Op element : splitBGP(op)) {
				wrappedOp.add(new OpWrapper(element));
			}
		} else if (op instanceof OpSequence) {
			for (Op element : splitSequence(op)) {
				wrappedOp.add(new OpWrapper(element));
			}
		} else if (op instanceof OpExtend) {
			for (Op element : splitExtend(op)) {
				wrappedOp.add(new OpWrapper(element));
			}
		} else {
			throw new NotImplementedException("no support of "+op.getName());
		}
		
		return wrappedOp;
	}

	private List<Op> splitSequence(Op op) {
		List<Op> result = new ArrayList<Op>();
		OpSequence sequence = (OpSequence)op;
		for (Op element : sequence.getElements()) {
			if (element instanceof OpBGP) {
				result.addAll(splitBGP(element));
			} else if (element instanceof OpExtend) {
				result.addAll(splitExtend(element));
			} else {
				throw new NotImplementedException("no support for "+element.getName());
			}
		}
		return result;
	}

	private List<Op>  splitExtend(Op op) {
		List<Op> result = new ArrayList<Op>();
		OpExtend extend = (OpExtend)op;
		
		for (Entry<Var, Expr> element : extend.getVarExprList().getExprs().entrySet()) {
			result.add(OpExtend.create(OpTable.empty() , element.getKey(), element.getValue()));
		}
		return result;
	}

	private List<Op> splitBGP(Op op) {
		List<Op> result = new ArrayList<Op>();
		OpBGP bgp = (OpBGP)op;
		for (Triple triple : bgp.getPattern()) {
			if (containsEmbeddedTriple(triple)) {
				result.addAll(splitTripleWithEmbeddedTriple(triple));
			} else {
				result.add(new OpTriple(triple));
			}
		}
		return result;
	}


}

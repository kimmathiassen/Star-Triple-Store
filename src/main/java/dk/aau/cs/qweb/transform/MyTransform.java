package dk.aau.cs.qweb.transform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.TransformCopy;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpExtend;
import org.apache.jena.sparql.algebra.op.OpJoin;
import org.apache.jena.sparql.algebra.op.OpTable;
import org.apache.jena.sparql.algebra.op.OpTriple;
import org.apache.jena.sparql.core.Var;
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
	
//	@Override
//	public Op transform(OpTriple optriple) {
//		Op op = optriple;
//		
//		if (containsEmbeddedTriple(optriple)) {
//			List<OpWrapper> elements = splitTripleWithEmbeddedTriple(optriple);
//			OpWrapper tree = getOpWithHighestSelectivity(elements);
//			while (elements.size()>0) {
//				OpWrapper rightJoin = getOpWithHighestSelectivityThatJoins(tree,elements);
//				tree = opJoin(tree, rightJoin);
//			}
//			return tree.asOp();
//		}
//		return op;
//	}
	
//	@Override
//    public Op transform(OpSequence opSequence, List<Op> elts) {
//		
//		
//		OpWrapper opExtend = null;
//		for (Op op : opSequence.getElements()) {
//			if (op instanceof OpExtend) {
//				if (opExtend == null) {
//					opExtend = new OpWrapper(op);
//				} else { 
//					OpWrapper temp = new OpWrapper(op);
//					opExtend = temp.getSelectivity() > opExtend.getSelectivity() ? temp : opExtend;
//					
//				}
//				return mergeExtendAndBGP(opExtend,opSequence);
//			}
//		}
//		return opSequence;
//    }
	
//	@Override
//    public Op transform(OpExtend opExtend, Op subOp) {
//		System.out.println(opExtend);
//		System.out.println(subOp);
//		return null;
//	}

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

//	private OpSequence mergeExtendAndBGP(OpExtend opExtend, OpSequence opSequence) {
//		System.out.println(opExtend.getVarExprList());
//		for (Op op : opSequence.getElements()) {
//			if (op instanceof OpBGP) {
//				List<OpWrapper> opWrappers = addToList(op);
//			}
//		}
//		// TODO Auto-generated method stub
//		return null;
//	}



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
		}
		
		//TODO add cases for other the OpBGP
		return wrappedOp;
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

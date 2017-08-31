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
import dk.aau.cs.qweb.node.EmbeddedNode;

/**
 * The class contains the logic for the query optimization.
 *
 */
public class MyTransform extends TransformCopy {

	@Override
	public Op transform(OpBGP opBGP) {
		Op op = createJoinTree(opBGP);
		//print(op,0);
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
		if (triple.getSubject() instanceof EmbeddedNode) {
			
			EmbeddedNode s = (EmbeddedNode)triple.getSubject();
			NodeValueNode exp = new NodeValueNode(s);
			VarDictionary varDict = VarDictionary.getInstance();
			
			subject = varDict.getFreshVariable();
			split.add(OpExtend.create(OpTable.empty() , (Var) subject, exp));
		} 
		if (triple.getObject() instanceof EmbeddedNode) {
			EmbeddedNode o = (EmbeddedNode)triple.getObject();
			NodeValueNode exp = new NodeValueNode(o);
			VarDictionary varDict = VarDictionary.getInstance();
			
			object = varDict.getFreshVariable();
			split.add(OpExtend.create(OpTable.empty() , (Var) object, exp));
		}
		split.add(new OpTriple(new Triple(subject,predicate,object)));
		
		return split;
	}

	private boolean containsEmbeddedTriple(Triple triple) {
		if (triple.getSubject() instanceof EmbeddedNode) {
			return true;
		} else if (triple.getObject() instanceof EmbeddedNode) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method create a left based join tree based on some heuristics. 
	 * It is created in a bottom up fashion.
	 */
	private Op createJoinTree(Op op) {
		List<OpWrapper> unusedOpwrappers = addToList(op);
		OpWrapper headOfTree = getOpWithHighestSelectivity(unusedOpwrappers);;
		
		while(containsJoin(headOfTree,unusedOpwrappers)) {
			headOfTree = opJoin(headOfTree,getOpWithHighestSelectivityThatJoins(headOfTree,unusedOpwrappers));
		}
		
		//handle case if there are opWrappers that do join with the tree. In this case a opSequence is created.
		while (unusedOpwrappers.size() > 0) {
			Op seq = OpSequence.create(headOfTree.asOp(), unusedOpwrappers.get(0).asOp());
			headOfTree = new OpWrapper(seq);
			unusedOpwrappers.remove(0);
		}
		
		return headOfTree.asOp();
	}

	/**
	 * Return true iff there exist at least two OPs that share a variable.
	 */
	private boolean containsJoin(OpWrapper op, List<OpWrapper> opWrappers) {
		List<Var> variables = new ArrayList<Var>();
		variables.addAll(op.getVariables());
		
		for (OpWrapper opWrapper : opWrappers) {
			if (!Collections.disjoint(variables,opWrapper.getVariables())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Create a join between the opWrappers
	 * @param The old treeHead should be the left.
	 * @param The new opWrapper should be right.
	 * @return A OpJoin that is head of the tree
	 */
	private OpWrapper opJoin(OpWrapper left, OpWrapper right) {
		return new OpWrapper(OpJoin.create(left.asOp(),right.asOp()));
	}

	/**
	 * The op with the highest selectivity (that joins) is removed from the list and returned. 
	 * 
	 * @param operatorTree, used to find join candidates
	 * @param opWrappers, list of unused opwrappers.
	 * @return The opwrapper to be added to the operator tree.
	 */
	private OpWrapper getOpWithHighestSelectivityThatJoins(OpWrapper operatorTree,List<OpWrapper> opWrappers) {
		List<Var> variables = new ArrayList<Var>();
		OpWrapper highestSelectivity = null;
		variables.addAll(operatorTree.getVariables());
		for (OpWrapper opWrapper : opWrappers) {
			if (!Collections.disjoint(variables,opWrapper.getVariables())) {
				if (highestSelectivity == null) {
					highestSelectivity = opWrapper;
				}
				if (compareSelectivity(highestSelectivity, opWrapper)) {
					highestSelectivity = opWrapper;
				}
			}
		}
		opWrappers.remove(highestSelectivity);
		return highestSelectivity;
	}

	/**
	 * Traverse the list of OpWappers and find the element with the highest selectivity, see {@link SelectivityMap}
	 * That element is removed from the list and returned.
	 */
	private OpWrapper getOpWithHighestSelectivity(List<OpWrapper> opWrappers) {
		OpWrapper highestSelectivity = opWrappers.get(0);
		for (OpWrapper opWrapper : opWrappers) {
			if (compareSelectivity(highestSelectivity, opWrapper)) {
				highestSelectivity = opWrapper;
			}
		}
		opWrappers.remove(highestSelectivity);
		return highestSelectivity;
	}

	/**
	 * Method for comparing the selectivty of two opWrappers
	 * 
	 * @param defender
	 * @param opposer
	 * @return return true if the selectivity of the opposer is higher than the defender. If equals 
	 * two cases are considered. 
	 * OPs without reference nodes are preferred. (reference nodes are more expensive to process than non-reference nodes, thus the fewer that better)
	 * If both only contains varialbes e.g.  bind(?s, ?p, ?o) as ?b
	 * Then the bind operation is preferred (OpExtend). (Bind have more variables than triple patterns, thus better joins can potentialy be selected from this point on. )
	 */
	private boolean compareSelectivity(OpWrapper defender, OpWrapper opposer) {
		//True means that opposer is higher
		if (opposer.getSelectivity() == defender.getSelectivity()) {
			
			if (opposer.isOpExtend()) {
				return true;
			} else if (defender.isOpExtend()) {
				return false;
			}
			
			if (opposer.containsReferenceNode() || defender.containsReferenceNode()) {
				return opposer.containsReferenceNode() ? false : true;
			}
		}
		
		return opposer.getSelectivity() > defender.getSelectivity();
	}

	/**
	 * Create a list of OpWrappers
	 */
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

	/**
	 * Method for traversing the query operator and printing it.
	 * This is not very well polished method.
	 * Alternativly the standard printer could be updated to support embedded triple patterns.
	 * This is a recursive function. 
	 * @param The head operator
	 * @param depth of the tree.
	 */
	private void print(Op op, int depth) {
		if (op instanceof OpExtend) {
			for (Op element : splitExtend(op)) {
				OpExtend extend = (OpExtend)element;
				
				for (Entry<Var, Expr> entry : extend.getVarExprList().getExprs().entrySet()) {
					System.out.print(insertDepth(depth));
					System.out.print( "Bind: "+ entry.getKey()+" ");
					NodeValueNode node = (NodeValueNode) entry.getValue();
					EmbeddedNode t = (EmbeddedNode) node.getNode();
					System.out.println("<"+t.getSubject()+" "+t.getPredicate()+" "+t.getObject()+">");
				}
			}
		} else if (op instanceof OpJoin) {
			System.out.print(insertDepth(depth));
			System.out.println("Join: \n");
			depth++;
			print(((OpJoin) op).getRight(),depth);
			print(((OpJoin) op).getLeft(),depth);
		} else if (op instanceof OpTriple) {
			OpTriple triple = (OpTriple)op;
			System.out.print(insertDepth(depth));
			System.out.print("Triple: "+ triple.getTriple().getSubject()+" ");
			System.out.print(triple.getTriple().getPredicate()+" ");
			System.out.println(triple.getTriple().getObject());
		} else if (op instanceof OpSequence) {
			System.out.print(insertDepth(depth));
			System.out.print("Sequence: \n");
			depth++;
			for (Op element : ((OpSequence) op).getElements()) {
				if (element instanceof OpJoin) {
					print((OpJoin) element,depth);
				} else if (element instanceof OpTriple) {
					print((OpTriple) element,depth);
				} else if (element instanceof OpExtend) {
					print((OpExtend)element,depth);
				} else if (element instanceof OpSequence) {
					print((OpSequence)element,depth);
				} else {
					throw new NotImplementedException("support of op "+element.getClass()+" has not been implemented");
				}
			}
		}
		else {
			throw new NotImplementedException("no support of "+op.getName());
		}
	}

	private String insertDepth(int depth) {
		return String.join("", Collections.nCopies(depth, "  "));
	}
	
}

package dk.aau.cs.qweb.transform;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.op.OpExtend;
import org.apache.jena.sparql.algebra.op.OpProject;
import org.apache.jena.sparql.algebra.op.OpTriple;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.core.VarExprList;
import org.apache.jena.sparql.expr.Expr;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dk.aau.cs.qweb.main.queryparser.SyntaxStar;

public class BindOperator {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void singleBindEmbeddedTriplePattern() {
//		String BASE = "http://example/" ; 
//        Var var_t = Var.alloc("t") ;
//        Var var_o = Var.alloc("o") ;
//        Op triple = new OpTriple(new Triple(var_t, NodeFactory.createURI(BASE+"p"), var_o)) ;
//        
//        Expr expr = new Triple(NodeFactory.createURI(BASE+"p"), NodeFactory.createURI(BASE+"p"), NodeFactory.createURI(BASE+"p"));
//        
//        
//        VarExprList bindVar = new VarExprList();
//        bindVar.add(var_t);
//        Op extend = OpExtend.create(triple,bindVar,embeddedTriple);
//
//        List<Var> projectVars = new ArrayList<Var>();
//        projectVars.add(var_o);
//        Op optimalOp = new OpProject(extend,projectVars);
        
        
//        String queryString = "SELECT ?o WHERE {Bind (<<<"+BASE+"s> <"+BASE+"p> <"+BASE+"o>>> as ?t) . ?t <"+BASE+"p1> <"+BASE+"o2> }" ;
//        
//        Query query = QueryFactory.create(queryString,SyntaxStar.syntaxSPARQL_Star) ;
//        
//        
//		fail("Not yet implemented");
	}

}

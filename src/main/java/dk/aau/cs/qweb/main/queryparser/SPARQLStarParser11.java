package dk.aau.cs.qweb.main.queryparser;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Query;
import org.apache.jena.riot.system.RiotLib;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.E_Add;
import org.apache.jena.sparql.expr.E_BNode;
import org.apache.jena.sparql.expr.E_Bound;
import org.apache.jena.sparql.expr.E_Coalesce;
import org.apache.jena.sparql.expr.E_Conditional;
import org.apache.jena.sparql.expr.E_Datatype;
import org.apache.jena.sparql.expr.E_DateTimeDay;
import org.apache.jena.sparql.expr.E_DateTimeHours;
import org.apache.jena.sparql.expr.E_DateTimeMinutes;
import org.apache.jena.sparql.expr.E_DateTimeMonth;
import org.apache.jena.sparql.expr.E_DateTimeSeconds;
import org.apache.jena.sparql.expr.E_DateTimeTZ;
import org.apache.jena.sparql.expr.E_DateTimeTimezone;
import org.apache.jena.sparql.expr.E_DateTimeYear;
import org.apache.jena.sparql.expr.E_Divide;
import org.apache.jena.sparql.expr.E_Equals;
import org.apache.jena.sparql.expr.E_Function;
import org.apache.jena.sparql.expr.E_GreaterThan;
import org.apache.jena.sparql.expr.E_GreaterThanOrEqual;
import org.apache.jena.sparql.expr.E_IRI;
import org.apache.jena.sparql.expr.E_IsBlank;
import org.apache.jena.sparql.expr.E_IsIRI;
import org.apache.jena.sparql.expr.E_IsLiteral;
import org.apache.jena.sparql.expr.E_IsNumeric;
import org.apache.jena.sparql.expr.E_IsURI;
import org.apache.jena.sparql.expr.E_Lang;
import org.apache.jena.sparql.expr.E_LangMatches;
import org.apache.jena.sparql.expr.E_LessThan;
import org.apache.jena.sparql.expr.E_LessThanOrEqual;
import org.apache.jena.sparql.expr.E_LogicalAnd;
import org.apache.jena.sparql.expr.E_LogicalNot;
import org.apache.jena.sparql.expr.E_LogicalOr;
import org.apache.jena.sparql.expr.E_MD5;
import org.apache.jena.sparql.expr.E_Multiply;
import org.apache.jena.sparql.expr.E_NotEquals;
import org.apache.jena.sparql.expr.E_NotOneOf;
import org.apache.jena.sparql.expr.E_Now;
import org.apache.jena.sparql.expr.E_NumAbs;
import org.apache.jena.sparql.expr.E_NumCeiling;
import org.apache.jena.sparql.expr.E_NumFloor;
import org.apache.jena.sparql.expr.E_NumRound;
import org.apache.jena.sparql.expr.E_OneOf;
import org.apache.jena.sparql.expr.E_Random;
import org.apache.jena.sparql.expr.E_Regex;
import org.apache.jena.sparql.expr.E_SHA1;
import org.apache.jena.sparql.expr.E_SHA256;
import org.apache.jena.sparql.expr.E_SHA384;
import org.apache.jena.sparql.expr.E_SHA512;
import org.apache.jena.sparql.expr.E_SameTerm;
import org.apache.jena.sparql.expr.E_Str;
import org.apache.jena.sparql.expr.E_StrAfter;
import org.apache.jena.sparql.expr.E_StrBefore;
import org.apache.jena.sparql.expr.E_StrConcat;
import org.apache.jena.sparql.expr.E_StrContains;
import org.apache.jena.sparql.expr.E_StrDatatype;
import org.apache.jena.sparql.expr.E_StrEncodeForURI;
import org.apache.jena.sparql.expr.E_StrEndsWith;
import org.apache.jena.sparql.expr.E_StrLang;
import org.apache.jena.sparql.expr.E_StrLength;
import org.apache.jena.sparql.expr.E_StrLowerCase;
import org.apache.jena.sparql.expr.E_StrReplace;
import org.apache.jena.sparql.expr.E_StrStartsWith;
import org.apache.jena.sparql.expr.E_StrSubstring;
import org.apache.jena.sparql.expr.E_StrUUID;
import org.apache.jena.sparql.expr.E_StrUpperCase;
import org.apache.jena.sparql.expr.E_Subtract;
import org.apache.jena.sparql.expr.E_URI;
import org.apache.jena.sparql.expr.E_UUID;
import org.apache.jena.sparql.expr.E_UnaryMinus;
import org.apache.jena.sparql.expr.E_UnaryPlus;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.aggregate.AggregateRegistry;
import org.apache.jena.sparql.expr.aggregate.Aggregator;
import org.apache.jena.sparql.expr.aggregate.AggregatorFactory;
import org.apache.jena.sparql.expr.aggregate.Args;
import org.apache.jena.sparql.graph.NodeConst;
import org.apache.jena.sparql.lang.SPARQLParserBase;
import org.apache.jena.sparql.lang.sparql_11.JavaCharStream;
import org.apache.jena.sparql.lang.sparql_11.ParseException;
import org.apache.jena.sparql.lang.sparql_11.SPARQLParser11Constants;
import org.apache.jena.sparql.lang.sparql_11.SPARQLParser11TokenManager;
import org.apache.jena.sparql.lang.sparql_11.Token;
import org.apache.jena.sparql.modify.request.QuadAcc;
import org.apache.jena.sparql.modify.request.QuadAccSink;
import org.apache.jena.sparql.modify.request.QuadDataAccSink;
import org.apache.jena.sparql.modify.request.Target;
import org.apache.jena.sparql.modify.request.UpdateAdd;
import org.apache.jena.sparql.modify.request.UpdateClear;
import org.apache.jena.sparql.modify.request.UpdateCopy;
import org.apache.jena.sparql.modify.request.UpdateCreate;
import org.apache.jena.sparql.modify.request.UpdateDeleteWhere;
import org.apache.jena.sparql.modify.request.UpdateDrop;
import org.apache.jena.sparql.modify.request.UpdateLoad;
import org.apache.jena.sparql.modify.request.UpdateModify;
import org.apache.jena.sparql.modify.request.UpdateMove;
import org.apache.jena.sparql.modify.request.UpdateWithUsing;
import org.apache.jena.sparql.path.P_Link;
import org.apache.jena.sparql.path.P_NegPropSet;
import org.apache.jena.sparql.path.P_Path0;
import org.apache.jena.sparql.path.P_ReverseLink;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.path.PathFactory;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementBind;
import org.apache.jena.sparql.syntax.ElementData;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementMinus;
import org.apache.jena.sparql.syntax.ElementNamedGraph;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementService;
import org.apache.jena.sparql.syntax.ElementSubQuery;
import org.apache.jena.sparql.syntax.ElementUnion;
import org.apache.jena.sparql.syntax.Template;
import org.apache.jena.sparql.syntax.TripleCollector;
import org.apache.jena.sparql.syntax.TripleCollectorBGP;
import org.apache.jena.sparql.syntax.TripleCollectorMark;
import org.apache.jena.update.Update;

import dk.aau.cs.qweb.node.NodeFactoryStar;
import dk.aau.cs.qweb.triple.TriplePatternBuilder;

@SuppressWarnings("unused")
public class SPARQLStarParser11 extends SPARQLParserBase implements SPARQLParser11Constants {
	protected final Node nRDFtype       = NodeFactoryStar.createSimpleURINode("http://www.w3.org/1999/02/22-rdf-syntax-ns#type") ;
	
	 final public void QueryUnit() throws ParseException {
		    ByteOrderMark();
		    startQuery() ;
		    Query();
		    jj_consume_token(0);
		    finishQuery() ;
		  }

		  final public void Query() throws ParseException {
		    Prologue();
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case SELECT:
		      SelectQuery();
		      break;
		    case CONSTRUCT:
		      ConstructQuery();
		      break;
		    case DESCRIBE:
		      DescribeQuery();
		      break;
		    case ASK:
		      AskQuery();
		      break;
		    default:
		      jj_la1[0] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    ValuesClause();
		  }

		  final public void UpdateUnit() throws ParseException {
		    ByteOrderMark();
		    startUpdateRequest() ;
		    Update();
		    jj_consume_token(0);
		    finishUpdateRequest() ;
		  }

		  final public void ByteOrderMark() throws ParseException {
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case BOM:
		      jj_consume_token(BOM);
		      break;
		    default:
		      jj_la1[1] = jj_gen;
		      ;
		    }
		  }

		  final public void Prologue() throws ParseException {
		    label_1:
		    while (true) {
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case BASE:
		      case PREFIX:
		        ;
		        break;
		      default:
		        jj_la1[2] = jj_gen;
		        break label_1;
		      }
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case BASE:
		        BaseDecl();
		        break;
		      case PREFIX:
		        PrefixDecl();
		        break;
		      default:
		        jj_la1[3] = jj_gen;
		        jj_consume_token(-1);
		        throw new ParseException();
		      }
		    }
		  }

		  final public void BaseDecl() throws ParseException {
		                    String iri ;
		    jj_consume_token(BASE);
		    iri = IRIREF();
		    getPrologue().setBaseURI(iri) ;
		  }

		  final public void PrefixDecl() throws ParseException {
		                      Token t ; String iri ;
		    jj_consume_token(PREFIX);
		    t = jj_consume_token(PNAME_NS);
		    iri = IRIREF();
		        String s = fixupPrefix(t.image, t.beginLine, t.beginColumn) ;
		        getPrologue().setPrefix(s, iri) ;
		  }

		  final public void SelectQuery() throws ParseException {
		    SelectClause();
		    label_2:
		    while (true) {
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case FROM:
		        ;
		        break;
		      default:
		        jj_la1[4] = jj_gen;
		        break label_2;
		      }
		      DatasetClause();
		    }
		    WhereClause();
		    SolutionModifier();
		  }

		  final public void SubSelect() throws ParseException {
		    SelectClause();
		    WhereClause();
		    SolutionModifier();
		    ValuesClause();
		  }

		  final public void SelectClause() throws ParseException {
		                        Var v ; Expr expr ; Node n ;
		    jj_consume_token(SELECT);
		      getQuery().setQuerySelectType() ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case DISTINCT:
		    case REDUCED:
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case DISTINCT:
		        jj_consume_token(DISTINCT);
		                 getQuery().setDistinct(true);
		        break;
		      case REDUCED:
		        jj_consume_token(REDUCED);
		                getQuery().setReduced(true);
		        break;
		      default:
		        jj_la1[5] = jj_gen;
		        jj_consume_token(-1);
		        throw new ParseException();
		      }
		      break;
		    default:
		      jj_la1[6] = jj_gen;
		      ;
		    }
		    setAllowAggregatesInExpressions(true) ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case VAR1:
		    case VAR2:
		    case LPAREN:
		      label_3:
		      while (true) {
		        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		        case VAR1:
		        case VAR2:
		          v = Var();
		                  getQuery().addResultVar(v) ;
		          break;
		        case LPAREN:
		          v = null ;
		          jj_consume_token(LPAREN);
		          expr = Expression();
		          jj_consume_token(AS);
		          v = Var();
		          jj_consume_token(RPAREN);
		          getQuery().addResultVar(v, expr) ;
		        getQuery().setQueryResultStar(false) ;
		          break;
		        default:
		          jj_la1[7] = jj_gen;
		          jj_consume_token(-1);
		          throw new ParseException();
		        }
		        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		        case VAR1:
		        case VAR2:
		        case LPAREN:
		          ;
		          break;
		        default:
		          jj_la1[8] = jj_gen;
		          break label_3;
		        }
		      }
		      break;
		    case STAR:
		      jj_consume_token(STAR);
		             getQuery().setQueryResultStar(true) ;
		      break;
		    default:
		      jj_la1[9] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    setAllowAggregatesInExpressions(false) ;
		  }

		  final public void ConstructQuery() throws ParseException {
		                          Template t ;
		                          TripleCollectorBGP acc = new TripleCollectorBGP() ;
		    jj_consume_token(CONSTRUCT);
		     getQuery().setQueryConstructType() ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case LBRACE:
		      t = ConstructTemplate();
		        getQuery().setConstructTemplate(t) ;
		      label_4:
		      while (true) {
		        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		        case FROM:
		          ;
		          break;
		        default:
		          jj_la1[10] = jj_gen;
		          break label_4;
		        }
		        DatasetClause();
		      }
		      WhereClause();
		      SolutionModifier();
		      break;
		    case FROM:
		    case WHERE:
		      label_5:
		      while (true) {
		        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		        case FROM:
		          ;
		          break;
		        default:
		          jj_la1[11] = jj_gen;
		          break label_5;
		        }
		        DatasetClause();
		      }
		      jj_consume_token(WHERE);
		      jj_consume_token(LBRACE);
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case IRIref:
		      case PNAME_NS:
		      case PNAME_LN:
		      case BLANK_NODE_LABEL:
		      case VAR1:
		      case VAR2:
		      case TRUE:
		      case FALSE:
		      case INTEGER:
		      case DECIMAL:
		      case DOUBLE:
		      case INTEGER_POSITIVE:
		      case DECIMAL_POSITIVE:
		      case DOUBLE_POSITIVE:
		      case INTEGER_NEGATIVE:
		      case DECIMAL_NEGATIVE:
		      case DOUBLE_NEGATIVE:
		      case STRING_LITERAL1:
		      case STRING_LITERAL2:
		      case STRING_LITERAL_LONG1:
		      case STRING_LITERAL_LONG2:
		      case LPAREN:
		      case NIL:
		      case LBRACKET:
		      case ANON:
		        TriplesTemplate(acc);
		        break;
		      default:
		        jj_la1[12] = jj_gen;
		        ;
		      }
		      jj_consume_token(RBRACE);
		      SolutionModifier();
		      t = new Template(acc.getBGP()) ;
		      getQuery().setConstructTemplate(t) ;
		      ElementPathBlock epb = new ElementPathBlock(acc.getBGP()) ;
		      ElementGroup elg = new ElementGroup() ;
		      elg.addElement(epb) ;
		      getQuery().setQueryPattern(elg) ;
		      break;
		    default:
		      jj_la1[13] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		  }

		  final public void DescribeQuery() throws ParseException {
		                         Node n ;
		    jj_consume_token(DESCRIBE);
		      getQuery().setQueryDescribeType() ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case IRIref:
		    case PNAME_NS:
		    case PNAME_LN:
		    case VAR1:
		    case VAR2:
		      label_6:
		      while (true) {
		        n = VarOrIri();
		                       getQuery().addDescribeNode(n) ;
		        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		        case IRIref:
		        case PNAME_NS:
		        case PNAME_LN:
		        case VAR1:
		        case VAR2:
		          ;
		          break;
		        default:
		          jj_la1[14] = jj_gen;
		          break label_6;
		        }
		      }
		      getQuery().setQueryResultStar(false) ;
		      break;
		    case STAR:
		      jj_consume_token(STAR);
		      getQuery().setQueryResultStar(true) ;
		      break;
		    default:
		      jj_la1[15] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    label_7:
		    while (true) {
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case FROM:
		        ;
		        break;
		      default:
		        jj_la1[16] = jj_gen;
		        break label_7;
		      }
		      DatasetClause();
		    }
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case WHERE:
		    case LBRACE:
		      WhereClause();
		      break;
		    default:
		      jj_la1[17] = jj_gen;
		      ;
		    }
		    SolutionModifier();
		  }

		  final public void AskQuery() throws ParseException {
		    jj_consume_token(ASK);
		          getQuery().setQueryAskType() ;
		    label_8:
		    while (true) {
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case FROM:
		        ;
		        break;
		      default:
		        jj_la1[18] = jj_gen;
		        break label_8;
		      }
		      DatasetClause();
		    }
		    WhereClause();
		    SolutionModifier();
		  }

		  final public void DatasetClause() throws ParseException {
		    jj_consume_token(FROM);
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case IRIref:
		    case PNAME_NS:
		    case PNAME_LN:
		      DefaultGraphClause();
		      break;
		    case NAMED:
		      NamedGraphClause();
		      break;
		    default:
		      jj_la1[19] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		  }

		  final public void DefaultGraphClause() throws ParseException {
		                              String iri ;
		    iri = SourceSelector();
		    getQuery().addGraphURI(iri) ;
		  }

		  final public void NamedGraphClause() throws ParseException {
		                            String iri ;
		    jj_consume_token(NAMED);
		    iri = SourceSelector();
		    getQuery().addNamedGraphURI(iri) ;
		  }

		  final public String SourceSelector() throws ParseException {
		                            String iri ;
		    iri = iri();
		                {if (true) return iri ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public void WhereClause() throws ParseException {
		                       Element el ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case WHERE:
		      jj_consume_token(WHERE);
		      break;
		    default:
		      jj_la1[20] = jj_gen;
		      ;
		    }
		     startWherePattern() ;
		    el = GroupGraphPattern();
		                              getQuery().setQueryPattern(el) ;
		     finishWherePattern() ;
		  }

		  final public void SolutionModifier() throws ParseException {
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case GROUP:
		      GroupClause();
		      break;
		    default:
		      jj_la1[21] = jj_gen;
		      ;
		    }
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case HAVING:
		      HavingClause();
		      break;
		    default:
		      jj_la1[22] = jj_gen;
		      ;
		    }
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case ORDER:
		      OrderClause();
		      break;
		    default:
		      jj_la1[23] = jj_gen;
		      ;
		    }
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case LIMIT:
		    case OFFSET:
		      LimitOffsetClauses();
		      break;
		    default:
		      jj_la1[24] = jj_gen;
		      ;
		    }
		  }

		  final public void GroupClause() throws ParseException {
		    jj_consume_token(GROUP);
		    jj_consume_token(BY);
		    label_9:
		    while (true) {
		      GroupCondition();
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case IRIref:
		      case PNAME_NS:
		      case PNAME_LN:
		      case VAR1:
		      case VAR2:
		      case EXISTS:
		      case NOT:
		      case COUNT:
		      case MIN:
		      case MAX:
		      case SUM:
		      case AVG:
		      case SAMPLE:
		      case GROUP_CONCAT:
		      case BOUND:
		      case COALESCE:
		      case IF:
		      case BNODE:
		      case IRI:
		      case URI:
		      case STR:
		      case STRLANG:
		      case STRDT:
		      case DTYPE:
		      case LANG:
		      case LANGMATCHES:
		      case IS_URI:
		      case IS_IRI:
		      case IS_BLANK:
		      case IS_LITERAL:
		      case IS_NUMERIC:
		      case REGEX:
		      case SAME_TERM:
		      case RAND:
		      case ABS:
		      case CEIL:
		      case FLOOR:
		      case ROUND:
		      case CONCAT:
		      case SUBSTR:
		      case STRLEN:
		      case REPLACE:
		      case UCASE:
		      case LCASE:
		      case ENCODE_FOR_URI:
		      case CONTAINS:
		      case STRSTARTS:
		      case STRENDS:
		      case STRBEFORE:
		      case STRAFTER:
		      case YEAR:
		      case MONTH:
		      case DAY:
		      case HOURS:
		      case MINUTES:
		      case SECONDS:
		      case TIMEZONE:
		      case TZ:
		      case NOW:
		      case UUID:
		      case STRUUID:
		      case MD5:
		      case SHA1:
		      case SHA256:
		      case SHA384:
		      case SHA512:
		      case LPAREN:
		        ;
		        break;
		      default:
		        jj_la1[25] = jj_gen;
		        break label_9;
		      }
		    }
		  }

		  final public void GroupCondition() throws ParseException {
		                          Var v = null ; Expr expr = null ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case EXISTS:
		    case NOT:
		    case COUNT:
		    case MIN:
		    case MAX:
		    case SUM:
		    case AVG:
		    case SAMPLE:
		    case GROUP_CONCAT:
		    case BOUND:
		    case COALESCE:
		    case IF:
		    case BNODE:
		    case IRI:
		    case URI:
		    case STR:
		    case STRLANG:
		    case STRDT:
		    case DTYPE:
		    case LANG:
		    case LANGMATCHES:
		    case IS_URI:
		    case IS_IRI:
		    case IS_BLANK:
		    case IS_LITERAL:
		    case IS_NUMERIC:
		    case REGEX:
		    case SAME_TERM:
		    case RAND:
		    case ABS:
		    case CEIL:
		    case FLOOR:
		    case ROUND:
		    case CONCAT:
		    case SUBSTR:
		    case STRLEN:
		    case REPLACE:
		    case UCASE:
		    case LCASE:
		    case ENCODE_FOR_URI:
		    case CONTAINS:
		    case STRSTARTS:
		    case STRENDS:
		    case STRBEFORE:
		    case STRAFTER:
		    case YEAR:
		    case MONTH:
		    case DAY:
		    case HOURS:
		    case MINUTES:
		    case SECONDS:
		    case TIMEZONE:
		    case TZ:
		    case NOW:
		    case UUID:
		    case STRUUID:
		    case MD5:
		    case SHA1:
		    case SHA256:
		    case SHA384:
		    case SHA512:
		      expr = BuiltInCall();
		                           getQuery().addGroupBy((Var)null, expr) ;
		      break;
		    case IRIref:
		    case PNAME_NS:
		    case PNAME_LN:
		      expr = FunctionCall();
		                            getQuery().addGroupBy((Var)null, expr) ;
		      break;
		    case LPAREN:
		      jj_consume_token(LPAREN);
		      expr = Expression();
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case AS:
		        jj_consume_token(AS);
		        v = Var();
		        break;
		      default:
		        jj_la1[26] = jj_gen;
		        ;
		      }
		      jj_consume_token(RPAREN);
		      getQuery().addGroupBy(v ,expr) ;
		      break;
		    case VAR1:
		    case VAR2:
		      v = Var();
		      getQuery().addGroupBy(v) ;
		      break;
		    default:
		      jj_la1[27] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		  }

		  final public void HavingClause() throws ParseException {
		      setAllowAggregatesInExpressions(true) ;
		    jj_consume_token(HAVING);
		    label_10:
		    while (true) {
		      HavingCondition();
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case IRIref:
		      case PNAME_NS:
		      case PNAME_LN:
		      case EXISTS:
		      case NOT:
		      case COUNT:
		      case MIN:
		      case MAX:
		      case SUM:
		      case AVG:
		      case SAMPLE:
		      case GROUP_CONCAT:
		      case BOUND:
		      case COALESCE:
		      case IF:
		      case BNODE:
		      case IRI:
		      case URI:
		      case STR:
		      case STRLANG:
		      case STRDT:
		      case DTYPE:
		      case LANG:
		      case LANGMATCHES:
		      case IS_URI:
		      case IS_IRI:
		      case IS_BLANK:
		      case IS_LITERAL:
		      case IS_NUMERIC:
		      case REGEX:
		      case SAME_TERM:
		      case RAND:
		      case ABS:
		      case CEIL:
		      case FLOOR:
		      case ROUND:
		      case CONCAT:
		      case SUBSTR:
		      case STRLEN:
		      case REPLACE:
		      case UCASE:
		      case LCASE:
		      case ENCODE_FOR_URI:
		      case CONTAINS:
		      case STRSTARTS:
		      case STRENDS:
		      case STRBEFORE:
		      case STRAFTER:
		      case YEAR:
		      case MONTH:
		      case DAY:
		      case HOURS:
		      case MINUTES:
		      case SECONDS:
		      case TIMEZONE:
		      case TZ:
		      case NOW:
		      case UUID:
		      case STRUUID:
		      case MD5:
		      case SHA1:
		      case SHA256:
		      case SHA384:
		      case SHA512:
		      case LPAREN:
		        ;
		        break;
		      default:
		        jj_la1[28] = jj_gen;
		        break label_10;
		      }
		    }
		      setAllowAggregatesInExpressions(false) ;
		  }

		  final public void HavingCondition() throws ParseException {
		                           Expr c ;
		    c = Constraint();
		    getQuery().addHavingCondition(c) ;
		  }

		  final public void OrderClause() throws ParseException {
		    setAllowAggregatesInExpressions(true) ;
		    jj_consume_token(ORDER);
		    jj_consume_token(BY);
		    label_11:
		    while (true) {
		      OrderCondition();
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case IRIref:
		      case PNAME_NS:
		      case PNAME_LN:
		      case VAR1:
		      case VAR2:
		      case ASC:
		      case DESC:
		      case EXISTS:
		      case NOT:
		      case COUNT:
		      case MIN:
		      case MAX:
		      case SUM:
		      case AVG:
		      case SAMPLE:
		      case GROUP_CONCAT:
		      case BOUND:
		      case COALESCE:
		      case IF:
		      case BNODE:
		      case IRI:
		      case URI:
		      case STR:
		      case STRLANG:
		      case STRDT:
		      case DTYPE:
		      case LANG:
		      case LANGMATCHES:
		      case IS_URI:
		      case IS_IRI:
		      case IS_BLANK:
		      case IS_LITERAL:
		      case IS_NUMERIC:
		      case REGEX:
		      case SAME_TERM:
		      case RAND:
		      case ABS:
		      case CEIL:
		      case FLOOR:
		      case ROUND:
		      case CONCAT:
		      case SUBSTR:
		      case STRLEN:
		      case REPLACE:
		      case UCASE:
		      case LCASE:
		      case ENCODE_FOR_URI:
		      case CONTAINS:
		      case STRSTARTS:
		      case STRENDS:
		      case STRBEFORE:
		      case STRAFTER:
		      case YEAR:
		      case MONTH:
		      case DAY:
		      case HOURS:
		      case MINUTES:
		      case SECONDS:
		      case TIMEZONE:
		      case TZ:
		      case NOW:
		      case UUID:
		      case STRUUID:
		      case MD5:
		      case SHA1:
		      case SHA256:
		      case SHA384:
		      case SHA512:
		      case LPAREN:
		        ;
		        break;
		      default:
		        jj_la1[29] = jj_gen;
		        break label_11;
		      }
		    }
		    setAllowAggregatesInExpressions(false) ;
		  }

		  final public void OrderCondition() throws ParseException {
		  int direction = 0 ; Expr expr = null ; Node v = null ;
		    direction = Query.ORDER_DEFAULT ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case ASC:
		    case DESC:
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case ASC:
		        jj_consume_token(ASC);
		                direction = Query.ORDER_ASCENDING ;
		        break;
		      case DESC:
		        jj_consume_token(DESC);
		                 direction = Query.ORDER_DESCENDING ;
		        break;
		      default:
		        jj_la1[30] = jj_gen;
		        jj_consume_token(-1);
		        throw new ParseException();
		      }
		      expr = BrackettedExpression();
		      break;
		    case IRIref:
		    case PNAME_NS:
		    case PNAME_LN:
		    case VAR1:
		    case VAR2:
		    case EXISTS:
		    case NOT:
		    case COUNT:
		    case MIN:
		    case MAX:
		    case SUM:
		    case AVG:
		    case SAMPLE:
		    case GROUP_CONCAT:
		    case BOUND:
		    case COALESCE:
		    case IF:
		    case BNODE:
		    case IRI:
		    case URI:
		    case STR:
		    case STRLANG:
		    case STRDT:
		    case DTYPE:
		    case LANG:
		    case LANGMATCHES:
		    case IS_URI:
		    case IS_IRI:
		    case IS_BLANK:
		    case IS_LITERAL:
		    case IS_NUMERIC:
		    case REGEX:
		    case SAME_TERM:
		    case RAND:
		    case ABS:
		    case CEIL:
		    case FLOOR:
		    case ROUND:
		    case CONCAT:
		    case SUBSTR:
		    case STRLEN:
		    case REPLACE:
		    case UCASE:
		    case LCASE:
		    case ENCODE_FOR_URI:
		    case CONTAINS:
		    case STRSTARTS:
		    case STRENDS:
		    case STRBEFORE:
		    case STRAFTER:
		    case YEAR:
		    case MONTH:
		    case DAY:
		    case HOURS:
		    case MINUTES:
		    case SECONDS:
		    case TIMEZONE:
		    case TZ:
		    case NOW:
		    case UUID:
		    case STRUUID:
		    case MD5:
		    case SHA1:
		    case SHA256:
		    case SHA384:
		    case SHA512:
		    case LPAREN:
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case IRIref:
		      case PNAME_NS:
		      case PNAME_LN:
		      case EXISTS:
		      case NOT:
		      case COUNT:
		      case MIN:
		      case MAX:
		      case SUM:
		      case AVG:
		      case SAMPLE:
		      case GROUP_CONCAT:
		      case BOUND:
		      case COALESCE:
		      case IF:
		      case BNODE:
		      case IRI:
		      case URI:
		      case STR:
		      case STRLANG:
		      case STRDT:
		      case DTYPE:
		      case LANG:
		      case LANGMATCHES:
		      case IS_URI:
		      case IS_IRI:
		      case IS_BLANK:
		      case IS_LITERAL:
		      case IS_NUMERIC:
		      case REGEX:
		      case SAME_TERM:
		      case RAND:
		      case ABS:
		      case CEIL:
		      case FLOOR:
		      case ROUND:
		      case CONCAT:
		      case SUBSTR:
		      case STRLEN:
		      case REPLACE:
		      case UCASE:
		      case LCASE:
		      case ENCODE_FOR_URI:
		      case CONTAINS:
		      case STRSTARTS:
		      case STRENDS:
		      case STRBEFORE:
		      case STRAFTER:
		      case YEAR:
		      case MONTH:
		      case DAY:
		      case HOURS:
		      case MINUTES:
		      case SECONDS:
		      case TIMEZONE:
		      case TZ:
		      case NOW:
		      case UUID:
		      case STRUUID:
		      case MD5:
		      case SHA1:
		      case SHA256:
		      case SHA384:
		      case SHA512:
		      case LPAREN:
		        expr = Constraint();
		        break;
		      case VAR1:
		      case VAR2:
		        v = Var();
		        break;
		      default:
		        jj_la1[31] = jj_gen;
		        jj_consume_token(-1);
		        throw new ParseException();
		      }
		      break;
		    default:
		      jj_la1[32] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    if ( v == null )
		          getQuery().addOrderBy(expr, direction) ;
		      else
		          getQuery().addOrderBy(v, direction) ;
		  }

		  final public void LimitOffsetClauses() throws ParseException {
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case LIMIT:
		      LimitClause();
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case OFFSET:
		        OffsetClause();
		        break;
		      default:
		        jj_la1[33] = jj_gen;
		        ;
		      }
		      break;
		    case OFFSET:
		      OffsetClause();
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case LIMIT:
		        LimitClause();
		        break;
		      default:
		        jj_la1[34] = jj_gen;
		        ;
		      }
		      break;
		    default:
		      jj_la1[35] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		  }

		  final public void LimitClause() throws ParseException {
		                       Token t ;
		    jj_consume_token(LIMIT);
		    t = jj_consume_token(INTEGER);
		      getQuery().setLimit(integerValue(t.image)) ;
		  }

		  final public void OffsetClause() throws ParseException {
		                        Token t ;
		    jj_consume_token(OFFSET);
		    t = jj_consume_token(INTEGER);
		      getQuery().setOffset(integerValue(t.image)) ;
		  }

		  final public void ValuesClause() throws ParseException {
		                        Token t ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case VALUES:
		      t = jj_consume_token(VALUES);
		      startValuesClause(t.beginLine, t.beginColumn) ;
		      DataBlock();
		      finishValuesClause(t.beginLine, t.beginColumn) ;
		      break;
		    default:
		      jj_la1[36] = jj_gen;
		      ;
		    }
		  }

		  final public void Update() throws ParseException {
		    Prologue();
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case INSERT:
		    case DELETE:
		    case INSERT_DATA:
		    case DELETE_DATA:
		    case DELETE_WHERE:
		    case LOAD:
		    case CLEAR:
		    case CREATE:
		    case ADD:
		    case MOVE:
		    case COPY:
		    case DROP:
		    case WITH:
		      Update1();
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case SEMICOLON:
		        jj_consume_token(SEMICOLON);
		        Update();
		        break;
		      default:
		        jj_la1[37] = jj_gen;
		        ;
		      }
		      break;
		    default:
		      jj_la1[38] = jj_gen;
		      ;
		    }
		  }

		  final public void Update1() throws ParseException {
		                   Update up = null ;
		    startUpdateOperation() ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case LOAD:
		      up = Load();
		      break;
		    case CLEAR:
		      up = Clear();
		      break;
		    case DROP:
		      up = Drop();
		      break;
		    case ADD:
		      up = Add();
		      break;
		    case MOVE:
		      up = Move();
		      break;
		    case COPY:
		      up = Copy();
		      break;
		    case CREATE:
		      up = Create();
		      break;
		    case DELETE_WHERE:
		      up = DeleteWhere();
		      break;
		    case INSERT:
		    case DELETE:
		    case WITH:
		      up = Modify();
		      break;
		    case INSERT_DATA:
		      InsertData();
		      break;
		    case DELETE_DATA:
		      DeleteData();
		      break;
		    default:
		      jj_la1[39] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    if (null != up) emitUpdate(up) ;
		    finishUpdateOperation() ;
		  }

		  final public Update Load() throws ParseException {
		                  String url ; Node dest = null ; boolean silent = false ;
		    jj_consume_token(LOAD);
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case SILENT:
		      jj_consume_token(SILENT);
		                       silent = true ;
		      break;
		    default:
		      jj_la1[40] = jj_gen;
		      ;
		    }
		    url = iri();
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case INTO:
		      jj_consume_token(INTO);
		      dest = GraphRef();
		      break;
		    default:
		      jj_la1[41] = jj_gen;
		      ;
		    }
		      {if (true) return new UpdateLoad(url, dest, silent) ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Update Clear() throws ParseException {
		                   boolean silent = false ; Target target ;
		    jj_consume_token(CLEAR);
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case SILENT:
		      jj_consume_token(SILENT);
		                       silent = true ;
		      break;
		    default:
		      jj_la1[42] = jj_gen;
		      ;
		    }
		    target = GraphRefAll();
		     {if (true) return new UpdateClear(target, silent) ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Update Drop() throws ParseException {
		                  boolean silent = false ; Target target ;
		    jj_consume_token(DROP);
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case SILENT:
		      jj_consume_token(SILENT);
		                      silent = true ;
		      break;
		    default:
		      jj_la1[43] = jj_gen;
		      ;
		    }
		    target = GraphRefAll();
		     {if (true) return new UpdateDrop(target, silent) ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Update Create() throws ParseException {
		                    Node iri ; boolean silent = false ;
		    jj_consume_token(CREATE);
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case SILENT:
		      jj_consume_token(SILENT);
		                        silent=true ;
		      break;
		    default:
		      jj_la1[44] = jj_gen;
		      ;
		    }
		    iri = GraphRef();
		     {if (true) return new UpdateCreate(iri, silent) ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Update Add() throws ParseException {
		                 Target src ; Target dest ; boolean silent = false ;
		    jj_consume_token(ADD);
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case SILENT:
		      jj_consume_token(SILENT);
		                    silent=true ;
		      break;
		    default:
		      jj_la1[45] = jj_gen;
		      ;
		    }
		    src = GraphOrDefault();
		    jj_consume_token(TO);
		    dest = GraphOrDefault();
		    {if (true) return new UpdateAdd(src, dest, silent) ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Update Move() throws ParseException {
		                  Target src ; Target dest ; boolean silent = false ;
		    jj_consume_token(MOVE);
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case SILENT:
		      jj_consume_token(SILENT);
		                     silent=true ;
		      break;
		    default:
		      jj_la1[46] = jj_gen;
		      ;
		    }
		    src = GraphOrDefault();
		    jj_consume_token(TO);
		    dest = GraphOrDefault();
		    {if (true) return new UpdateMove(src, dest, silent) ;}
		    throw new Error("Missing return statement in function");
		  }

		  
		final public Update Copy() throws ParseException {
		                  Target src ; Target dest ; boolean silent = false ;
		    jj_consume_token(COPY);
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case SILENT:
		      jj_consume_token(SILENT);
		                     silent=true ;
		      break;
		    default:
		      jj_la1[47] = jj_gen;
		      ;
		    }
		    src = GraphOrDefault();
		    jj_consume_token(TO);
		    dest = GraphOrDefault();
		    {if (true) return new UpdateCopy(src, dest, silent) ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public void InsertData() throws ParseException {
		                      QuadDataAccSink qd = createInsertDataSink() ; Token t ;
		    t = jj_consume_token(INSERT_DATA);
		    int beginLine = t.beginLine; int beginColumn = t.beginColumn; t = null;
		    startDataInsert(qd, beginLine, beginColumn) ;
		    QuadData(qd);
		    finishDataInsert(qd, beginLine, beginColumn) ;
		    qd.close() ;
		  }

		  final public void DeleteData() throws ParseException {
		                      QuadDataAccSink qd = createDeleteDataSink() ; Token t ;
		    t = jj_consume_token(DELETE_DATA);
		    int beginLine = t.beginLine; int beginColumn = t.beginColumn; t = null;
		    startDataDelete(qd, beginLine, beginColumn) ;
		    QuadData(qd);
		    finishDataDelete(qd, beginLine, beginColumn) ;
		    qd.close() ;
		  }

		  final public Update DeleteWhere() throws ParseException {
		                         QuadAcc qp = new QuadAcc() ; Token t ;
		    t = jj_consume_token(DELETE_WHERE);
		    int beginLine = t.beginLine; int beginColumn = t.beginColumn; t = null;
		    startDeleteTemplate(qp, beginLine, beginColumn) ;
		    QuadPattern(qp);
		    finishDeleteTemplate(qp, beginLine, beginColumn) ;
		    {if (true) return new UpdateDeleteWhere(qp) ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Update Modify() throws ParseException {
		                    Element el ; String iri = null ;
		                    UpdateModify up = new UpdateModify() ;
		    startModifyUpdate() ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case WITH:
		      jj_consume_token(WITH);
		      iri = iri();
		                         Node n = createNode(iri) ; up.setWithIRI(n) ;
		      break;
		    default:
		      jj_la1[48] = jj_gen;
		      ;
		    }
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case DELETE:
		      DeleteClause(up);
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case INSERT:
		        InsertClause(up);
		        break;
		      default:
		        jj_la1[49] = jj_gen;
		        ;
		      }
		      break;
		    case INSERT:
		      InsertClause(up);
		      break;
		    default:
		      jj_la1[50] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    label_12:
		    while (true) {
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case USING:
		        ;
		        break;
		      default:
		        jj_la1[51] = jj_gen;
		        break label_12;
		      }
		      UsingClause(up);
		    }
		    jj_consume_token(WHERE);
		    startWherePattern() ;
		    el = GroupGraphPattern();
		                             up.setElement(el) ;
		    finishWherePattern() ;
		    finishModifyUpdate() ;
		    {if (true) return up ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public void DeleteClause(UpdateModify up) throws ParseException {
		                                       QuadAcc qp = up.getDeleteAcc() ; Token t ;
		    t = jj_consume_token(DELETE);
		     int beginLine = t.beginLine; int beginColumn = t.beginColumn; t = null;
		     startDeleteTemplate(qp, beginLine, beginColumn) ;
		    QuadPattern(qp);
		     finishDeleteTemplate(qp, beginLine, beginColumn) ;
		     up.setHasDeleteClause(true) ;
		  }

		  final public void InsertClause(UpdateModify up) throws ParseException {
		                                       QuadAcc qp = up.getInsertAcc() ; Token t ;
		    t = jj_consume_token(INSERT);
		     int beginLine = t.beginLine; int beginColumn = t.beginColumn; t = null;
		     startInsertTemplate(qp, beginLine, beginColumn) ;
		    QuadPattern(qp);
		     finishInsertTemplate(qp, beginLine, beginColumn) ;
		     up.setHasInsertClause(true) ;
		  }

		  final public void UsingClause(UpdateWithUsing update) throws ParseException {
		                                             String iri ; Node n ;
		    jj_consume_token(USING);
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case IRIref:
		    case PNAME_NS:
		    case PNAME_LN:
		      iri = iri();
		      n = createNode(iri) ; update.addUsing(n) ;
		      break;
		    case NAMED:
		      jj_consume_token(NAMED);
		      iri = iri();
		      n = createNode(iri) ; update.addUsingNamed(n) ;
		      break;
		    default:
		      jj_la1[52] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		  }

		  final public Target GraphOrDefault() throws ParseException {
		                            String iri ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case DFT:
		      jj_consume_token(DFT);
		            {if (true) return Target.DEFAULT ;}
		      break;
		    case IRIref:
		    case PNAME_NS:
		    case PNAME_LN:
		    case GRAPH:
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case GRAPH:
		        jj_consume_token(GRAPH);
		        break;
		      default:
		        jj_la1[53] = jj_gen;
		        ;
		      }
		      iri = iri();
		       {if (true) return Target.create(createNode(iri)) ;}
		      break;
		    default:
		      jj_la1[54] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    throw new Error("Missing return statement in function");
		  }

		  final public Node GraphRef() throws ParseException {
		                    String iri ;
		    jj_consume_token(GRAPH);
		    iri = iri();
		      {if (true) return createNode(iri) ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Target GraphRefAll() throws ParseException {
		                         Node iri ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case GRAPH:
		      iri = GraphRef();
		       {if (true) return Target.create(iri) ;}
		      break;
		    case DFT:
		      jj_consume_token(DFT);
		             {if (true) return Target.DEFAULT ;}
		      break;
		    case NAMED:
		      jj_consume_token(NAMED);
		               {if (true) return Target.NAMED ;}
		      break;
		    case ALL:
		      jj_consume_token(ALL);
		             {if (true) return Target.ALL ;}
		      break;
		    default:
		      jj_la1[55] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    throw new Error("Missing return statement in function");
		  }

		  final public void QuadPattern(QuadAcc acc) throws ParseException {
		    jj_consume_token(LBRACE);
		    Quads(acc);
		    jj_consume_token(RBRACE);
		  }

		  final public void QuadData(QuadDataAccSink acc) throws ParseException {
		    jj_consume_token(LBRACE);
		    Quads(acc);
		    jj_consume_token(RBRACE);
		  }

		  final public void Quads(QuadAccSink acc) throws ParseException {
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case IRIref:
		    case PNAME_NS:
		    case PNAME_LN:
		    case BLANK_NODE_LABEL:
		    case VAR1:
		    case VAR2:
		    case TRUE:
		    case FALSE:
		    case INTEGER:
		    case DECIMAL:
		    case DOUBLE:
		    case INTEGER_POSITIVE:
		    case DECIMAL_POSITIVE:
		    case DOUBLE_POSITIVE:
		    case INTEGER_NEGATIVE:
		    case DECIMAL_NEGATIVE:
		    case DOUBLE_NEGATIVE:
		    case STRING_LITERAL1:
		    case STRING_LITERAL2:
		    case STRING_LITERAL_LONG1:
		    case STRING_LITERAL_LONG2:
		    case LPAREN:
		    case NIL:
		    case LBRACKET:
		    case ANON:
		      TriplesTemplate(acc);
		      break;
		    default:
		      jj_la1[56] = jj_gen;
		      ;
		    }
		    label_13:
		    while (true) {
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case GRAPH:
		        ;
		        break;
		      default:
		        jj_la1[57] = jj_gen;
		        break label_13;
		      }
		      QuadsNotTriples(acc);
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case DOT:
		        jj_consume_token(DOT);
		        break;
		      default:
		        jj_la1[58] = jj_gen;
		        ;
		      }
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case IRIref:
		      case PNAME_NS:
		      case PNAME_LN:
		      case BLANK_NODE_LABEL:
		      case VAR1:
		      case VAR2:
		      case TRUE:
		      case FALSE:
		      case INTEGER:
		      case DECIMAL:
		      case DOUBLE:
		      case INTEGER_POSITIVE:
		      case DECIMAL_POSITIVE:
		      case DOUBLE_POSITIVE:
		      case INTEGER_NEGATIVE:
		      case DECIMAL_NEGATIVE:
		      case DOUBLE_NEGATIVE:
		      case STRING_LITERAL1:
		      case STRING_LITERAL2:
		      case STRING_LITERAL_LONG1:
		      case STRING_LITERAL_LONG2:
		      case LPAREN:
		      case NIL:
		      case LBRACKET:
		      case ANON:
		        TriplesTemplate(acc);
		        break;
		      default:
		        jj_la1[59] = jj_gen;
		        ;
		      }
		    }
		  }

		  final public void QuadsNotTriples(QuadAccSink acc) throws ParseException {
		                                         Node gn ; Node prev = acc.getGraph() ;
		    jj_consume_token(GRAPH);
		    gn = VarOrIri();
		      setAccGraph(acc, gn) ;
		    jj_consume_token(LBRACE);
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case IRIref:
		    case PNAME_NS:
		    case PNAME_LN:
		    case BLANK_NODE_LABEL:
		    case VAR1:
		    case VAR2:
		    case TRUE:
		    case FALSE:
		    case INTEGER:
		    case DECIMAL:
		    case DOUBLE:
		    case INTEGER_POSITIVE:
		    case DECIMAL_POSITIVE:
		    case DOUBLE_POSITIVE:
		    case INTEGER_NEGATIVE:
		    case DECIMAL_NEGATIVE:
		    case DOUBLE_NEGATIVE:
		    case STRING_LITERAL1:
		    case STRING_LITERAL2:
		    case STRING_LITERAL_LONG1:
		    case STRING_LITERAL_LONG2:
		    case LPAREN:
		    case NIL:
		    case LBRACKET:
		    case ANON:
		      TriplesTemplate(acc);
		      break;
		    default:
		      jj_la1[60] = jj_gen;
		      ;
		    }
		    jj_consume_token(RBRACE);
		      setAccGraph(acc, prev) ;
		  }

		  final public void TriplesTemplate(TripleCollector acc) throws ParseException {
		    TriplesSameSubject(acc);
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case DOT:
		      jj_consume_token(DOT);
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case IRIref:
		      case PNAME_NS:
		      case PNAME_LN:
		      case BLANK_NODE_LABEL:
		      case VAR1:
		      case VAR2:
		      case TRUE:
		      case FALSE:
		      case INTEGER:
		      case DECIMAL:
		      case DOUBLE:
		      case INTEGER_POSITIVE:
		      case DECIMAL_POSITIVE:
		      case DOUBLE_POSITIVE:
		      case INTEGER_NEGATIVE:
		      case DECIMAL_NEGATIVE:
		      case DOUBLE_NEGATIVE:
		      case STRING_LITERAL1:
		      case STRING_LITERAL2:
		      case STRING_LITERAL_LONG1:
		      case STRING_LITERAL_LONG2:
		      case LPAREN:
		      case NIL:
		      case LBRACKET:
		      case ANON:
		        TriplesTemplate(acc);
		        break;
		      default:
		        jj_la1[61] = jj_gen;
		        ;
		      }
		      break;
		    default:
		      jj_la1[62] = jj_gen;
		      ;
		    }
		  }

		  final public Element GroupGraphPattern() throws ParseException {
		                                Element el = null ; Token t ;
		    t = jj_consume_token(LBRACE);
		    int beginLine = t.beginLine; int beginColumn = t.beginColumn; t = null;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case SELECT:
		      startSubSelect(beginLine, beginColumn) ;
		      SubSelect();
		      Query q = endSubSelect(beginLine, beginColumn) ;
		      el = new ElementSubQuery(q) ;
		      break;
		    default:
		      jj_la1[63] = jj_gen;
		      el = GroupGraphPatternSub();
		    }
		    jj_consume_token(RBRACE);
		      {if (true) return el ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Element GroupGraphPatternSub() throws ParseException {
            Element el = null ;
	        ElementGroup elg = new ElementGroup() ;
	        startGroup(elg) ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case IRIref:
		    case LT: //Embedded triple
		    case PNAME_NS:
		    case PNAME_LN:
		    case BLANK_NODE_LABEL:
		    case VAR1:
		    case VAR2:
		    case TRUE:
		    case FALSE:
		    case INTEGER:
		    case DECIMAL:
		    case DOUBLE:
		    case INTEGER_POSITIVE:
		    case DECIMAL_POSITIVE:
		    case DOUBLE_POSITIVE:
		    case INTEGER_NEGATIVE:
		    case DECIMAL_NEGATIVE:
		    case DOUBLE_NEGATIVE:
		    case STRING_LITERAL1:
		    case STRING_LITERAL2:
		    case STRING_LITERAL_LONG1:
		    case STRING_LITERAL_LONG2:
		    case LPAREN:
		    case NIL:
		    case LBRACKET:
		    case ANON:
		      startTriplesBlock() ;
		      el = TriplesBlock(null);
		      endTriplesBlock() ;
		      elg.addElement(el) ;
		      break;
		    default:
		      jj_la1[64] = jj_gen;
		      ;
		    }
		    label_14:
		    while (true) {
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case VALUES:
		      case GRAPH:
		      case OPTIONAL:
		      case MINUS_P:
		      case BIND:
		      case SERVICE:
		      case FILTER:
		      case LBRACE:
		        ;
		        break;
		      default:
		        jj_la1[65] = jj_gen;
		        break label_14;
		      }
		      el = GraphPatternNotTriples();
		      elg.addElement(el) ;
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case DOT:
		        jj_consume_token(DOT);
		        break;
		      default:
		        jj_la1[66] = jj_gen;
		        ;
		      }
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case IRIref:
		      case PNAME_NS:
		      case PNAME_LN:
		      case LT: //Embedded triple
		      case BLANK_NODE_LABEL:
		      case VAR1:
		      case VAR2:
		      case TRUE:
		      case FALSE:
		      case INTEGER:
		      case DECIMAL:
		      case DOUBLE:
		      case INTEGER_POSITIVE:
		      case DECIMAL_POSITIVE:
		      case DOUBLE_POSITIVE:
		      case INTEGER_NEGATIVE:
		      case DECIMAL_NEGATIVE:
		      case DOUBLE_NEGATIVE:
		      case STRING_LITERAL1:
		      case STRING_LITERAL2:
		      case STRING_LITERAL_LONG1:
		      case STRING_LITERAL_LONG2:
		      case LPAREN:
		      case NIL:
		      case LBRACKET:
		      case ANON:
		        startTriplesBlock() ;
		        el = TriplesBlock(null);
		        endTriplesBlock() ;
		        elg.addElement(el) ;
		        break;
		      default:
		        jj_la1[67] = jj_gen;
		        ;
		      }
		    }
		        endGroup(elg) ;
		        {if (true) return elg ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Element TriplesBlock(ElementPathBlock acc) throws ParseException {
		    if ( acc == null )
		        acc = new ElementPathBlock() ;
		    TriplesSameSubjectPath(acc);
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case DOT:
		      jj_consume_token(DOT);
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case IRIref:
		      case PNAME_NS:
		      case PNAME_LN:
		      case LT: //Embedded triple
		      case BLANK_NODE_LABEL:
		      case VAR1:
		      case VAR2:
		      case TRUE:
		      case FALSE:
		      case INTEGER:
		      case DECIMAL:
		      case DOUBLE:
		      case INTEGER_POSITIVE:
		      case DECIMAL_POSITIVE:
		      case DOUBLE_POSITIVE:
		      case INTEGER_NEGATIVE:
		      case DECIMAL_NEGATIVE:
		      case DOUBLE_NEGATIVE:
		      case STRING_LITERAL1:
		      case STRING_LITERAL2:
		      case STRING_LITERAL_LONG1:
		      case STRING_LITERAL_LONG2:
		      case LPAREN:
		      case NIL:
		      case LBRACKET:
		      case ANON:
		        TriplesBlock(acc);
		        break;
		      default:
		        jj_la1[68] = jj_gen;
		        ;
		      }
		      break;
		    default:
		      jj_la1[69] = jj_gen;
		      ;
		    }
		      {if (true) return acc ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Element GraphPatternNotTriples() throws ParseException {
		                                     Element el = null ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case LBRACE:
		      el = GroupOrUnionGraphPattern();
		      break;
		    case OPTIONAL:
		      el = OptionalGraphPattern();
		      break;
		    case MINUS_P:
		      el = MinusGraphPattern();
		      break;
		    case GRAPH:
		      el = GraphGraphPattern();
		      break;
		    case SERVICE:
		      el = ServiceGraphPattern();
		      break;
		    case FILTER:
		      el = Filter();
		      break;
		    case BIND:
		      el = Bind();
		      break;
		    case VALUES:
		      el = InlineData();
		      break;
		    default:
		      jj_la1[70] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		   {if (true) return el ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Element OptionalGraphPattern() throws ParseException {
		                                   Element el ;
		    jj_consume_token(OPTIONAL);
		    el = GroupGraphPattern();
		      {if (true) return new ElementOptional(el) ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Element GraphGraphPattern() throws ParseException {
		                                Element el ; Node n ;
		    jj_consume_token(GRAPH);
		    n = VarOrIri();
		    el = GroupGraphPattern();
		      {if (true) return new ElementNamedGraph(n, el) ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Element ServiceGraphPattern() throws ParseException {
		                                  Element el ; Node n ; boolean silent = false ;
		    jj_consume_token(SERVICE);
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case SILENT:
		      jj_consume_token(SILENT);
		     silent=true;
		      break;
		    default:
		      jj_la1[71] = jj_gen;
		      ;
		    }
		    n = VarOrIri();
		    el = GroupGraphPattern();
		      {if (true) return new ElementService(n, el, silent) ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Element Bind() throws ParseException {
		                   Var v ; Expr expr ;
		    jj_consume_token(BIND);
		    jj_consume_token(LPAREN);
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case LT:
		    	jj_consume_token(LT);
		        token = token.next = token_source.getNextToken();
		        Node s = VarOrTerm();
		        Node p = VarOrTerm();
		        Node o = VarOrTerm();
		        jj_consume_token(GT);
		        token = token.next = token_source.getNextToken();
		       
		        expr = asExpr(NodeFactoryStar.createEmbeddedNode(s, p, o));
		        break;
		    default:
		    	expr = Expression();
		    }
		 
		    jj_consume_token(AS);
		    v = Var();
		    jj_consume_token(RPAREN);
		    {if (true) return new ElementBind(v, expr) ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Element InlineData() throws ParseException {
		                         ElementData el ; Token t ;
		    t = jj_consume_token(VALUES);
		    int beginLine = t.beginLine; int beginColumn = t.beginColumn; t = null;
		    el = new ElementData() ;
		    startInlineData(el.getVars(), el.getRows(), beginLine, beginColumn) ;
		    DataBlock();
		    finishInlineData(beginLine, beginColumn) ;
		    {if (true) return el ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public void DataBlock() throws ParseException {
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case VAR1:
		    case VAR2:
		      InlineDataOneVar();
		      break;
		    case LPAREN:
		    case NIL:
		      InlineDataFull();
		      break;
		    default:
		      jj_la1[72] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		  }

		  final public void InlineDataOneVar() throws ParseException {
		                            Var v ; Node n ; Token t ;
		    v = Var();
		    emitDataBlockVariable(v) ;
		    t = jj_consume_token(LBRACE);
		    label_15:
		    while (true) {
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case IRIref:
		      case PNAME_NS:
		      case PNAME_LN:
		      case UNDEF:
		      case TRUE:
		      case FALSE:
		      case INTEGER:
		      case DECIMAL:
		      case DOUBLE:
		      case INTEGER_POSITIVE:
		      case DECIMAL_POSITIVE:
		      case DOUBLE_POSITIVE:
		      case INTEGER_NEGATIVE:
		      case DECIMAL_NEGATIVE:
		      case DOUBLE_NEGATIVE:
		      case STRING_LITERAL1:
		      case STRING_LITERAL2:
		      case STRING_LITERAL_LONG1:
		      case STRING_LITERAL_LONG2:
		        ;
		        break;
		      default:
		        jj_la1[73] = jj_gen;
		        break label_15;
		      }
		      n = DataBlockValue();
		      startDataBlockValueRow(-1, -1) ;
		      emitDataBlockValue(n, -1, -1) ;
		      finishDataBlockValueRow(-1, -1) ;
		    }
		    t = jj_consume_token(RBRACE);
		  }

		  final public void InlineDataFull() throws ParseException {
		                          Var v ; Node n ; Token t ; int beginLine; int beginColumn;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case NIL:
		      jj_consume_token(NIL);
		      break;
		    case LPAREN:
		      jj_consume_token(LPAREN);
		      label_16:
		      while (true) {
		        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		        case VAR1:
		        case VAR2:
		          ;
		          break;
		        default:
		          jj_la1[74] = jj_gen;
		          break label_16;
		        }
		        v = Var();
		                 emitDataBlockVariable(v) ;
		      }
		      jj_consume_token(RPAREN);
		      break;
		    default:
		      jj_la1[75] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    jj_consume_token(LBRACE);
		    label_17:
		    while (true) {
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case LPAREN:
		      case NIL:
		        ;
		        break;
		      default:
		        jj_la1[76] = jj_gen;
		        break label_17;
		      }
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case LPAREN:
		        t = jj_consume_token(LPAREN);
		      beginLine = t.beginLine; beginColumn = t.beginColumn; t = null;
		      startDataBlockValueRow(beginLine, beginColumn) ;
		        label_18:
		        while (true) {
		          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		          case IRIref:
		          case PNAME_NS:
		          case PNAME_LN:
		          case UNDEF:
		          case TRUE:
		          case FALSE:
		          case INTEGER:
		          case DECIMAL:
		          case DOUBLE:
		          case INTEGER_POSITIVE:
		          case DECIMAL_POSITIVE:
		          case DOUBLE_POSITIVE:
		          case INTEGER_NEGATIVE:
		          case DECIMAL_NEGATIVE:
		          case DOUBLE_NEGATIVE:
		          case STRING_LITERAL1:
		          case STRING_LITERAL2:
		          case STRING_LITERAL_LONG1:
		          case STRING_LITERAL_LONG2:
		            ;
		            break;
		          default:
		            jj_la1[77] = jj_gen;
		            break label_18;
		          }
		          n = DataBlockValue();
		          emitDataBlockValue(n, beginLine, beginColumn) ;
		        }
		        t = jj_consume_token(RPAREN);
		      beginLine = t.beginLine; beginColumn = t.beginColumn; t = null;
		        finishDataBlockValueRow(beginLine, beginColumn) ;
		        break;
		      case NIL:
		        t = jj_consume_token(NIL);
		      beginLine = t.beginLine; beginColumn = t.beginColumn; t = null;
		        startDataBlockValueRow(beginLine, beginColumn) ;
		        finishDataBlockValueRow(beginLine, beginColumn) ;
		        break;
		      default:
		        jj_la1[78] = jj_gen;
		        jj_consume_token(-1);
		        throw new ParseException();
		      }
		    }
		    jj_consume_token(RBRACE);
		  }

		  final public Node DataBlockValue() throws ParseException {
		                          Node n ; String iri ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case IRIref:
		    case PNAME_NS:
		    case PNAME_LN:
		      iri = iri();
		                {if (true) return createNode(iri) ;}
		      break;
		    case STRING_LITERAL1:
		    case STRING_LITERAL2:
		    case STRING_LITERAL_LONG1:
		    case STRING_LITERAL_LONG2:
		      n = RDFLiteral();
		                     {if (true) return n ;}
		      break;
		    case INTEGER:
		    case DECIMAL:
		    case DOUBLE:
		    case INTEGER_POSITIVE:
		    case DECIMAL_POSITIVE:
		    case DOUBLE_POSITIVE:
		    case INTEGER_NEGATIVE:
		    case DECIMAL_NEGATIVE:
		    case DOUBLE_NEGATIVE:
		      n = NumericLiteral();
		                         {if (true) return n ;}
		      break;
		    case TRUE:
		    case FALSE:
		      n = BooleanLiteral();
		                         {if (true) return n ;}
		      break;
		    case UNDEF:
		      jj_consume_token(UNDEF);
		            {if (true) return null ;}
		      break;
		    default:
		      jj_la1[79] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    throw new Error("Missing return statement in function");
		  }

		  final public Element MinusGraphPattern() throws ParseException {
		                                Element el ;
		    jj_consume_token(MINUS_P);
		    el = GroupGraphPattern();
		      {if (true) return new ElementMinus(el) ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Element GroupOrUnionGraphPattern() throws ParseException {
		      Element el = null ; ElementUnion el2 = null ;
		    el = GroupGraphPattern();
		    label_19:
		    while (true) {
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case UNION:
		        ;
		        break;
		      default:
		        jj_la1[80] = jj_gen;
		        break label_19;
		      }
		      jj_consume_token(UNION);
		      if ( el2 == null )
		      {
		        el2 = new ElementUnion() ;
		        el2.addElement(el) ;
		      }
		      el = GroupGraphPattern();
		      el2.addElement(el) ;
		    }
		      {if (true) return (el2==null)? el : el2 ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Element Filter() throws ParseException {
		                     Expr c ;
		    jj_consume_token(FILTER);
		    c = Constraint();
		    {if (true) return new ElementFilter(c) ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Expr Constraint() throws ParseException {
		                      Expr c ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case LPAREN:
		      c = BrackettedExpression();
		      break;
		    case EXISTS:
		    case NOT:
		    case COUNT:
		    case MIN:
		    case MAX:
		    case SUM:
		    case AVG:
		    case SAMPLE:
		    case GROUP_CONCAT:
		    case BOUND:
		    case COALESCE:
		    case IF:
		    case BNODE:
		    case IRI:
		    case URI:
		    case STR:
		    case STRLANG:
		    case STRDT:
		    case DTYPE:
		    case LANG:
		    case LANGMATCHES:
		    case IS_URI:
		    case IS_IRI:
		    case IS_BLANK:
		    case IS_LITERAL:
		    case IS_NUMERIC:
		    case REGEX:
		    case SAME_TERM:
		    case RAND:
		    case ABS:
		    case CEIL:
		    case FLOOR:
		    case ROUND:
		    case CONCAT:
		    case SUBSTR:
		    case STRLEN:
		    case REPLACE:
		    case UCASE:
		    case LCASE:
		    case ENCODE_FOR_URI:
		    case CONTAINS:
		    case STRSTARTS:
		    case STRENDS:
		    case STRBEFORE:
		    case STRAFTER:
		    case YEAR:
		    case MONTH:
		    case DAY:
		    case HOURS:
		    case MINUTES:
		    case SECONDS:
		    case TIMEZONE:
		    case TZ:
		    case NOW:
		    case UUID:
		    case STRUUID:
		    case MD5:
		    case SHA1:
		    case SHA256:
		    case SHA384:
		    case SHA512:
		      c = BuiltInCall();
		      break;
		    case IRIref:
		    case PNAME_NS:
		    case PNAME_LN:
		      c = FunctionCall();
		      break;
		    default:
		      jj_la1[81] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    {if (true) return c ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Expr FunctionCall() throws ParseException {
		                        String fname ; Args a ;
		    fname = iri();
		    a = ArgList();
		     if ( AggregateRegistry.isRegistered(fname) ) {
		         if ( ! getAllowAggregatesInExpressions() )
		            throwParseException("Aggregate expression not legal at this point : "+fname, -1, -1) ;
		         Aggregator agg = AggregatorFactory.createCustom(fname, a) ;
		         Expr exprAgg = getQuery().allocAggregate(agg) ;
		         {if (true) return exprAgg ;}
		     }
		     {if (true) return new E_Function(fname, a) ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Args ArgList() throws ParseException {
		                   Expr expr ; Args args = new Args() ; Token t ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case NIL:
		      jj_consume_token(NIL);
		      break;
		    case LPAREN:
		      jj_consume_token(LPAREN);
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case DISTINCT:
		        t = jj_consume_token(DISTINCT);
		                        args.distinct = true ;
		        int beginLine = t.beginLine; int beginColumn = t.beginColumn; t = null;
		          if ( ! getAllowAggregatesInExpressions() )
		              throwParseException("Aggregate expression not legal at this point",
		                                 beginLine, beginColumn) ;
		        break;
		      default:
		        jj_la1[82] = jj_gen;
		        ;
		      }
		      expr = Expression();
		                            args.add(expr) ;
		      label_20:
		      while (true) {
		        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		        case COMMA:
		          ;
		          break;
		        default:
		          jj_la1[83] = jj_gen;
		          break label_20;
		        }
		        jj_consume_token(COMMA);
		        expr = Expression();
		                                     args.add(expr) ;
		      }
		      jj_consume_token(RPAREN);
		      break;
		    default:
		      jj_la1[84] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		      {if (true) return args ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public ExprList ExpressionList() throws ParseException {
		                              Expr expr = null ; ExprList exprList = new ExprList() ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case NIL:
		      jj_consume_token(NIL);
		      break;
		    case LPAREN:
		      jj_consume_token(LPAREN);
		      expr = Expression();
		                          exprList.add(expr) ;
		      label_21:
		      while (true) {
		        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		        case COMMA:
		          ;
		          break;
		        default:
		          jj_la1[85] = jj_gen;
		          break label_21;
		        }
		        jj_consume_token(COMMA);
		        expr = Expression();
		                                     exprList.add(expr) ;
		      }
		      jj_consume_token(RPAREN);
		      break;
		    default:
		      jj_la1[86] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    {if (true) return exprList ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Template ConstructTemplate() throws ParseException {
		                                 TripleCollectorBGP acc = new TripleCollectorBGP();
		                                 Template t = new Template(acc.getBGP()) ;
		      setInConstructTemplate(true) ;
		    jj_consume_token(LBRACE);
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case IRIref:
		    case PNAME_NS:
		    case PNAME_LN:
		    case BLANK_NODE_LABEL:
		    case VAR1:
		    case VAR2:
		    case TRUE:
		    case FALSE:
		    case INTEGER:
		    case DECIMAL:
		    case DOUBLE:
		    case INTEGER_POSITIVE:
		    case DECIMAL_POSITIVE:
		    case DOUBLE_POSITIVE:
		    case INTEGER_NEGATIVE:
		    case DECIMAL_NEGATIVE:
		    case DOUBLE_NEGATIVE:
		    case STRING_LITERAL1:
		    case STRING_LITERAL2:
		    case STRING_LITERAL_LONG1:
		    case STRING_LITERAL_LONG2:
		    case LPAREN:
		    case NIL:
		    case LBRACKET:
		    case ANON:
		      ConstructTriples(acc);
		      break;
		    default:
		      jj_la1[87] = jj_gen;
		      ;
		    }
		    jj_consume_token(RBRACE);
		      setInConstructTemplate(false) ;
		      {if (true) return t ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public void ConstructTriples(TripleCollector acc) throws ParseException {
		    TriplesSameSubject(acc);
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case DOT:
		      jj_consume_token(DOT);
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case IRIref:
		      case PNAME_NS:
		      case PNAME_LN:
		      case BLANK_NODE_LABEL:
		      case VAR1:
		      case VAR2:
		      case TRUE:
		      case FALSE:
		      case INTEGER:
		      case DECIMAL:
		      case DOUBLE:
		      case INTEGER_POSITIVE:
		      case DECIMAL_POSITIVE:
		      case DOUBLE_POSITIVE:
		      case INTEGER_NEGATIVE:
		      case DECIMAL_NEGATIVE:
		      case DOUBLE_NEGATIVE:
		      case STRING_LITERAL1:
		      case STRING_LITERAL2:
		      case STRING_LITERAL_LONG1:
		      case STRING_LITERAL_LONG2:
		      case LPAREN:
		      case NIL:
		      case LBRACKET:
		      case ANON:
		        ConstructTriples(acc);
		        break;
		      default:
		        jj_la1[88] = jj_gen;
		        ;
		      }
		      break;
		    default:
		      jj_la1[89] = jj_gen;
		      ;
		    }
		  }

		  final public void TriplesSameSubject(TripleCollector acc) throws ParseException {
		                                                 Node s ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case IRIref:
		    case PNAME_NS:
		    case PNAME_LN:
		    case BLANK_NODE_LABEL:
		    case VAR1:
		    case VAR2:
		    case TRUE:
		    case FALSE:
		    case INTEGER:
		    case DECIMAL:
		    case DOUBLE:
		    case INTEGER_POSITIVE:
		    case DECIMAL_POSITIVE:
		    case DOUBLE_POSITIVE:
		    case INTEGER_NEGATIVE:
		    case DECIMAL_NEGATIVE:
		    case DOUBLE_NEGATIVE:
		    case STRING_LITERAL1:
		    case STRING_LITERAL2:
		    case STRING_LITERAL_LONG1:
		    case STRING_LITERAL_LONG2:
		    case NIL:
		    case ANON:
		      s = VarOrTerm();
		      PropertyListNotEmpty(s, acc);
		      break;
		    case LPAREN:
		    case LBRACKET:
		    ElementPathBlock tempAcc = new ElementPathBlock() ;
		      s = TriplesNode(tempAcc);
		      PropertyList(s, tempAcc);
		    insert(acc, tempAcc) ;
		      break;
		    default:
		      jj_la1[90] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		  }

		  final public void PropertyList(Node s, TripleCollector acc) throws ParseException {
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case IRIref:
		    case PNAME_NS:
		    case PNAME_LN:
		    case VAR1:
		    case VAR2:
		    case KW_A:
		      PropertyListNotEmpty(s, acc);
		      break;
		    default:
		      jj_la1[91] = jj_gen;
		      ;
		    }
		  }

		  final public void PropertyListNotEmpty(Node s, TripleCollector acc) throws ParseException {
		      Node p = null ;
		    p = Verb();
		    ObjectList(s, p, null, acc);
		    label_22:
		    while (true) {
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case SEMICOLON:
		        ;
		        break;
		      default:
		        jj_la1[92] = jj_gen;
		        break label_22;
		      }
		      jj_consume_token(SEMICOLON);
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case IRIref:
		      case PNAME_NS:
		      case PNAME_LN:
		      case VAR1:
		      case VAR2:
		      case KW_A:
		        p = Verb();
		        ObjectList(s, p, null, acc);
		        break;
		      default:
		        jj_la1[93] = jj_gen;
		        ;
		      }
		    }
		  }

		  final public Node Verb() throws ParseException {
		                Node p ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case IRIref:
		    case PNAME_NS:
		    case PNAME_LN:
		    case VAR1:
		    case VAR2:
		      p = VarOrIri();
		      break;
		    case KW_A:
		      jj_consume_token(KW_A);
		                              p = nRDFtype ;
		      break;
		    default:
		      jj_la1[94] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    {if (true) return p ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public void ObjectList(Node s, Node p, Path path, TripleCollector acc) throws ParseException {
		                                                                   Node o ;
		    Object(s, p, path, acc);
		    label_23:
		    while (true) {
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case COMMA:
		        ;
		        break;
		      default:
		        jj_la1[95] = jj_gen;
		        break label_23;
		      }
		      jj_consume_token(COMMA);
		      Object(s, p, path, acc);
		    }
		  }

		  final public void Object(Node s, Node p, Path path, TripleCollector acc) throws ParseException {
		                                                               Node o ;
		    ElementPathBlock tempAcc = new ElementPathBlock() ; int mark = tempAcc.mark() ;
		    o = GraphNode(tempAcc);
		    insert(tempAcc, mark, s, p, path, o) ; insert(acc, tempAcc) ;
		  }

		  final public void TriplesSameSubjectPath(TripleCollector acc) throws ParseException {
		                                                     Node s ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case IRIref:
		    case LT:
		    case PNAME_NS:
		    case PNAME_LN:
		    case BLANK_NODE_LABEL:
		    case VAR1:
		    case VAR2:
		    case TRUE:
		    case FALSE:
		    case INTEGER:
		    case DECIMAL:
		    case DOUBLE:
		    case INTEGER_POSITIVE:
		    case DECIMAL_POSITIVE:
		    case DOUBLE_POSITIVE:
		    case INTEGER_NEGATIVE:
		    case DECIMAL_NEGATIVE:
		    case DOUBLE_NEGATIVE:
		    case STRING_LITERAL1:
		    case STRING_LITERAL2:
		    case STRING_LITERAL_LONG1:
		    case STRING_LITERAL_LONG2:
		    case NIL:
		    case ANON:
		      s = VarOrTerm();
		      PropertyListPathNotEmpty(s, acc);
		      break;
		    case LPAREN:
		    case LBRACKET:
		    ElementPathBlock tempAcc = new ElementPathBlock() ;
		      s = TriplesNodePath(tempAcc);
		      PropertyListPath(s, tempAcc);
		    insert(acc, tempAcc) ;
		      break;
		    default:
		      jj_la1[96] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		  }

		  final public void PropertyListPath(Node s, TripleCollector acc) throws ParseException {
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case IRIref:
		    case PNAME_NS:
		    case PNAME_LN:
		    case VAR1:
		    case VAR2:
		    case KW_A:
		    case LPAREN:
		    case BANG:
		    case CARAT:
		      PropertyListPathNotEmpty(s, acc);
		      break;
		    default:
		      jj_la1[97] = jj_gen;
		      ;
		    }
		  }

		  final public void PropertyListPathNotEmpty(Node s, TripleCollector acc) throws ParseException {
		      Path path = null ; Node p = null ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case IRIref:
		    case PNAME_NS:
		    case PNAME_LN:
		    case KW_A:
		    case LPAREN:
		    case BANG:
		    case CARAT:
		      path = VerbPath();
		      break;
		    case VAR1:
		    case VAR2:
		      p = VerbSimple();
		      break;
		    default:
		      jj_la1[98] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    ObjectListPath(s, p, path, acc);
		    label_24:
		    while (true) {
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case SEMICOLON:
		        ;
		        break;
		      default:
		        jj_la1[99] = jj_gen;
		        break label_24;
		      }
		      jj_consume_token(SEMICOLON);
		      path = null ; p = null ;
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case IRIref:
		      case PNAME_NS:
		      case PNAME_LN:
		      case VAR1:
		      case VAR2:
		      case KW_A:
		      case LPAREN:
		      case BANG:
		      case CARAT:
		        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		        case IRIref:
		        case PNAME_NS:
		        case PNAME_LN:
		        case KW_A:
		        case LPAREN:
		        case BANG:
		        case CARAT:
		          path = VerbPath();
		          break;
		        case VAR1:
		        case VAR2:
		          p = VerbSimple();
		          break;
		        default:
		          jj_la1[100] = jj_gen;
		          jj_consume_token(-1);
		          throw new ParseException();
		        }
		        ObjectListPath(s, p, path, acc);
		        break;
		      default:
		        jj_la1[101] = jj_gen;
		        ;
		      }
		    }
		  }

		  final public Path VerbPath() throws ParseException {
		                   Node p ; Path path ;
		    path = Path();
		                  {if (true) return path ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Node VerbSimple() throws ParseException {
		                      Node p ;
		    p = Var();
		    {if (true) return p ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public void ObjectListPath(Node s, Node p, Path path, TripleCollector acc) throws ParseException {
		                                                                       Node o ;
		    ObjectPath(s, p, path, acc);
		    label_25:
		    while (true) {
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case COMMA:
		        ;
		        break;
		      default:
		        jj_la1[102] = jj_gen;
		        break label_25;
		      }
		      jj_consume_token(COMMA);
		      ObjectPath(s, p, path, acc);
		    }
		  }

		  final public void ObjectPath(Node s, Node p, Path path, TripleCollector acc) throws ParseException {
		                                                                   Node o ;
		    ElementPathBlock tempAcc = new ElementPathBlock() ; int mark = tempAcc.mark() ;
		    o = GraphNodePath(tempAcc);
		    insert(tempAcc, mark, s, p, path, o) ; insert(acc, tempAcc) ;
		  }

		  final public Path Path() throws ParseException {
		                Path p ;
		    p = PathAlternative();
		                          {if (true) return p ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Path PathAlternative() throws ParseException {
		                           Path p1 , p2 ;
		    p1 = PathSequence();
		    label_26:
		    while (true) {
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case VBAR:
		        ;
		        break;
		      default:
		        jj_la1[103] = jj_gen;
		        break label_26;
		      }
		      jj_consume_token(VBAR);
		      p2 = PathSequence();
		        p1 = PathFactory.pathAlt(p1, p2) ;
		    }
		     {if (true) return p1 ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Path PathSequence() throws ParseException {
		                        Path p1 , p2 ;
		    p1 = PathEltOrInverse();
		    label_27:
		    while (true) {
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case SLASH:
		        ;
		        break;
		      default:
		        jj_la1[104] = jj_gen;
		        break label_27;
		      }
		      jj_consume_token(SLASH);
		      p2 = PathEltOrInverse();
		        p1 = PathFactory.pathSeq(p1, p2) ;
		    }
		     {if (true) return p1;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Path PathElt() throws ParseException {
		                   String str ; Node n ; Path p ;
		    p = PathPrimary();
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case PLUS:
		    case STAR:
		    case QMARK:
		      p = PathMod(p);
		      break;
		    default:
		      jj_la1[105] = jj_gen;
		      ;
		    }
		     {if (true) return p ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Path PathEltOrInverse() throws ParseException {
		                            String str ; Node n ; Path p ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case IRIref:
		    case PNAME_NS:
		    case PNAME_LN:
		    case KW_A:
		    case LPAREN:
		    case BANG:
		      p = PathElt();
		      break;
		    case CARAT:
		      jj_consume_token(CARAT);
		      p = PathElt();
		       p = PathFactory.pathInverse(p) ;
		      break;
		    default:
		      jj_la1[106] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		     {if (true) return p ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Path PathMod(Path p) throws ParseException {
		                         long i1 ; long i2 ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case QMARK:
		      jj_consume_token(QMARK);
		               {if (true) return PathFactory.pathZeroOrOne(p) ;}
		      break;
		    case STAR:
		      jj_consume_token(STAR);
		              {if (true) return PathFactory.pathZeroOrMore1(p) ;}
		      break;
		    case PLUS:
		      jj_consume_token(PLUS);
		              {if (true) return PathFactory.pathOneOrMore1(p) ;}
		      break;
		    default:
		      jj_la1[107] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    throw new Error("Missing return statement in function");
		  }

		  final public Path PathPrimary() throws ParseException {
		                       String str ; Path p ; Node n ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case IRIref:
		    case PNAME_NS:
		    case PNAME_LN:
		      str = iri();
		       n = createNode(str) ; p = PathFactory.pathLink(n) ;
		      break;
		    case KW_A:
		      jj_consume_token(KW_A);
		       p = PathFactory.pathLink(nRDFtype) ;
		      break;
		    case BANG:
		      jj_consume_token(BANG);
		      p = PathNegatedPropertySet();
		      break;
		    case LPAREN:
		      jj_consume_token(LPAREN);
		      p = Path();
		      jj_consume_token(RPAREN);
		      break;
		    default:
		      jj_la1[108] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		   {if (true) return p ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Path PathNegatedPropertySet() throws ParseException {
		                                  P_Path0 p ; P_NegPropSet pNegSet ;
		    pNegSet = new P_NegPropSet() ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case IRIref:
		    case PNAME_NS:
		    case PNAME_LN:
		    case KW_A:
		    case CARAT:
		      p = PathOneInPropertySet();
		      pNegSet.add(p) ;
		      break;
		    case LPAREN:
		      jj_consume_token(LPAREN);
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case IRIref:
		      case PNAME_NS:
		      case PNAME_LN:
		      case KW_A:
		      case CARAT:
		        p = PathOneInPropertySet();
		                                   pNegSet.add(p) ;
		        label_28:
		        while (true) {
		          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		          case VBAR:
		            ;
		            break;
		          default:
		            jj_la1[109] = jj_gen;
		            break label_28;
		          }
		          jj_consume_token(VBAR);
		          p = PathOneInPropertySet();
		                                           pNegSet.add(p) ;
		        }
		        break;
		      default:
		        jj_la1[110] = jj_gen;
		        ;
		      }
		      jj_consume_token(RPAREN);
		      break;
		    default:
		      jj_la1[111] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    {if (true) return pNegSet ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public P_Path0 PathOneInPropertySet() throws ParseException {
		                                   String str ; Node n ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case IRIref:
		    case PNAME_NS:
		    case PNAME_LN:
		      str = iri();
		                  n = createNode(str) ; {if (true) return new P_Link(n) ;}
		      break;
		    case KW_A:
		      jj_consume_token(KW_A);
		             {if (true) return new P_Link(nRDFtype) ;}
		      break;
		    case CARAT:
		      jj_consume_token(CARAT);
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case IRIref:
		      case PNAME_NS:
		      case PNAME_LN:
		        str = iri();
		                    n = createNode(str) ; {if (true) return new P_ReverseLink(n) ;}
		        break;
		      case KW_A:
		        jj_consume_token(KW_A);
		               {if (true) return new P_ReverseLink(nRDFtype) ;}
		        break;
		      default:
		        jj_la1[112] = jj_gen;
		        jj_consume_token(-1);
		        throw new ParseException();
		      }
		      break;
		    default:
		      jj_la1[113] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    throw new Error("Missing return statement in function");
		  }

		  final public long Integer() throws ParseException {
		                  Token t ;
		    t = jj_consume_token(INTEGER);
		      {if (true) return integerValue(t.image) ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Node TriplesNode(TripleCollectorMark acc) throws ParseException {
		                                              Node n ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case LPAREN:
		      n = Collection(acc);
		                        {if (true) return n ;}
		      break;
		    case LBRACKET:
		      n = BlankNodePropertyList(acc);
		                                   {if (true) return n ;}
		      break;
		    default:
		      jj_la1[114] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    throw new Error("Missing return statement in function");
		  }

		  final public Node BlankNodePropertyList(TripleCollector acc) throws ParseException {
		                                                    Token t ;
		    t = jj_consume_token(LBRACKET);
		      Node n = createBNode( t.beginLine, t.beginColumn) ;
		    PropertyListNotEmpty(n, acc);
		    jj_consume_token(RBRACKET);
		      {if (true) return n ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Node TriplesNodePath(TripleCollectorMark acc) throws ParseException {
		                                                  Node n ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case LPAREN:
		      n = CollectionPath(acc);
		                            {if (true) return n ;}
		      break;
		    case LBRACKET:
		      n = BlankNodePropertyListPath(acc);
		                                       {if (true) return n ;}
		      break;
		    default:
		      jj_la1[115] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    throw new Error("Missing return statement in function");
		  }

		  final public Node BlankNodePropertyListPath(TripleCollector acc) throws ParseException {
		                                                        Token t ;
		    t = jj_consume_token(LBRACKET);
		      Node n = createBNode( t.beginLine, t.beginColumn) ;
		    PropertyListPathNotEmpty(n, acc);
		    jj_consume_token(RBRACKET);
		      {if (true) return n ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Node Collection(TripleCollectorMark acc) throws ParseException {
		      Node listHead = nRDFnil ; Node lastCell = null ; int mark ; Node n ; Token t ;
		    t = jj_consume_token(LPAREN);
		    int beginLine = t.beginLine; int beginColumn = t.beginColumn; t = null;
		    label_29:
		    while (true) {
		      Node cell = createListNode( beginLine, beginColumn) ;
		      if ( listHead == nRDFnil )
		         listHead = cell ;
		      if ( lastCell != null )
		        insert(acc, lastCell, nRDFrest, cell) ;
		      mark = acc.mark() ;
		      n = GraphNode(acc);
		      insert(acc, mark, cell, nRDFfirst, n) ;
		      lastCell = cell ;
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case IRIref:
		      case PNAME_NS:
		      case PNAME_LN:
		      case BLANK_NODE_LABEL:
		      case VAR1:
		      case VAR2:
		      case TRUE:
		      case FALSE:
		      case INTEGER:
		      case DECIMAL:
		      case DOUBLE:
		      case INTEGER_POSITIVE:
		      case DECIMAL_POSITIVE:
		      case DOUBLE_POSITIVE:
		      case INTEGER_NEGATIVE:
		      case DECIMAL_NEGATIVE:
		      case DOUBLE_NEGATIVE:
		      case STRING_LITERAL1:
		      case STRING_LITERAL2:
		      case STRING_LITERAL_LONG1:
		      case STRING_LITERAL_LONG2:
		      case LPAREN:
		      case NIL:
		      case LBRACKET:
		      case ANON:
		        ;
		        break;
		      default:
		        jj_la1[116] = jj_gen;
		        break label_29;
		      }
		    }
		    jj_consume_token(RPAREN);
		     if ( lastCell != null )
		       insert(acc, lastCell, nRDFrest, nRDFnil) ;
		     {if (true) return listHead ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Node CollectionPath(TripleCollectorMark acc) throws ParseException {
		      Node listHead = nRDFnil ; Node lastCell = null ; int mark ; Node n ; Token t ;
		    t = jj_consume_token(LPAREN);
		    int beginLine = t.beginLine; int beginColumn = t.beginColumn; t = null;
		    label_30:
		    while (true) {
		      Node cell = createListNode( beginLine, beginColumn) ;
		      if ( listHead == nRDFnil )
		         listHead = cell ;
		      if ( lastCell != null )
		        insert(acc, lastCell, nRDFrest, cell) ;
		      mark = acc.mark() ;
		      n = GraphNodePath(acc);
		      insert(acc, mark, cell, nRDFfirst, n) ;
		      lastCell = cell ;
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case IRIref:
		      case PNAME_NS:
		      case PNAME_LN:
		      case BLANK_NODE_LABEL:
		      case VAR1:
		      case VAR2:
		      case TRUE:
		      case FALSE:
		      case INTEGER:
		      case DECIMAL:
		      case DOUBLE:
		      case INTEGER_POSITIVE:
		      case DECIMAL_POSITIVE:
		      case DOUBLE_POSITIVE:
		      case INTEGER_NEGATIVE:
		      case DECIMAL_NEGATIVE:
		      case DOUBLE_NEGATIVE:
		      case STRING_LITERAL1:
		      case STRING_LITERAL2:
		      case STRING_LITERAL_LONG1:
		      case STRING_LITERAL_LONG2:
		      case LPAREN:
		      case NIL:
		      case LBRACKET:
		      case ANON:
		        ;
		        break;
		      default:
		        jj_la1[117] = jj_gen;
		        break label_30;
		      }
		    }
		    jj_consume_token(RPAREN);
		     if ( lastCell != null )
		       insert(acc, lastCell, nRDFrest, nRDFnil) ;
		     {if (true) return listHead ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Node GraphNode(TripleCollectorMark acc) throws ParseException {
		                                            Node n ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case IRIref:
		    case PNAME_NS:
		    case PNAME_LN:
		    case BLANK_NODE_LABEL:
		    case VAR1:
		    case VAR2:
		    case TRUE:
		    case FALSE:
		    case INTEGER:
		    case DECIMAL:
		    case DOUBLE:
		    case INTEGER_POSITIVE:
		    case DECIMAL_POSITIVE:
		    case DOUBLE_POSITIVE:
		    case INTEGER_NEGATIVE:
		    case DECIMAL_NEGATIVE:
		    case DOUBLE_NEGATIVE:
		    case STRING_LITERAL1:
		    case STRING_LITERAL2:
		    case STRING_LITERAL_LONG1:
		    case STRING_LITERAL_LONG2:
		    case NIL:
		    case ANON:
		      n = VarOrTerm();
		                    {if (true) return n ;}
		      break;
		    case LPAREN:
		    case LBRACKET:
		      n = TriplesNode(acc);
		                         {if (true) return n ;}
		      break;
		    default:
		      jj_la1[118] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    throw new Error("Missing return statement in function");
		  }

		  final public Node GraphNodePath(TripleCollectorMark acc) throws ParseException {
		                                                Node n ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case IRIref:
		    case PNAME_NS:
		    case PNAME_LN:
		    case BLANK_NODE_LABEL:
		    case VAR1:
		    case VAR2:
		    case TRUE:
		    case FALSE:
		    case INTEGER:
		    case DECIMAL:
		    case DOUBLE:
		    case INTEGER_POSITIVE:
		    case DECIMAL_POSITIVE:
		    case DOUBLE_POSITIVE:
		    case INTEGER_NEGATIVE:
		    case DECIMAL_NEGATIVE:
		    case DOUBLE_NEGATIVE:
		    case STRING_LITERAL1:
		    case STRING_LITERAL2:
		    case STRING_LITERAL_LONG1:
		    case STRING_LITERAL_LONG2:
		    case NIL:
		    case ANON:
		      n = VarOrTerm();
		                    {if (true) return n ;}
		      break;
		    case LPAREN:
		    case LBRACKET:
		      n = TriplesNodePath(acc);
		                             {if (true) return n ;}
		      break;
		    default:
		      jj_la1[119] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    throw new Error("Missing return statement in function");
		  }

		  final public Node VarOrTerm() throws ParseException {
		                    Node n = null ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case VAR1:
		    case VAR2:
		      n = Var();
		      break;
		    case IRIref:
		    case LT:
		    case PNAME_NS:
		    case PNAME_LN:
		    case BLANK_NODE_LABEL:
		    case TRUE:
		    case FALSE:
		    case INTEGER:
		    case DECIMAL:
		    case DOUBLE:
		    case INTEGER_POSITIVE:
		    case DECIMAL_POSITIVE:
		    case DOUBLE_POSITIVE:
		    case INTEGER_NEGATIVE:
		    case DECIMAL_NEGATIVE:
		    case DOUBLE_NEGATIVE:
		    case STRING_LITERAL1:
		    case STRING_LITERAL2:
		    case STRING_LITERAL_LONG1:
		    case STRING_LITERAL_LONG2:
		    case NIL:
		    case ANON:
		      n = GraphTerm();
		      break;
		    default:
		      jj_la1[120] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    {if (true) return n ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Node VarOrIri() throws ParseException {
		                   Node n = null ; String iri ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case VAR1:
		    case VAR2:
		      n = Var();
		      break;
		    case IRIref:
		    case PNAME_NS:
		    case PNAME_LN:
		      iri = iri();
		                              n = createNode(iri) ;
		      break;
		    default:
		      jj_la1[121] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    {if (true) return n ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Node VarOrBlankNodeOrIri() throws ParseException {
		                              Node n = null ; String iri ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case VAR1:
		    case VAR2:
		      n = Var();
		      break;
		    case BLANK_NODE_LABEL:
		    case ANON:
		      n = BlankNode();
		      break;
		    case IRIref:
		    case PNAME_NS:
		    case PNAME_LN:
		      iri = iri();
		                                                n = createNode(iri) ;
		      break;
		    default:
		      jj_la1[122] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    {if (true) return n ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Var Var() throws ParseException {
		              Token t ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case VAR1:
		      t = jj_consume_token(VAR1);
		      break;
		    case VAR2:
		      t = jj_consume_token(VAR2);
		      break;
		    default:
		      jj_la1[123] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		      {if (true) return createVariable(t.image, t.beginLine, t.beginColumn) ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Node GraphTerm() throws ParseException {
		                     Node n ; String iri ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case IRIref:
		    
		    case PNAME_NS:
		    case PNAME_LN:
		      iri = iri();
		                {if (true) return createNode(iri) ;}
		      break;
		    case LT:
		    	return EmbeddedTriple();
		    	
		    case STRING_LITERAL1:
		    case STRING_LITERAL2:
		    case STRING_LITERAL_LONG1:
		    case STRING_LITERAL_LONG2:
		      n = RDFLiteral();
		                     {if (true) return n ;}
		      break;
		    case INTEGER:
		    case DECIMAL:
		    case DOUBLE:
		    case INTEGER_POSITIVE:
		    case DECIMAL_POSITIVE:
		    case DOUBLE_POSITIVE:
		    case INTEGER_NEGATIVE:
		    case DECIMAL_NEGATIVE:
		    case DOUBLE_NEGATIVE:
		      n = NumericLiteral();
		                         {if (true) return n ;}
		      break;
		    case TRUE:
		    case FALSE:
		      n = BooleanLiteral();
		                         {if (true) return n ;}
		      break;
		    case BLANK_NODE_LABEL:
		    case ANON:
		      n = BlankNode();
		                    {if (true) return n ;}
		      break;
		    case NIL:
		      jj_consume_token(NIL);
		          {if (true) return nRDFnil ;}
		      break;
		    default:
		      jj_la1[124] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    throw new Error("Missing return statement in function");
		  }

		  final public Expr Expression() throws ParseException {
		                      Expr expr ;
		    expr = ConditionalOrExpression();
		    {if (true) return expr ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Expr ConditionalOrExpression() throws ParseException {
		                                   Expr expr1, expr2 ;
		    expr1 = ConditionalAndExpression();
		    label_31:
		    while (true) {
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case SC_OR:
		        ;
		        break;
		      default:
		        jj_la1[125] = jj_gen;
		        break label_31;
		      }
		      jj_consume_token(SC_OR);
		      expr2 = ConditionalAndExpression();
		      expr1 = new E_LogicalOr(expr1, expr2) ;
		    }
		      {if (true) return expr1 ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Expr ConditionalAndExpression() throws ParseException {
		                                    Expr expr1, expr2 ;
		    expr1 = ValueLogical();
		    label_32:
		    while (true) {
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case SC_AND:
		        ;
		        break;
		      default:
		        jj_la1[126] = jj_gen;
		        break label_32;
		      }
		      jj_consume_token(SC_AND);
		      expr2 = ValueLogical();
		      expr1 = new E_LogicalAnd(expr1, expr2) ;
		    }
		      {if (true) return expr1 ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Expr ValueLogical() throws ParseException {
		                        Expr expr ;
		    expr = RelationalExpression();
		      {if (true) return expr ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Expr RelationalExpression() throws ParseException {
		                                Expr expr1, expr2 ; ExprList a ;
		    expr1 = NumericExpression();
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case NOT:
		    case IN:
		    case EQ:
		    case NE:
		    case GT:
		    case LT:
		    case LE:
		    case GE:
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case EQ:
		        jj_consume_token(EQ);
		        expr2 = NumericExpression();
		        expr1 = new E_Equals(expr1, expr2) ;
		        break;
		      case NE:
		        jj_consume_token(NE);
		        expr2 = NumericExpression();
		        expr1 = new E_NotEquals(expr1, expr2) ;
		        break;
		      case LT:
		        jj_consume_token(LT);
		        expr2 = NumericExpression();
		        expr1 = new E_LessThan(expr1, expr2) ;
		        break;
		      case GT:
		        jj_consume_token(GT);
		        expr2 = NumericExpression();
		        expr1 = new E_GreaterThan(expr1, expr2) ;
		        break;
		      case LE:
		        jj_consume_token(LE);
		        expr2 = NumericExpression();
		        expr1 = new E_LessThanOrEqual(expr1, expr2) ;
		        break;
		      case GE:
		        jj_consume_token(GE);
		        expr2 = NumericExpression();
		        expr1 = new E_GreaterThanOrEqual(expr1, expr2) ;
		        break;
		      case IN:
		        jj_consume_token(IN);
		        a = ExpressionList();
		        expr1 = new E_OneOf(expr1, a) ;
		        break;
		      case NOT:
		        jj_consume_token(NOT);
		        jj_consume_token(IN);
		        a = ExpressionList();
		        expr1 = new E_NotOneOf(expr1, a) ;
		        break;
		      default:
		        jj_la1[127] = jj_gen;
		        jj_consume_token(-1);
		        throw new ParseException();
		      }
		      break;
		    default:
		      jj_la1[128] = jj_gen;
		      ;
		    }
		      {if (true) return expr1 ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Expr NumericExpression() throws ParseException {
		                              Expr expr ;
		    expr = AdditiveExpression();
		      {if (true) return expr ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Expr AdditiveExpression() throws ParseException {
		                              Expr expr1, expr2, expr3 ; boolean addition ; Node n ;
		    expr1 = MultiplicativeExpression();
		    label_33:
		    while (true) {
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case INTEGER_POSITIVE:
		      case DECIMAL_POSITIVE:
		      case DOUBLE_POSITIVE:
		      case INTEGER_NEGATIVE:
		      case DECIMAL_NEGATIVE:
		      case DOUBLE_NEGATIVE:
		      case PLUS:
		      case MINUS:
		        ;
		        break;
		      default:
		        jj_la1[129] = jj_gen;
		        break label_33;
		      }
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case PLUS:
		        jj_consume_token(PLUS);
		        expr2 = MultiplicativeExpression();
		      expr1 = new E_Add(expr1, expr2) ;
		        break;
		      case MINUS:
		        jj_consume_token(MINUS);
		        expr2 = MultiplicativeExpression();
		      expr1 = new E_Subtract(expr1, expr2) ;
		        break;
		      case INTEGER_POSITIVE:
		      case DECIMAL_POSITIVE:
		      case DOUBLE_POSITIVE:
		      case INTEGER_NEGATIVE:
		      case DECIMAL_NEGATIVE:
		      case DOUBLE_NEGATIVE:
		        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		        case INTEGER_POSITIVE:
		        case DECIMAL_POSITIVE:
		        case DOUBLE_POSITIVE:
		          n = NumericLiteralPositive();
		         n = stripSign(n) ;
		         expr2 = asExpr(n) ;
		         addition = true ;
		          break;
		        case INTEGER_NEGATIVE:
		        case DECIMAL_NEGATIVE:
		        case DOUBLE_NEGATIVE:
		          n = NumericLiteralNegative();
		         n = stripSign(n) ;
		         expr2 = asExpr(n) ;
		         addition = false ;
		          break;
		        default:
		          jj_la1[130] = jj_gen;
		          jj_consume_token(-1);
		          throw new ParseException();
		        }
		        label_34:
		        while (true) {
		          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		          case STAR:
		          case SLASH:
		            ;
		            break;
		          default:
		            jj_la1[131] = jj_gen;
		            break label_34;
		          }
		          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		          case STAR:
		            jj_consume_token(STAR);
		            expr3 = UnaryExpression();
		                                           expr2 = new E_Multiply(expr2, expr3) ;
		            break;
		          case SLASH:
		            jj_consume_token(SLASH);
		            expr3 = UnaryExpression();
		                                            expr2 = new E_Divide(expr2, expr3) ;
		            break;
		          default:
		            jj_la1[132] = jj_gen;
		            jj_consume_token(-1);
		            throw new ParseException();
		          }
		        }
		      if ( addition )
		         expr1 = new E_Add(expr1, expr2) ;
		      else
		         expr1 = new E_Subtract(expr1, expr2) ;
		        break;
		      default:
		        jj_la1[133] = jj_gen;
		        jj_consume_token(-1);
		        throw new ParseException();
		      }
		    }
		    {if (true) return expr1 ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Expr MultiplicativeExpression() throws ParseException {
		                                    Expr expr1, expr2 ;
		    expr1 = UnaryExpression();
		    label_35:
		    while (true) {
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case STAR:
		      case SLASH:
		        ;
		        break;
		      default:
		        jj_la1[134] = jj_gen;
		        break label_35;
		      }
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case STAR:
		        jj_consume_token(STAR);
		        expr2 = UnaryExpression();
		      expr1 = new E_Multiply(expr1, expr2) ;
		        break;
		      case SLASH:
		        jj_consume_token(SLASH);
		        expr2 = UnaryExpression();
		      expr1 = new E_Divide(expr1, expr2) ;
		        break;
		      default:
		        jj_la1[135] = jj_gen;
		        jj_consume_token(-1);
		        throw new ParseException();
		      }
		    }
		      {if (true) return expr1 ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Expr UnaryExpression() throws ParseException {
		                           Expr expr ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case BANG:
		      jj_consume_token(BANG);
		      expr = PrimaryExpression();
		      {if (true) return new E_LogicalNot(expr) ;}
		      break;
		    case PLUS:
		      jj_consume_token(PLUS);
		      expr = PrimaryExpression();
		                                        {if (true) return new E_UnaryPlus(expr) ;}
		      break;
		    case MINUS:
		      jj_consume_token(MINUS);
		      expr = PrimaryExpression();
		                                         {if (true) return new E_UnaryMinus(expr) ;}
		      break;
		    case IRIref:
		    case PNAME_NS:
		    case PNAME_LN:
		    case VAR1:
		    case VAR2:
		    case EXISTS:
		    case NOT:
		    case COUNT:
		    case MIN:
		    case MAX:
		    case SUM:
		    case AVG:
		    case SAMPLE:
		    case GROUP_CONCAT:
		    case BOUND:
		    case COALESCE:
		    case IF:
		    case BNODE:
		    case IRI:
		    case URI:
		    case STR:
		    case STRLANG:
		    case STRDT:
		    case DTYPE:
		    case LANG:
		    case LANGMATCHES:
		    case IS_URI:
		    case IS_IRI:
		    case IS_BLANK:
		    case IS_LITERAL:
		    case IS_NUMERIC:
		    case REGEX:
		    case SAME_TERM:
		    case RAND:
		    case ABS:
		    case CEIL:
		    case FLOOR:
		    case ROUND:
		    case CONCAT:
		    case SUBSTR:
		    case STRLEN:
		    case REPLACE:
		    case UCASE:
		    case LCASE:
		    case ENCODE_FOR_URI:
		    case CONTAINS:
		    case STRSTARTS:
		    case STRENDS:
		    case STRBEFORE:
		    case STRAFTER:
		    case YEAR:
		    case MONTH:
		    case DAY:
		    case HOURS:
		    case MINUTES:
		    case SECONDS:
		    case TIMEZONE:
		    case TZ:
		    case NOW:
		    case UUID:
		    case STRUUID:
		    case MD5:
		    case SHA1:
		    case SHA256:
		    case SHA384:
		    case SHA512:
		    case TRUE:
		    case FALSE:
		    case INTEGER:
		    case DECIMAL:
		    case DOUBLE:
		    case INTEGER_POSITIVE:
		    case DECIMAL_POSITIVE:
		    case DOUBLE_POSITIVE:
		    case INTEGER_NEGATIVE:
		    case DECIMAL_NEGATIVE:
		    case DOUBLE_NEGATIVE:
		    case STRING_LITERAL1:
		    case STRING_LITERAL2:
		    case STRING_LITERAL_LONG1:
		    case STRING_LITERAL_LONG2:
		    case LPAREN:
		      expr = PrimaryExpression();
		                                 {if (true) return expr ;}
		      break;
		    default:
		      jj_la1[136] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    throw new Error("Missing return statement in function");
		  }

		  final public Expr PrimaryExpression() throws ParseException {
		                             Expr expr ; Node gn ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case LPAREN:
		      expr = BrackettedExpression();
		                                    {if (true) return expr ;}
		      break;
		    case EXISTS:
		    case NOT:
		    case COUNT:
		    case MIN:
		    case MAX:
		    case SUM:
		    case AVG:
		    case SAMPLE:
		    case GROUP_CONCAT:
		    case BOUND:
		    case COALESCE:
		    case IF:
		    case BNODE:
		    case IRI:
		    case URI:
		    case STR:
		    case STRLANG:
		    case STRDT:
		    case DTYPE:
		    case LANG:
		    case LANGMATCHES:
		    case IS_URI:
		    case IS_IRI:
		    case IS_BLANK:
		    case IS_LITERAL:
		    case IS_NUMERIC:
		    case REGEX:
		    case SAME_TERM:
		    case RAND:
		    case ABS:
		    case CEIL:
		    case FLOOR:
		    case ROUND:
		    case CONCAT:
		    case SUBSTR:
		    case STRLEN:
		    case REPLACE:
		    case UCASE:
		    case LCASE:
		    case ENCODE_FOR_URI:
		    case CONTAINS:
		    case STRSTARTS:
		    case STRENDS:
		    case STRBEFORE:
		    case STRAFTER:
		    case YEAR:
		    case MONTH:
		    case DAY:
		    case HOURS:
		    case MINUTES:
		    case SECONDS:
		    case TIMEZONE:
		    case TZ:
		    case NOW:
		    case UUID:
		    case STRUUID:
		    case MD5:
		    case SHA1:
		    case SHA256:
		    case SHA384:
		    case SHA512:
		      expr = BuiltInCall();
		                           {if (true) return expr ;}
		      break;
		    case IRIref:
		    case PNAME_NS:
		    case PNAME_LN:
		      expr = iriOrFunction();
		                             {if (true) return expr ;}
		      break;
		    case STRING_LITERAL1:
		    case STRING_LITERAL2:
		    case STRING_LITERAL_LONG1:
		    case STRING_LITERAL_LONG2:
		      gn = RDFLiteral();
		                        {if (true) return asExpr(gn) ;}
		      break;
		    case INTEGER:
		    case DECIMAL:
		    case DOUBLE:
		    case INTEGER_POSITIVE:
		    case DECIMAL_POSITIVE:
		    case DOUBLE_POSITIVE:
		    case INTEGER_NEGATIVE:
		    case DECIMAL_NEGATIVE:
		    case DOUBLE_NEGATIVE:
		      gn = NumericLiteral();
		                            {if (true) return asExpr(gn) ;}
		      break;
		    case TRUE:
		    case FALSE:
		      gn = BooleanLiteral();
		                            {if (true) return asExpr(gn) ;}
		      break;
		    case VAR1:
		    case VAR2:
		      gn = Var();
		                 {if (true) return asExpr(gn) ;}
		      break;
		    default:
		      jj_la1[137] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    throw new Error("Missing return statement in function");
		  }

		  final public Expr BrackettedExpression() throws ParseException {
		                                Expr expr ;
		    jj_consume_token(LPAREN);
		    expr = Expression();
		    jj_consume_token(RPAREN);
		                                            {if (true) return expr ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Expr BuiltInCall() throws ParseException {
		                       Expr expr ; Expr expr1 = null ; Expr expr2 = null ;
		                       Node gn ; ExprList a ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case COUNT:
		    case MIN:
		    case MAX:
		    case SUM:
		    case AVG:
		    case SAMPLE:
		    case GROUP_CONCAT:
		      expr = Aggregate();
		                         {if (true) return expr ;}
		      break;
		    case STR:
		      jj_consume_token(STR);
		      jj_consume_token(LPAREN);
		      expr = Expression();
		      jj_consume_token(RPAREN);
		      {if (true) return new E_Str(expr) ;}
		      break;
		    case LANG:
		      jj_consume_token(LANG);
		      jj_consume_token(LPAREN);
		      expr = Expression();
		      jj_consume_token(RPAREN);
		      {if (true) return new E_Lang(expr) ;}
		      break;
		    case LANGMATCHES:
		      jj_consume_token(LANGMATCHES);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(COMMA);
		      expr2 = Expression();
		      jj_consume_token(RPAREN);
		      {if (true) return new E_LangMatches(expr1, expr2) ;}
		      break;
		    case DTYPE:
		      jj_consume_token(DTYPE);
		      jj_consume_token(LPAREN);
		      expr = Expression();
		      jj_consume_token(RPAREN);
		      {if (true) return new E_Datatype(expr) ;}
		      break;
		    case BOUND:
		      jj_consume_token(BOUND);
		      jj_consume_token(LPAREN);
		      gn = Var();
		      jj_consume_token(RPAREN);
		      {if (true) return new E_Bound(new ExprVar(gn)) ;}
		      break;
		    case IRI:
		      jj_consume_token(IRI);
		      jj_consume_token(LPAREN);
		      expr = Expression();
		      jj_consume_token(RPAREN);
		      {if (true) return new E_IRI(expr) ;}
		      break;
		    case URI:
		      jj_consume_token(URI);
		      jj_consume_token(LPAREN);
		      expr = Expression();
		      jj_consume_token(RPAREN);
		      {if (true) return new E_URI(expr) ;}
		      break;
		    case BNODE:
		      jj_consume_token(BNODE);
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case LPAREN:
		        jj_consume_token(LPAREN);
		        expr1 = Expression();
		        jj_consume_token(RPAREN);
		        {if (true) return new E_BNode(expr1) ;}
		        break;
		      case NIL:
		        jj_consume_token(NIL);
		              {if (true) return new E_BNode() ;}
		        break;
		      default:
		        jj_la1[138] = jj_gen;
		        jj_consume_token(-1);
		        throw new ParseException();
		      }
		      break;
		    case RAND:
		      jj_consume_token(RAND);
		      jj_consume_token(NIL);
		                   {if (true) return new E_Random() ;}
		      break;
		    case ABS:
		      jj_consume_token(ABS);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(RPAREN);
		                                                   {if (true) return new E_NumAbs(expr1) ;}
		      break;
		    case CEIL:
		      jj_consume_token(CEIL);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(RPAREN);
		                                                    {if (true) return new E_NumCeiling(expr1) ;}
		      break;
		    case FLOOR:
		      jj_consume_token(FLOOR);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(RPAREN);
		                                                     {if (true) return new E_NumFloor(expr1) ;}
		      break;
		    case ROUND:
		      jj_consume_token(ROUND);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(RPAREN);
		                                                     {if (true) return new E_NumRound(expr1) ;}
		      break;
		    case CONCAT:
		      jj_consume_token(CONCAT);
		      a = ExpressionList();
		                                    {if (true) return new E_StrConcat(a) ;}
		      break;
		    case SUBSTR:
		      expr = SubstringExpression();
		                                   {if (true) return expr ;}
		      break;
		    case STRLEN:
		      jj_consume_token(STRLEN);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(RPAREN);
		                                                      {if (true) return new E_StrLength(expr1) ;}
		      break;
		    case REPLACE:
		      expr = StrReplaceExpression();
		                                    {if (true) return expr ;}
		      break;
		    case UCASE:
		      jj_consume_token(UCASE);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(RPAREN);
		                                                     {if (true) return new E_StrUpperCase(expr1) ;}
		      break;
		    case LCASE:
		      jj_consume_token(LCASE);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(RPAREN);
		                                                     {if (true) return new E_StrLowerCase(expr1) ;}
		      break;
		    case ENCODE_FOR_URI:
		      jj_consume_token(ENCODE_FOR_URI);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(RPAREN);
		                                                              {if (true) return new E_StrEncodeForURI(expr1) ;}
		      break;
		    case CONTAINS:
		      jj_consume_token(CONTAINS);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(COMMA);
		      expr2 = Expression();
		      jj_consume_token(RPAREN);
		      {if (true) return new E_StrContains(expr1, expr2) ;}
		      break;
		    case STRSTARTS:
		      jj_consume_token(STRSTARTS);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(COMMA);
		      expr2 = Expression();
		      jj_consume_token(RPAREN);
		      {if (true) return new E_StrStartsWith(expr1, expr2) ;}
		      break;
		    case STRENDS:
		      jj_consume_token(STRENDS);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(COMMA);
		      expr2 = Expression();
		      jj_consume_token(RPAREN);
		      {if (true) return new E_StrEndsWith(expr1, expr2) ;}
		      break;
		    case STRBEFORE:
		      jj_consume_token(STRBEFORE);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(COMMA);
		      expr2 = Expression();
		      jj_consume_token(RPAREN);
		      {if (true) return new E_StrBefore(expr1, expr2) ;}
		      break;
		    case STRAFTER:
		      jj_consume_token(STRAFTER);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(COMMA);
		      expr2 = Expression();
		      jj_consume_token(RPAREN);
		      {if (true) return new E_StrAfter(expr1, expr2) ;}
		      break;
		    case YEAR:
		      jj_consume_token(YEAR);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(RPAREN);
		                                                    {if (true) return new E_DateTimeYear(expr1) ;}
		      break;
		    case MONTH:
		      jj_consume_token(MONTH);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(RPAREN);
		                                                     {if (true) return new E_DateTimeMonth(expr1) ;}
		      break;
		    case DAY:
		      jj_consume_token(DAY);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(RPAREN);
		                                                   {if (true) return new E_DateTimeDay(expr1) ;}
		      break;
		    case HOURS:
		      jj_consume_token(HOURS);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(RPAREN);
		                                                     {if (true) return new E_DateTimeHours(expr1) ;}
		      break;
		    case MINUTES:
		      jj_consume_token(MINUTES);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(RPAREN);
		                                                       {if (true) return new E_DateTimeMinutes(expr1) ;}
		      break;
		    case SECONDS:
		      jj_consume_token(SECONDS);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(RPAREN);
		                                                       {if (true) return new E_DateTimeSeconds(expr1) ;}
		      break;
		    case TIMEZONE:
		      jj_consume_token(TIMEZONE);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(RPAREN);
		                                                        {if (true) return new E_DateTimeTimezone(expr1) ;}
		      break;
		    case TZ:
		      jj_consume_token(TZ);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(RPAREN);
		                                                  {if (true) return new E_DateTimeTZ(expr1) ;}
		      break;
		    case NOW:
		      jj_consume_token(NOW);
		      jj_consume_token(NIL);
		                  {if (true) return new E_Now() ;}
		      break;
		    case UUID:
		      jj_consume_token(UUID);
		      jj_consume_token(NIL);
		                   {if (true) return new E_UUID() ;}
		      break;
		    case STRUUID:
		      jj_consume_token(STRUUID);
		      jj_consume_token(NIL);
		                      {if (true) return new E_StrUUID() ;}
		      break;
		    case MD5:
		      jj_consume_token(MD5);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(RPAREN);
		                                                   {if (true) return new E_MD5(expr1) ;}
		      break;
		    case SHA1:
		      jj_consume_token(SHA1);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(RPAREN);
		                                                    {if (true) return new E_SHA1(expr1) ;}
		      break;
		    case SHA256:
		      jj_consume_token(SHA256);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(RPAREN);
		                                                      {if (true) return new E_SHA256(expr1) ;}
		      break;
		    case SHA384:
		      jj_consume_token(SHA384);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(RPAREN);
		                                                      {if (true) return new E_SHA384(expr1) ;}
		      break;
		    case SHA512:
		      jj_consume_token(SHA512);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(RPAREN);
		                                                      {if (true) return new E_SHA512(expr1) ;}
		      break;
		    case COALESCE:
		      jj_consume_token(COALESCE);
		      a = ExpressionList();
		      {if (true) return new E_Coalesce(a) ;}
		      break;
		    case IF:
		      jj_consume_token(IF);
		      jj_consume_token(LPAREN);
		      expr = Expression();
		      jj_consume_token(COMMA);
		      expr1 = Expression();
		      jj_consume_token(COMMA);
		      expr2 = Expression();
		      jj_consume_token(RPAREN);
		      {if (true) return new E_Conditional(expr, expr1, expr2) ;}
		      break;
		    case STRLANG:
		      jj_consume_token(STRLANG);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(COMMA);
		      expr2 = Expression();
		      jj_consume_token(RPAREN);
		      {if (true) return new E_StrLang(expr1, expr2) ;}
		      break;
		    case STRDT:
		      jj_consume_token(STRDT);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(COMMA);
		      expr2 = Expression();
		      jj_consume_token(RPAREN);
		      {if (true) return new E_StrDatatype(expr1, expr2) ;}
		      break;
		    case SAME_TERM:
		      jj_consume_token(SAME_TERM);
		      jj_consume_token(LPAREN);
		      expr1 = Expression();
		      jj_consume_token(COMMA);
		      expr2 = Expression();
		      jj_consume_token(RPAREN);
		      {if (true) return new E_SameTerm(expr1, expr2) ;}
		      break;
		    case IS_IRI:
		      jj_consume_token(IS_IRI);
		      jj_consume_token(LPAREN);
		      expr = Expression();
		      jj_consume_token(RPAREN);
		      {if (true) return new E_IsIRI(expr) ;}
		      break;
		    case IS_URI:
		      jj_consume_token(IS_URI);
		      jj_consume_token(LPAREN);
		      expr = Expression();
		      jj_consume_token(RPAREN);
		      {if (true) return new E_IsURI(expr) ;}
		      break;
		    case IS_BLANK:
		      jj_consume_token(IS_BLANK);
		      jj_consume_token(LPAREN);
		      expr = Expression();
		      jj_consume_token(RPAREN);
		      {if (true) return new E_IsBlank(expr) ;}
		      break;
		    case IS_LITERAL:
		      jj_consume_token(IS_LITERAL);
		      jj_consume_token(LPAREN);
		      expr = Expression();
		      jj_consume_token(RPAREN);
		      {if (true) return new E_IsLiteral(expr) ;}
		      break;
		    case IS_NUMERIC:
		      jj_consume_token(IS_NUMERIC);
		      jj_consume_token(LPAREN);
		      expr = Expression();
		      jj_consume_token(RPAREN);
		      {if (true) return new E_IsNumeric(expr) ;}
		      break;
		    case REGEX:
		      expr = RegexExpression();
		                               {if (true) return expr ;}
		      break;
		    case EXISTS:
		      expr = ExistsFunc();
		                          {if (true) return expr ;}
		      break;
		    case NOT:
		      expr = NotExistsFunc();
		                             {if (true) return expr ;}
		      break;
		    default:
		      jj_la1[139] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    throw new Error("Missing return statement in function");
		  }

		  final public Expr RegexExpression() throws ParseException {
		  Expr expr ; Expr patExpr = null ; Expr flagsExpr = null ;
		    jj_consume_token(REGEX);
		    jj_consume_token(LPAREN);
		    expr = Expression();
		    jj_consume_token(COMMA);
		    patExpr = Expression();
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case COMMA:
		      jj_consume_token(COMMA);
		      flagsExpr = Expression();
		      break;
		    default:
		      jj_la1[140] = jj_gen;
		      ;
		    }
		    jj_consume_token(RPAREN);
		        {if (true) return new E_Regex(expr, patExpr, flagsExpr) ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Expr SubstringExpression() throws ParseException {
		  Expr expr1 ; Expr expr2 = null ; Expr expr3 = null ;
		    jj_consume_token(SUBSTR);
		    jj_consume_token(LPAREN);
		    expr1 = Expression();
		    jj_consume_token(COMMA);
		    expr2 = Expression();
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case COMMA:
		      jj_consume_token(COMMA);
		      expr3 = Expression();
		      break;
		    default:
		      jj_la1[141] = jj_gen;
		      ;
		    }
		    jj_consume_token(RPAREN);
		        {if (true) return new E_StrSubstring(expr1, expr2, expr3) ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Expr StrReplaceExpression() throws ParseException {
		  Expr expr1 ; Expr expr2 = null ; Expr expr3 = null ; Expr expr4 = null ;
		    jj_consume_token(REPLACE);
		    jj_consume_token(LPAREN);
		    expr1 = Expression();
		    jj_consume_token(COMMA);
		    expr2 = Expression();
		    jj_consume_token(COMMA);
		    expr3 = Expression();
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case COMMA:
		      jj_consume_token(COMMA);
		      expr4 = Expression();
		      break;
		    default:
		      jj_la1[142] = jj_gen;
		      ;
		    }
		    jj_consume_token(RPAREN);
		    {if (true) return new E_StrReplace(expr1,expr2,expr3,expr4) ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Expr ExistsFunc() throws ParseException {
		                      Element el ;
		    jj_consume_token(EXISTS);
		    el = GroupGraphPattern();
		     {if (true) return createExprExists(el) ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Expr NotExistsFunc() throws ParseException {
		                         Element el ;
		    jj_consume_token(NOT);
		    jj_consume_token(EXISTS);
		    el = GroupGraphPattern();
		     {if (true) return createExprNotExists(el) ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Expr Aggregate() throws ParseException {
		                     Aggregator agg = null ; String sep = null ;
		                     Expr expr = null ; Expr expr2 = null ;
		                     boolean distinct = false ;
		                     ExprList ordered = new ExprList() ;
		                     Token t ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case COUNT:
		      t = jj_consume_token(COUNT);
		      jj_consume_token(LPAREN);
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case DISTINCT:
		        jj_consume_token(DISTINCT);
		                   distinct = true ;
		        break;
		      default:
		        jj_la1[143] = jj_gen;
		        ;
		      }
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case STAR:
		        jj_consume_token(STAR);
		        break;
		      case IRIref:
		      case PNAME_NS:
		      case PNAME_LN:
		      case VAR1:
		      case VAR2:
		      case EXISTS:
		      case NOT:
		      case COUNT:
		      case MIN:
		      case MAX:
		      case SUM:
		      case AVG:
		      case SAMPLE:
		      case GROUP_CONCAT:
		      case BOUND:
		      case COALESCE:
		      case IF:
		      case BNODE:
		      case IRI:
		      case URI:
		      case STR:
		      case STRLANG:
		      case STRDT:
		      case DTYPE:
		      case LANG:
		      case LANGMATCHES:
		      case IS_URI:
		      case IS_IRI:
		      case IS_BLANK:
		      case IS_LITERAL:
		      case IS_NUMERIC:
		      case REGEX:
		      case SAME_TERM:
		      case RAND:
		      case ABS:
		      case CEIL:
		      case FLOOR:
		      case ROUND:
		      case CONCAT:
		      case SUBSTR:
		      case STRLEN:
		      case REPLACE:
		      case UCASE:
		      case LCASE:
		      case ENCODE_FOR_URI:
		      case CONTAINS:
		      case STRSTARTS:
		      case STRENDS:
		      case STRBEFORE:
		      case STRAFTER:
		      case YEAR:
		      case MONTH:
		      case DAY:
		      case HOURS:
		      case MINUTES:
		      case SECONDS:
		      case TIMEZONE:
		      case TZ:
		      case NOW:
		      case UUID:
		      case STRUUID:
		      case MD5:
		      case SHA1:
		      case SHA256:
		      case SHA384:
		      case SHA512:
		      case TRUE:
		      case FALSE:
		      case INTEGER:
		      case DECIMAL:
		      case DOUBLE:
		      case INTEGER_POSITIVE:
		      case DECIMAL_POSITIVE:
		      case DOUBLE_POSITIVE:
		      case INTEGER_NEGATIVE:
		      case DECIMAL_NEGATIVE:
		      case DOUBLE_NEGATIVE:
		      case STRING_LITERAL1:
		      case STRING_LITERAL2:
		      case STRING_LITERAL_LONG1:
		      case STRING_LITERAL_LONG2:
		      case LPAREN:
		      case BANG:
		      case PLUS:
		      case MINUS:
		        expr = Expression();
		        break;
		      default:
		        jj_la1[144] = jj_gen;
		        jj_consume_token(-1);
		        throw new ParseException();
		      }
		      jj_consume_token(RPAREN);
		      if ( expr == null ) { agg = AggregatorFactory.createCount(distinct) ; }
		      if ( expr != null ) { agg = AggregatorFactory.createCountExpr(distinct, expr) ; }
		      break;
		    case SUM:
		      t = jj_consume_token(SUM);
		      jj_consume_token(LPAREN);
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case DISTINCT:
		        jj_consume_token(DISTINCT);
		                                      distinct = true ;
		        break;
		      default:
		        jj_la1[145] = jj_gen;
		        ;
		      }
		      expr = Expression();
		      jj_consume_token(RPAREN);
		      agg = AggregatorFactory.createSum(distinct, expr) ;
		      break;
		    case MIN:
		      t = jj_consume_token(MIN);
		      jj_consume_token(LPAREN);
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case DISTINCT:
		        jj_consume_token(DISTINCT);
		                                      distinct = true ;
		        break;
		      default:
		        jj_la1[146] = jj_gen;
		        ;
		      }
		      expr = Expression();
		      jj_consume_token(RPAREN);
		      agg = AggregatorFactory.createMin(distinct, expr) ;
		      break;
		    case MAX:
		      t = jj_consume_token(MAX);
		      jj_consume_token(LPAREN);
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case DISTINCT:
		        jj_consume_token(DISTINCT);
		                                      distinct = true ;
		        break;
		      default:
		        jj_la1[147] = jj_gen;
		        ;
		      }
		      expr = Expression();
		      jj_consume_token(RPAREN);
		      agg = AggregatorFactory.createMax(distinct, expr) ;
		      break;
		    case AVG:
		      t = jj_consume_token(AVG);
		      jj_consume_token(LPAREN);
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case DISTINCT:
		        jj_consume_token(DISTINCT);
		                                      distinct = true ;
		        break;
		      default:
		        jj_la1[148] = jj_gen;
		        ;
		      }
		      expr = Expression();
		      jj_consume_token(RPAREN);
		      agg = AggregatorFactory.createAvg(distinct, expr) ;
		      break;
		    case SAMPLE:
		      t = jj_consume_token(SAMPLE);
		      jj_consume_token(LPAREN);
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case DISTINCT:
		        jj_consume_token(DISTINCT);
		                                         distinct = true ;
		        break;
		      default:
		        jj_la1[149] = jj_gen;
		        ;
		      }
		      expr = Expression();
		      jj_consume_token(RPAREN);
		      agg = AggregatorFactory.createSample(distinct, expr) ;
		      break;
		    case GROUP_CONCAT:
		      t = jj_consume_token(GROUP_CONCAT);
		      jj_consume_token(LPAREN);
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case DISTINCT:
		        t = jj_consume_token(DISTINCT);
		                      distinct = true ;
		        break;
		      default:
		        jj_la1[150] = jj_gen;
		        ;
		      }
		      expr = Expression();
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case SEMICOLON:
		        jj_consume_token(SEMICOLON);
		        jj_consume_token(SEPARATOR);
		        jj_consume_token(EQ);
		        sep = String();
		        break;
		      default:
		        jj_la1[151] = jj_gen;
		        ;
		      }
		      jj_consume_token(RPAREN);
		      agg = AggregatorFactory.createGroupConcat(distinct, expr, sep, ordered) ;
		      break;
		    default:
		      jj_la1[152] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    if ( ! getAllowAggregatesInExpressions() )
		           throwParseException("Aggregate expression not legal at this point",
		                                t.beginLine, t.beginColumn) ;
		    Expr exprAgg = getQuery().allocAggregate(agg) ;
		    {if (true) return exprAgg ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Expr iriOrFunction() throws ParseException {
		                         String iri ; Args a = null ;
		    iri = iri();
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case LPAREN:
		    case NIL:
		      a = ArgList();
		      break;
		    default:
		      jj_la1[153] = jj_gen;
		      ;
		    }
		    if ( a == null )
		       {if (true) return asExpr(createNode(iri)) ;}
		    if ( AggregateRegistry.isRegistered(iri) ) {
		         if ( ! getAllowAggregatesInExpressions() )
		            throwParseException("Aggregate expression not legal at this point : "+iri, -1, -1) ;
		         Aggregator agg = AggregatorFactory.createCustom(iri, a) ;
		         Expr exprAgg = getQuery().allocAggregate(agg) ;
		         {if (true) return exprAgg ;}
		      }
		    {if (true) return new E_Function(iri, a) ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Node RDFLiteral() throws ParseException {
		                      Token t ; String lex = null ;
		    lex = String();
		    String lang = null ; String uri = null ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case LANGTAG:
		    case DATATYPE:
		      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		      case LANGTAG:
		        t = jj_consume_token(LANGTAG);
		                      lang = stripChars(t.image, 1) ;
		        break;
		      case DATATYPE:
		        jj_consume_token(DATATYPE);
		        uri = iri();
		        break;
		      default:
		        jj_la1[154] = jj_gen;
		        jj_consume_token(-1);
		        throw new ParseException();
		      }
		      break;
		    default:
		      jj_la1[155] = jj_gen;
		      ;
		    }
		      {if (true) return createLiteral(lex, lang, uri) ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Node NumericLiteral() throws ParseException {
		                          Node n ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case INTEGER:
		    case DECIMAL:
		    case DOUBLE:
		      n = NumericLiteralUnsigned();
		      break;
		    case INTEGER_POSITIVE:
		    case DECIMAL_POSITIVE:
		    case DOUBLE_POSITIVE:
		      n = NumericLiteralPositive();
		      break;
		    case INTEGER_NEGATIVE:
		    case DECIMAL_NEGATIVE:
		    case DOUBLE_NEGATIVE:
		      n = NumericLiteralNegative();
		      break;
		    default:
		      jj_la1[156] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    {if (true) return n ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public Node NumericLiteralUnsigned() throws ParseException {
		                                  Token t ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case INTEGER:
		      t = jj_consume_token(INTEGER);
		                  {if (true) return createLiteralInteger(t.image) ;}
		      break;
		    case DECIMAL:
		      t = jj_consume_token(DECIMAL);
		                  {if (true) return createLiteralDecimal(t.image) ;}
		      break;
		    case DOUBLE:
		      t = jj_consume_token(DOUBLE);
		                 {if (true) return createLiteralDouble(t.image) ;}
		      break;
		    default:
		      jj_la1[157] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    throw new Error("Missing return statement in function");
		  }

		  final public Node NumericLiteralPositive() throws ParseException {
		                                  Token t ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case INTEGER_POSITIVE:
		      t = jj_consume_token(INTEGER_POSITIVE);
		                           {if (true) return createLiteralInteger(t.image) ;}
		      break;
		    case DECIMAL_POSITIVE:
		      t = jj_consume_token(DECIMAL_POSITIVE);
		                           {if (true) return createLiteralDecimal(t.image) ;}
		      break;
		    case DOUBLE_POSITIVE:
		      t = jj_consume_token(DOUBLE_POSITIVE);
		                          {if (true) return createLiteralDouble(t.image) ;}
		      break;
		    default:
		      jj_la1[158] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    throw new Error("Missing return statement in function");
		  }

		  final public Node NumericLiteralNegative() throws ParseException {
		                                  Token t ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case INTEGER_NEGATIVE:
		      t = jj_consume_token(INTEGER_NEGATIVE);
		                           {if (true) return createLiteralInteger(t.image) ;}
		      break;
		    case DECIMAL_NEGATIVE:
		      t = jj_consume_token(DECIMAL_NEGATIVE);
		                           {if (true) return createLiteralDecimal(t.image) ;}
		      break;
		    case DOUBLE_NEGATIVE:
		      t = jj_consume_token(DOUBLE_NEGATIVE);
		                          {if (true) return createLiteralDouble(t.image) ;}
		      break;
		    default:
		      jj_la1[159] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    throw new Error("Missing return statement in function");
		  }

		  final public Node BooleanLiteral() throws ParseException {
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case TRUE:
		      jj_consume_token(TRUE);
		           {if (true) return XSD_TRUE ;}
		      break;
		    case FALSE:
		      jj_consume_token(FALSE);
		            {if (true) return XSD_FALSE ;}
		      break;
		    default:
		      jj_la1[160] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    throw new Error("Missing return statement in function");
		  }

		  final public String String() throws ParseException {
		                    Token t ; String lex ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case STRING_LITERAL1:
		      t = jj_consume_token(STRING_LITERAL1);
		                            lex = stripQuotes(t.image) ;
		      break;
		    case STRING_LITERAL2:
		      t = jj_consume_token(STRING_LITERAL2);
		                            lex = stripQuotes(t.image) ;
		      break;
		    case STRING_LITERAL_LONG1:
		      t = jj_consume_token(STRING_LITERAL_LONG1);
		                                 lex = stripQuotes3(t.image) ;
		      break;
		    case STRING_LITERAL_LONG2:
		      t = jj_consume_token(STRING_LITERAL_LONG2);
		                                 lex = stripQuotes3(t.image) ;
		      break;
		    default:
		      jj_la1[161] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		      lex = unescapeStr(lex, t.beginLine, t.beginColumn) ;
		      {if (true) return lex ;}
		    throw new Error("Missing return statement in function");
		  }

		  final public String iri() throws ParseException {
		                 String iri ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case IRIref:
		      iri = IRIREF();
		                   {if (true) return iri ;}
		      break;
		    case PNAME_NS:
		    case PNAME_LN:
		      iri = PrefixedName();
		                         {if (true) return iri ;}
		      break;
		    default:
 		      jj_la1[162] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    throw new Error("Missing return statement in function");
		  }

		  final public String PrefixedName() throws ParseException {
		                          Token t ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case PNAME_LN:
		      t = jj_consume_token(PNAME_LN);
		      {if (true) return resolvePName(t.image, t.beginLine, t.beginColumn) ;}
		      break;
		    case PNAME_NS:
		      t = jj_consume_token(PNAME_NS);
		      {if (true) return resolvePName(t.image, t.beginLine, t.beginColumn) ;}
		      break;
		    default:
		      jj_la1[163] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    throw new Error("Missing return statement in function");
		  }

		  final public Node BlankNode() throws ParseException {
		                     Token t = null ;
		    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
		    case BLANK_NODE_LABEL:
		      t = jj_consume_token(BLANK_NODE_LABEL);
		      {if (true) return createBNode(t.image, t.beginLine, t.beginColumn) ;}
		      break;
		    case ANON:
		      t = jj_consume_token(ANON);
		               {if (true) return createBNode(t.beginLine, t.beginColumn) ;}
		      break;
		    default:
		      jj_la1[164] = jj_gen;
		      jj_consume_token(-1);
		      throw new ParseException();
		    }
		    throw new Error("Missing return statement in function");
		  }

		  final public String IRIREF() throws ParseException {
		                    Token t ;
		    t = jj_consume_token(IRIref);
		    {if (true) return resolveQuotedIRI(t.image, t.beginLine, t.beginColumn) ;}
		    throw new Error("Missing return statement in function");
		  }
		  
		  final public Node EmbeddedTriple() throws ParseException {
		        Token t = jj_consume_token(LT);
		        token = token.next = token_source.getNextToken();
		        Node s = VarOrTerm();
		        Node p = VarOrTerm();
		        Node o = VarOrTerm();
		        t = jj_consume_token(GT);
		        token = token.next = token_source.getNextToken();
		       
				return NodeFactoryStar.createEmbeddedNode(s, p, o);
		  }


		  /** Generated Token Manager. */
		  public SPARQLParser11TokenManager token_source;
		  JavaCharStream jj_input_stream;
		  /** Current token. */
		  public Token token;
		  /** Next token. */
		  public Token jj_nt;
		  private int jj_ntk;
		  private int jj_gen;
		  final private int[] jj_la1 = new int[165];
		  static private int[] jj_la1_0;
		  static private int[] jj_la1_1;
		  static private int[] jj_la1_2;
		  static private int[] jj_la1_3;
		  static private int[] jj_la1_4;
		  static private int[] jj_la1_5;
		  static private int[] jj_la1_6;
		  static {
		      jj_la1_init_0();
		      jj_la1_init_1();
		      jj_la1_init_2();
		      jj_la1_init_3();
		      jj_la1_init_4();
		      jj_la1_init_5();
		      jj_la1_init_6();
		   }
		   private static void jj_la1_init_0() {
		      jj_la1_0 = new int[] {0xe400000,0x200,0x300000,0x300000,0x0,0x1800000,0x1800000,0xc000,0xc000,0xc000,0x0,0x0,0xfc00,0x0,0xdc00,0xdc00,0x0,0x0,0x0,0x1c00,0x0,0x0,0x0,0x40000000,0x30000000,0xdc00,0x0,0xdc00,0x1c00,0xdc00,0x0,0xdc00,0xdc00,0x20000000,0x10000000,0x30000000,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x1c00,0x0,0x1c00,0x0,0xfc00,0x0,0x0,0xfc00,0xfc00,0xfc00,0x0,0x400000,0xfc00,0x0,0x0,0xfc00,0xfc00,0x0,0x0,0x0,0xc000,0x1c00,0xc000,0x0,0x0,0x1c00,0x0,0x1c00,0x0,0x1c00,0x800000,0x0,0x0,0x0,0x0,0xfc00,0xfc00,0x0,0xfc00,0x8dc00,0x0,0x8dc00,0x8dc00,0x0,0xfc00,0x8dc00,0x8dc00,0x0,0x8dc00,0x8dc00,0x0,0x0,0x0,0x0,0x81c00,0x0,0x81c00,0x0,0x81c00,0x81c00,0x81c00,0x81c00,0x0,0x0,0xfc00,0xfc00,0xfc00,0xfc00,0xfc00,0xdc00,0xfc00,0xc000,0x3c00,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0xdc00,0xdc00,0x0,0x0,0x0,0x0,0x0,0x800000,0xdc00,0x800000,0x800000,0x800000,0x800000,0x800000,0x800000,0x0,0x0,0x0,0x10000,0x10000,0x0,0x0,0x0,0x0,0x0,0x0,0x1c00,0x1800,0x2000,};
		   }
		   private static void jj_la1_init_1() {
		      jj_la1_1 = new int[] {0x0,0x0,0x0,0x0,0x20,0x0,0x0,0x0,0x0,0x0,0x20,0x20,0x0,0x60,0x0,0x0,0x20,0x40,0x20,0x10,0x40,0x20000,0x40000,0x0,0x0,0x3e0c000,0x10000,0x3e0c000,0x3e0c000,0x3e0c00c,0xc,0x3e0c000,0x3e0c00c,0x0,0x0,0x0,0x1,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x10,0x100,0x100,0x110,0x0,0x100,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x3b01,0x0,0x0,0x0,0x0,0x3b01,0x0,0x0,0x2,0x0,0x0,0x0,0x2,0x0,0x2,0x400,0x3e0c000,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x8000,0x8000,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x3e0c000,0x3e0c000,0x0,0x3e0c000,0x0,0x0,0x0,0x0,0x3e0c000,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x3e00000,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,};
		   }
		   private static void jj_la1_init_2() {
		      jj_la1_2 = new int[] {0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0xffffffdb,0x0,0xffffffdb,0xffffffdb,0xffffffdb,0x0,0xffffffdb,0xffffffdb,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x4,0x0,0x0,0x0,0x0,0x4,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0xffffffdb,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x20,0x20,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0xffffffdb,0xffffffdb,0x0,0xffffffdb,0x0,0x0,0x0,0x0,0xffffffdb,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x3,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,};
		   }
		   private static void jj_la1_init_3() {
		      jj_la1_3 = new int[] {0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x6000000,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x1dfffff,0x0,0x1dfffff,0x1dfffff,0x1dfffff,0x0,0x1dfffff,0x1dfffff,0x0,0x0,0x0,0x0,0x0,0xf0000000,0xf0000000,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x10000000,0x30000000,0x0,0x0,0x0,0x0,0x0,0x6000000,0x0,0x0,0x6000000,0x6000000,0x6000000,0x0,0x0,0x6000000,0x0,0x0,0x6000000,0x6000000,0x0,0x0,0x0,0x0,0x6000000,0x0,0x0,0x0,0x6000000,0x0,0x6000000,0x0,0x1dfffff,0x0,0x0,0x0,0x0,0x0,0x6000000,0x6000000,0x0,0x6000000,0x0,0x0,0x0,0x0,0x0,0x6000000,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x6000000,0x6000000,0x6000000,0x6000000,0x6000000,0x0,0x0,0x0,0x6000000,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x7dfffff,0x7dfffff,0x0,0x1dfffff,0x0,0x0,0x0,0x0,0x7dfffff,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x6000000,0x0,0x0,0x0,0x0,};
		   }
		   private static void jj_la1_init_4() {
		      jj_la1_4 = new int[] {0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0xc3fe0000,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x427f,0x427f,0x100,0x400,0x100,0x100,0x100,0x100,0x100,0x100,0x4000,0x0,0x0,0x8000,0x0,0x0,0x1000,0x3000,0xc3fe0000,0x0,0x0,0xc3fe0000,0xc3fe0000,0xc3fe0000,0x0,0x0,0xc3fe0000,0x0,0x0,0xc3fe0000,0xc3fe0000,0x0,0x0,0x100,0x0,0xc3fe0000,0x0,0x0,0x0,0xc3fe0000,0x0,0xc3fe0000,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0xc3fe0000,0xc3fe0000,0x0,0xc3fe0000,0x0,0x0,0x0,0x0,0x0,0xc3fe0000,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0xc3fe0000,0xc3fe0000,0xc3fe0000,0xc3fe0000,0xc3fe0000,0x0,0x0,0x0,0xc3fe0000,0x0,0x0,0x0,0x0,0x3f00000,0x3f00000,0x0,0x0,0x3f00000,0x0,0x0,0xc3fe0000,0xc3fe0000,0x0,0x0,0x0,0x0,0x0,0x0,0xc3fe0000,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x3fe0000,0xe0000,0x700000,0x3800000,0x0,0xc0000000,0x0,0x0,0x0,};
		   }
		   private static void jj_la1_init_5() {
		      jj_la1_5 = new int[] {0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x4,0x4,0x4000004,0x0,0x0,0x297,0x20,0x0,0x4000000,0x0,0x20,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x4,0x0,0x4,0x4,0x4,0x0,0x4,0x4,0x0,0x0,0x0,0x0,0x400,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x297,0x0,0x1000,0x297,0x297,0x297,0x1000,0x0,0x297,0x20,0x1000,0x297,0x297,0x1000,0x20,0x0,0x14,0x3,0x0,0x14,0x14,0x3,0x14,0x3,0x0,0x4,0x0,0x800,0x14,0x800,0x14,0x297,0x297,0x1000,0x297,0x0,0x400,0x0,0x0,0x800,0x297,0x80080004,0x80080004,0x400,0x80080004,0x80080004,0x800,0x40000000,0x8000000,0x5000000,0x80080004,0x5000000,0x80004,0x40000000,0x80000000,0x80000004,0x0,0x80000000,0x84,0x84,0x297,0x297,0x297,0x297,0x213,0x0,0x200,0x0,0x213,0x400000,0x800000,0x7e000,0x7e000,0x3000000,0x0,0xc000000,0xc000000,0x3000000,0xc000000,0xc000000,0x3080007,0x7,0x14,0x0,0x800,0x800,0x800,0x0,0x7080007,0x0,0x0,0x0,0x0,0x0,0x0,0x400,0x0,0x14,0x10000000,0x10000000,0x0,0x0,0x0,0x0,0x0,0x3,0x0,0x0,0x200,};
		   }
		   private static void jj_la1_init_6() {
		      jj_la1_6 = new int[] {0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x4,0x0,0x4,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,};
		   }

		  /** Constructor with InputStream. */
		  public SPARQLStarParser11(java.io.InputStream stream) {
		     this(stream, null);
		  }
		  /** Constructor with InputStream and supplied encoding */
		  public SPARQLStarParser11(java.io.InputStream stream, String encoding) {
		    try { jj_input_stream = new JavaCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
		    token_source = new SPARQLParser11TokenManager(jj_input_stream);
		    token = new Token();
		    jj_ntk = -1;
		    jj_gen = 0;
		    for (int i = 0; i < 165; i++) jj_la1[i] = -1;
		  }

		  /** Reinitialise. */
		  public void ReInit(java.io.InputStream stream) {
		     ReInit(stream, null);
		  }
		  /** Reinitialise. */
		  public void ReInit(java.io.InputStream stream, String encoding) {
		    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
		    token_source.ReInit(jj_input_stream);
		    token = new Token();
		    jj_ntk = -1;
		    jj_gen = 0;
		    for (int i = 0; i < 165; i++) jj_la1[i] = -1;
		  }

		  /** Constructor. */
		  public SPARQLStarParser11(java.io.Reader stream) {
		    jj_input_stream = new JavaCharStream(stream, 1, 1);
		    token_source = new SPARQLParser11TokenManager(jj_input_stream);
		    token = new Token();
		    jj_ntk = -1;
		    jj_gen = 0;
		    for (int i = 0; i < 165; i++) jj_la1[i] = -1;
		  }

		  /** Reinitialise. */
		  public void ReInit(java.io.Reader stream) {
		    jj_input_stream.ReInit(stream, 1, 1);
		    token_source.ReInit(jj_input_stream);
		    token = new Token();
		    jj_ntk = -1;
		    jj_gen = 0;
		    for (int i = 0; i < 165; i++) jj_la1[i] = -1;
		  }

		  /** Constructor with generated Token Manager. */
		  public SPARQLStarParser11(SPARQLParser11TokenManager tm) {
		    token_source = tm;
		    token = new Token();
		    jj_ntk = -1;
		    jj_gen = 0;
		    for (int i = 0; i < 165; i++) jj_la1[i] = -1;
		  }

		  /** Reinitialise. */
		  public void ReInit(SPARQLParser11TokenManager tm) {
		    token_source = tm;
		    token = new Token();
		    jj_ntk = -1;
		    jj_gen = 0;
		    for (int i = 0; i < 165; i++) jj_la1[i] = -1;
		  }

		  private Token jj_consume_token(int kind) throws ParseException {
		    Token oldToken;
		    if ((oldToken = token).next != null) token = token.next;
		    else token = token.next = token_source.getNextToken();
		    jj_ntk = -1;
		    if (token.kind == kind) {
		      jj_gen++;
		      return token;
		    }
		    token = oldToken;
		    jj_kind = kind;
		    throw generateParseException();
		  }


		/** Get the next Token. */
		  final public Token getNextToken() {
		    if (token.next != null) token = token.next;
		    else token = token.next = token_source.getNextToken();
		    jj_ntk = -1;
		    jj_gen++;
		    return token;
		  }

		/** Get the specific Token. */
		  final public Token getToken(int index) {
		    Token t = token;
		    for (int i = 0; i < index; i++) {
		      if (t.next != null) t = t.next;
		      else t = t.next = token_source.getNextToken();
		    }
		    return t;
		  }

		  private int jj_ntk() {
		    if ((jj_nt=token.next) == null)
		      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
		    else
		      return (jj_ntk = jj_nt.kind);
		  }

		  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
		  private int[] jj_expentry;
		  private int jj_kind = -1;

		  /** Generate ParseException. */
		  public ParseException generateParseException() {
		    jj_expentries.clear();
		    boolean[] la1tokens = new boolean[206];
		    if (jj_kind >= 0) {
		      la1tokens[jj_kind] = true;
		      jj_kind = -1;
		    }
		    for (int i = 0; i < 165; i++) {
		      if (jj_la1[i] == jj_gen) {
		        for (int j = 0; j < 32; j++) {
		          if ((jj_la1_0[i] & (1<<j)) != 0) {
		            la1tokens[j] = true;
		          }
		          if ((jj_la1_1[i] & (1<<j)) != 0) {
		            la1tokens[32+j] = true;
		          }
		          if ((jj_la1_2[i] & (1<<j)) != 0) {
		            la1tokens[64+j] = true;
		          }
		          if ((jj_la1_3[i] & (1<<j)) != 0) {
		            la1tokens[96+j] = true;
		          }
		          if ((jj_la1_4[i] & (1<<j)) != 0) {
		            la1tokens[128+j] = true;
		          }
		          if ((jj_la1_5[i] & (1<<j)) != 0) {
		            la1tokens[160+j] = true;
		          }
		          if ((jj_la1_6[i] & (1<<j)) != 0) {
		            la1tokens[192+j] = true;
		          }
		        }
		      }
		    }
		    for (int i = 0; i < 206; i++) {
		      if (la1tokens[i]) {
		        jj_expentry = new int[1];
		        jj_expentry[0] = i;
		        jj_expentries.add(jj_expentry);
		      }
		    }
		    int[][] exptokseq = new int[jj_expentries.size()][];
		    for (int i = 0; i < jj_expentries.size(); i++) {
		      exptokseq[i] = jj_expentries.get(i);
		    }
		    return new ParseException(token, exptokseq, tokenImage);
		  }

		  /** Enable tracing. */
		  final public void enable_tracing() {
		  }

		  /** Disable tracing. */
		  final public void disable_tracing() {
		  }

		  @Override
		  protected Node createLiteralInteger(String lexicalForm) {
		        //return NodeFactory.createLiteral(lexicalForm, XSDDatatype.XSDinteger) ;
			  return NodeFactoryStar.createSimpleLiteralNode(lexicalForm,XSDDatatype.XSDinteger);
		  }

		  @Override
		  protected Node createLiteralDouble(String lexicalForm) {
		      //return NodeFactory.createLiteral(lexicalForm, XSDDatatype.XSDdouble) ;
			  return NodeFactoryStar.createSimpleLiteralNode(lexicalForm,XSDDatatype.XSDdouble);
		  }
		  @Override
		  protected Node createLiteralDecimal(String lexicalForm) {
			  return NodeFactoryStar.createSimpleLiteralNode(lexicalForm,XSDDatatype.XSDdecimal);
		      //return NodeFactory.createLiteral(lexicalForm, XSDDatatype.XSDdecimal) ;
		  }
		  
		  @Override
		  protected Node createLiteral(String lexicalForm, String langTag, String datatypeURI) {
		      Node n = null ;
		      // Can't have type and lang tag in parsing.
		      if ( datatypeURI != null ) {
		          RDFDatatype dType = TypeMapper.getInstance().getSafeTypeByName(datatypeURI) ;
		          //n = NodeFactory.createLiteral(lexicalForm, dType) ;
		          n = NodeFactoryStar.createSimpleLiteralNode(lexicalForm,dType);
		      } else if ( langTag != null && !langTag.isEmpty() )
		          //n = NodeFactory.createLiteral(lexicalForm, langTag) ;
		    	  n = NodeFactoryStar.createSimpleLiteralNode(lexicalForm,langTag);
		      else {
		          //n = NodeFactory.createLiteral(lexicalForm) ;
		      		n = NodeFactoryStar.createSimpleLiteralNode(lexicalForm);
		      }
		      return n ;
		  }
		    
		  @Override
		  protected Node createNode(String iri) {
			  if (iri.equals("a")) {
				  return NodeFactoryStar.createSimpleURINode("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
			}
	          return NodeFactoryStar.createSimpleURINode(iri) ;
		  }
		  
		  @Override
	      protected Node createBNode(int line, int column) {
	          return NodeFactoryStar.createSimpleBlankNode(super.createBNode(line, column));
	      }
		  
		  @Override
	      protected Node createBNode(String label, int line, int column) {
			  return NodeFactoryStar.createSimpleBlankNode(super.createBNode(label,line, column));
	      }
}

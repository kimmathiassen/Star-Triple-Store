package dk.aau.cs.qweb.main.queryparser;

import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.query.QueryVisitor;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.core.Prologue;
import org.apache.jena.sparql.serializer.FmtExprSPARQL;
import org.apache.jena.sparql.serializer.FmtTemplate;
import org.apache.jena.sparql.serializer.QuerySerializerFactory;
import org.apache.jena.sparql.serializer.SerializationContext;
import org.apache.jena.sparql.util.NodeToLabelMapBNode;

public class QuerySerializerFactoryStar implements QuerySerializerFactory {

	 @Override
     public QueryVisitor create(Syntax syntax, Prologue prologue, IndentedWriter writer) {
         // For the query pattern
         SerializationContext cxt1 = new SerializationContext(prologue, new NodeToLabelMapBNode("b", false));
         // For the construct pattern
         SerializationContext cxt2 = new SerializationContext(prologue, new NodeToLabelMapBNode("c", false));

         return new QueryStarSerializer(writer, new FormatterStarElement(writer, cxt1), new FmtExprSPARQL(writer, cxt1),
                 new FmtTemplate(writer, cxt2));
     }

     @Override
     public QueryVisitor create(Syntax syntax, SerializationContext context, IndentedWriter writer) {
         return new QueryStarSerializer(writer, new FormatterStarElement(writer, context), new FmtExprSPARQL(writer,
                 context), new FmtTemplate(writer, context));
     }

     @Override
     public boolean accept(Syntax syntax) {
         // Since ARQ syntax is a super set of SPARQL 1.1 both SPARQL 1.0
         // and SPARQL 1.1 can be serialized by the same serializer
         return SyntaxStar.syntaxSPARQL_Star.equals(syntax);
     }

}

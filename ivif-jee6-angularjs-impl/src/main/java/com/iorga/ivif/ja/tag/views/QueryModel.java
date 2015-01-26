package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.entities.EntityAttribute;
import com.iorga.ivif.ja.tag.entities.EntityAttributePreparedWaiter;
import com.iorga.ivif.tag.AbstractTarget;
import com.iorga.ivif.tag.bean.Query;
import org.apache.commons.lang3.BooleanUtils;
import org.datanucleus.query.compiler.JPQLParser;
import org.datanucleus.query.compiler.Node;
import org.datanucleus.query.compiler.NodeType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import static com.iorga.ivif.ja.tag.entities.EntityTargetFile.*;

public class QueryModel extends AbstractTarget<String, JAGeneratorContext> {
    private final Query element;
    private final EntityTargetFileId baseEntityId;
    private final Object waiter;
    private String queryDslCode;
    private List<QueryParameter> parameters;


    private class OperatorContext {
        private Node parameter;
        private List<Node> identifierPath = new ArrayList<>();
        private Node literal;
    }

    public static class QueryParameter {
        protected String name;
        protected String className;

        public String getName() {
            return name;
        }

        public String getClassName() {
            return className;
        }
    }


    public QueryModel(String id, Query element, EntityTargetFileId baseEntityId, Object waiter) {
        super(id);
        this.element = element;
        this.baseEntityId = baseEntityId;
        this.waiter = waiter;
    }


    @Override
    public void prepare(JAGeneratorContext context) throws Exception {
        super.prepare(context);

        parameters = new ArrayList<>();
        queryDslCode = null;

        if (element != null) {

            List<OperatorContext> parametersToResolve = new ArrayList<>();

            String where = element.getWhere();
            Node rootNode = new JPQLParser(null, null).parse(where);
            StringBuilder queryDslCode = new StringBuilder();
            visit(rootNode, queryDslCode, null, parametersToResolve);

            this.queryDslCode = queryDslCode.toString();

            // Now we will resolve the parameters and add them
            for (OperatorContext operatorContext : parametersToResolve) {
                // TODO handle parameters declared multiple times
                final QueryParameter parameter = new QueryParameter();
                parameter.name = (String) operatorContext.parameter.getNodeValue();
                parameters.add(parameter);
                // solve its type
                if (operatorContext.literal != null) {
                    // This is a literal parameter
                } else {
                    List<Node> identifierPath = operatorContext.identifierPath;
                    if (!"$record".equals(identifierPath.get(0).getNodeValue())) {
                        // TODO handle another root, if there are declared joins
                        throw new IllegalStateException("An identifier must always begin with '$record'. Found " + identifierPath.toString());
                    } else {
                        Deque<Node> identifierPathDequeue = new LinkedList<>(identifierPath);
                        identifierPathDequeue.removeFirst();
                        resolveParameterClassName(parameter, identifierPathDequeue, baseEntityId, context, waiter);
                    }
                }
            }
        }
    }

    private void resolveParameterClassName(final QueryParameter parameter, final Deque<Node> identifierPath, final EntityTargetFileId entityTargetFileId, final JAGeneratorContext context, final Object waiter) throws Exception {
        Node currentPathPart = identifierPath.removeFirst();
        context.waitForEvent(new EntityAttributePreparedWaiter((String) currentPathPart.getNodeValue(), entityTargetFileId, waiter) {
            @Override
            protected void onEntityAttributePrepared(EntityAttribute entityAttribute) throws Exception {
                if (identifierPath.isEmpty()) {
                    // last part, can resolve type
                    parameter.className = entityAttribute.getType();
                } else {
                    // Continue recursive iteration
                    resolveParameterClassName(parameter, identifierPath, new EntityTargetFileId(entityAttribute.getType(), entityTargetFileId.getConfiguration()), context, waiter);
                }
            }
        });
    }

    private void visit(Node node, StringBuilder queryDslCode, OperatorContext parentOperatorContext, List<OperatorContext> parametersToResolve) {
        if (node != null) {
            // And handle this node
            Object nodeValue = node.getNodeValue();
            switch (node.getNodeType()) {
                case IDENTIFIER:
                    // append this path to resolve current parameter type later
                    parentOperatorContext.identifierPath.add(node);
                    queryDslCode.append(nodeValue);
                    if (!node.getChildNodes().isEmpty()) {
                        queryDslCode.append(".");
                    }
                    visit(node.getFirstChild(), queryDslCode, parentOperatorContext, parametersToResolve);
                    break;
                case PARAMETER:
                    // mark this to resolve the type later (at the end of the interpretation of the parent operator)
                    parentOperatorContext.parameter = node;
                    queryDslCode.append("parameters.").append(nodeValue);
                    break;
                case LITERAL:
                    parentOperatorContext.literal = node;
                    // append the value to the code, see org.datanucleus.query.compiler.JPQLParser.processLiteral()
                    if (nodeValue != null) {
                        Class<?> valueClass = nodeValue.getClass();
                        if (Character.class.isAssignableFrom(valueClass)) {
                            queryDslCode.append("'").append(nodeValue).append("'");
                        } else if (String.class.isAssignableFrom(valueClass)) {
                            queryDslCode.append("\"").append(nodeValue).append("\"");
                        } else if (BigDecimal.class.isAssignableFrom(valueClass)) {
                            queryDslCode.append("new ${util.useClass(\"").append(BigDecimal.class.getName()).append("\")}(\"").append(nodeValue.toString()).append("\")");
                        } else if (Boolean.class.isAssignableFrom(valueClass)) {
                            if (BooleanUtils.isTrue((Boolean) nodeValue)) {
                                queryDslCode.append("true");
                            } else {
                                queryDslCode.append("false");
                            }
                        }
                    } else {
                        queryDslCode.append("null");
                    }
                    break;
                case OPERATOR:
                    // Create a new node context in order to resolve children parameters later
                    OperatorContext operatorContext = new OperatorContext();
                    // We consider a binary operator, let's interpret left part
                    visit(node.getFirstChild(), queryDslCode, operatorContext, parametersToResolve);
                    final Node rightChild = node.getNextChild();
                    boolean visitRightPart = true;
                    switch ((String) nodeValue) {
                        case "&&":
                            queryDslCode.append(".and("); // TODO break line & add correct number of spaces for readability ?
                            break;
                        case "||":
                            queryDslCode.append(".or("); // TODO break line & add correct number of spaces for readability ?
                            break;
                        case "==":
                            // If the right part is a NULL literal, then we have a IS NULL where clause, which can be different from eq(null) (which QueryDSL forbids anyways)
                            if (rightChild.getNodeType() == NodeType.LITERAL && rightChild.getNodeValue() == null) {
                                queryDslCode.append(".isNull(");
                                visitRightPart = false;
                            } else {
                                queryDslCode.append(".eq(");
                            }
                            // TODO handle other operators
                    }
                    if (visitRightPart) {
                        // visit right part
                        visit(rightChild, queryDslCode, operatorContext, parametersToResolve);
                    }
                    // and close the operator
                    queryDslCode.append(")");
                    if (operatorContext.parameter != null) {
                        // mark the parameter to be resolved later
                        parametersToResolve.add(operatorContext);
                    }
            }
        }
    }
        // TODO Add this action parameters to the BaseWS => create a context.waitForTargetFileToBePrepared(targetFile, waiter) (the waiter will have a source, which will be this here
        // TODO réfléchir sur les arbres de dépendances de waiters (parts & targetFile), dans le but de reconstituer l'arbre des modifs à appliquer si un fichier source change
        // TODO Add this action query to the BaseWS->BaseService.search

        // TODO declare this handler


    public String getQueryDslCode() {
        return queryDslCode;
    }

    public List<QueryParameter> getParameters() {
        return parameters;
    }
}

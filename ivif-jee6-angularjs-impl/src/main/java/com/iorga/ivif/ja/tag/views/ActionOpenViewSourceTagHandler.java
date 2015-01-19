package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.WSTargetFileId;
import com.iorga.ivif.ja.tag.entities.EntityAttribute;
import com.iorga.ivif.ja.tag.entities.EntityTargetFile;
import com.iorga.ivif.ja.tag.entities.EntityTargetFile.EntityTargetFileId;
import com.iorga.ivif.tag.JAXBSourceTagHandler;
import com.iorga.ivif.tag.PartWaiter;
import com.iorga.ivif.tag.TargetFileWaiter;
import com.iorga.ivif.tag.bean.ActionOpenView;
import org.apache.commons.lang3.BooleanUtils;
import org.datanucleus.query.compiler.JPQLParser;
import org.datanucleus.query.compiler.Node;

import javax.xml.bind.JAXBException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class ActionOpenViewSourceTagHandler extends JAXBSourceTagHandler<ActionOpenView, JAGeneratorContext> {
    private List<ActionParameter> parameters = new ArrayList<>();
    private String queryDslCode;

    public static class ActionParameter {
        protected String name;
        protected String className;

        public String getName() {
            return name;
        }

        public String getClassName() {
            return className;
        }
    }

    public ActionOpenViewSourceTagHandler() throws JAXBException {
        super(ActionOpenView.class);
    }

    @Override
    public void prepareTargetFiles(final JAGeneratorContext context) throws Exception {
        super.prepareTargetFiles(context);

        ActionOpenViewServiceJsTargetFile actionOpenViewServiceJsTargetFile = context.getOrCreateTargetFile(ActionOpenViewServiceJsTargetFile.class, element.getName());
        actionOpenViewServiceJsTargetFile.setActionOpenViewSourceTagHandler(this);

        context.waitForTargetFileToBePrepared(new TargetFileWaiter<GridBaseWSTargetFile, WSTargetFileId, JAGeneratorContext>(GridBaseWSTargetFile.class, new WSTargetFileId(element.getGridName()+"BaseWS", context), this) {
            List<OperatorContext> parametersToResolve = new ArrayList<>();

            @Override
            public void onPrepared(GridBaseWSTargetFile gridBaseWSTargetFile) throws Exception {
                // We will parse the query, and generate the corresponding QueryDSL code, and if an identifier is detected, register it and compute its type
                // First we must retrieve the Grid=>Entity because it is the base types reference
                EntityTargetFile baseEntity = gridBaseWSTargetFile.getGrid().getEntityTargetFile();
                // Then we parse the query and will visit it to find parameters and resolve its type
                String where = element.getQuery().getWhere();
                Node rootNode = new JPQLParser(null, null).parse(where);
                StringBuilder queryDslCode = new StringBuilder();
                visit(rootNode, queryDslCode, null);

                ActionOpenViewSourceTagHandler.this.queryDslCode = queryDslCode.toString();

                // Now we will resolve the parameters and add them
                for (OperatorContext operatorContext : parametersToResolve) {
                    // TODO handle parameters declared multiple times
                    final ActionParameter parameter = new ActionParameter();
                    parameter.name = (String) operatorContext.parameter.getNodeValue();
                    parameters.add(parameter);
                    // solve its type
                    if (operatorContext.literal != null) {
                        // This is a literal parameter
                    } else {
                        List<Node> identifierPath = operatorContext.identifierPath;
                        if (!"record".equals(identifierPath.get(0).getNodeValue())) {
                            // TODO handle another root, if there are declared joins
                            throw new IllegalStateException("An identifier must always begin with 'record'. Found " + identifierPath.toString());
                        } else {
                            Deque<Node> identifierPathDequeue = new LinkedList<>(identifierPath);
                            identifierPathDequeue.removeFirst();
                            resolveParameterClassName(parameter, identifierPathDequeue, baseEntity);
                        }
                    }
                }

                // Finaly set add the action to the grid WS
                gridBaseWSTargetFile.addActionOpenView(ActionOpenViewSourceTagHandler.this);
                // And tell its controller
                gridBaseWSTargetFile.getGridSourceTagHandler().getGridCtrlJsTargetFile().setActionOpenViewDefined(true);
            }

            private void resolveParameterClassName(final ActionParameter parameter, final Deque<Node> identifierPath, final EntityTargetFile entityTargetFile) throws Exception {
                Node currentPathPart = identifierPath.removeFirst();
                entityTargetFile.waitForPartToBePrepared(new PartWaiter<EntityAttribute, String, JAGeneratorContext>(EntityAttribute.class, (String)currentPathPart.getNodeValue(), this) {
                    @Override
                    public void onPrepared(EntityAttribute entityAttribute) throws Exception {
                        if (identifierPath.isEmpty()) {
                            // last part, can resolve type
                            parameter.className = entityAttribute.getType();
                        } else {
                            // Continue recursive iteration
                            EntityTargetFile attributeEntityTargetFile = context.getOrCreateTargetFile(EntityTargetFile.class, new EntityTargetFileId(entityAttribute.getType(), context));
                            resolveParameterClassName(parameter, identifierPath, attributeEntityTargetFile);
                        }
                    }
                });
            }

            class OperatorContext {
                private Node parameter;
                private List<Node> identifierPath = new ArrayList<>();
                private Node literal;
            }
            private void visit(Node node, StringBuilder queryDslCode, OperatorContext parentOperatorContext) {
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
                            visit(node.getFirstChild(), queryDslCode, parentOperatorContext);
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
                            visit(node.getFirstChild(), queryDslCode, operatorContext);
                            switch ((String) nodeValue) {
                                case "&&":
                                    queryDslCode.append(".and("); // TODO break line & add correct number of spaces for readability ?
                                    break;
                                case "||":
                                    queryDslCode.append(".or("); // TODO break line & add correct number of spaces for readability ?
                                    break;
                                case "==":
                                    queryDslCode.append(".eq(");
                                    // TODO handle other operators
                            }
                            // visit right part
                            visit(node.getNextChild(), queryDslCode, operatorContext);
                            // and close the operator
                            queryDslCode.append(")");
                            if (operatorContext.parameter != null) {
                                // mark the parameter to be resolved later
                                parametersToResolve.add(operatorContext);
                            }
                    }
                }
            }
        });
        // TODO Add this action parameters to the BaseWS => create a context.waitForTargetFileToBePrepared(targetFile, waiter) (the waiter will have a source, which will be this here
        // TODO réfléchir sur les arbres de dépendances de waiters (parts & targetFile), dans le but de reconstituer l'arbre des modifs à appliquer si un fichier source change
        // TODO Add this action query to the BaseWS->BaseService.search

        // TODO declare this handler
    }

    public String getQueryDslCode() {
        return queryDslCode;
    }

    public List<ActionParameter> getParameters() {
        return parameters;
    }
}

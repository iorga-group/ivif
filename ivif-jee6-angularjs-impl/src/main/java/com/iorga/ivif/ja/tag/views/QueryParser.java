package com.iorga.ivif.ja.tag.views;


import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.iorga.ivif.ja.SortingType;
import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.configurations.JAConfiguration;
import com.iorga.ivif.ja.tag.entities.EntityAttribute;
import com.iorga.ivif.ja.tag.entities.EntityAttributePreparedWaiter;
import com.iorga.ivif.ja.tag.entities.EntityTargetFile;
import com.iorga.ivif.ja.tag.entities.EntityTargetFile.EntityTargetFileId;
import com.iorga.ivif.tag.bean.Parameter;
import com.iorga.ivif.tag.bean.Query;
import com.mysema.query.support.Expressions;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.jpa.parsing.*;
import org.eclipse.persistence.internal.jpa.parsing.jpql.antlr.JPQLParser;
import org.eclipse.persistence.internal.jpa.parsing.jpql.antlr.JPQLParserBuilder;

import java.text.DateFormat;
import java.util.*;
import java.util.Map.Entry;

public class QueryParser {

    public static final String RECORD_NAME = "$record";

    public static class QueryParameter {
        protected String name;
        protected String className;
        protected String value;

        public QueryParameter(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getClassName() {
            return className;
        }

        public String getValue() {
            return value;
        }
    }

    public static class OrderBy {
        protected String ref;
        protected String refVariableName;
        protected SortingType direction;

        public OrderBy(OrderByItemNode orderByItemNode) {
            final String originalRef = ((DotNode) orderByItemNode.getOrderByItem()).getAsString();
            if (!originalRef.startsWith(RECORD_NAME)) {
                throw new IllegalArgumentException("Default order by must start with '"+RECORD_NAME+"'. Got '"+originalRef+"'");
            }
            ref = StringUtils.substringAfter(originalRef, ".");
            refVariableName = ref.replaceAll("\\.", "_");
            direction = orderByItemNode.getDirection().getSortDirection() == ExpressionOperator.Ascending ? SortingType.ASCENDING : SortingType.DESCENDING;
        }

        public SortingType getDirection() {
            return direction;
        }

        public String getRef() {
            return ref;
        }

        public String getRefVariableName() {
            return refVariableName;
        }
    }

    public static class From {
        protected String name;
        protected String path;
        protected EntityTargetFileId entityTargetFileId;
        protected EntityTargetFile entityTargetFile;
        protected String qEntityClassName;


        protected void setEntityTargetFile(EntityTargetFile entityTargetFile) {
            this.entityTargetFile = entityTargetFile;
            setEntityTargetFileId(entityTargetFile.getId());
        }

        protected void setEntityTargetFileId(EntityTargetFileId entityTargetFileId) {
            this.entityTargetFileId = entityTargetFileId;
            this.qEntityClassName = entityTargetFileId.getPackageName() + ".Q" + entityTargetFileId.getSimpleClassName();
        }

        public String getPath() {
            return path;
        }
        public String getName() {
            return name;
        }
        public String getqEntityClassName() {
            return qEntityClassName;
        }
        public EntityTargetFileId getEntityTargetFileId() {
            return entityTargetFileId;
        }
    }

    public static class Join extends From {
        protected boolean left;
        protected EntityAttribute entityAttribute;

        public Join(JoinDeclNode node) {
            this.name = node.getVariableName();
            this.path = node.getPath().getAsString();
            this.left = node.isOuterJoin();
        }

        protected void setEntityAttribute(EntityAttribute entityAttribute, JAConfiguration configuration) {
            this.entityAttribute = entityAttribute;
            setEntityTargetFileId(new EntityTargetFileId(entityAttribute.getType(), configuration));
        }

        public boolean isLeft() {
            return left;
        }
    }

    public static class ParsedQuery {
        private final Map<String, From> froms;
        protected String queryDslCode;
        private final List<OrderBy> defaultOrderBy;
        protected List<QueryParameter> queryParameters;

        public ParsedQuery(Map<String, From> froms, String queryDslCode, List<QueryParameter> queryParameters, List<OrderBy> defaultOrderBy) {
            this.froms = froms;
            this.queryDslCode = queryDslCode;
            this.queryParameters = queryParameters;
            this.defaultOrderBy = defaultOrderBy;
        }

        public Map<String, From> getFroms() {
            return froms;
        }

        public String getQueryDslCode() {
            return queryDslCode;
        }

        public List<QueryParameter> getQueryParameters() {
            return queryParameters;
        }

        public List<OrderBy> getDefaultOrderBy() {
            return defaultOrderBy;
        }
    }

    private static class OperatorContext {
        private String parameterName;
        private String identifierPath;
        private LiteralNode literalNode;
        private Class<?> literalClass;
    }


    public static class NodeVisitor {
        private final StringBuilder queryDslCode = new StringBuilder();
        private final LinkedList<OperatorContext> operatorContexts = new LinkedList<>();
        private final Multimap<String, String> identifierPathToResolveByParameterName = LinkedListMultimap.create();
        private final Map<String, String> parametersValueByName;
        private final Map<String, QueryParameter> queryParametersByParameterName = new LinkedHashMap<>();

        public NodeVisitor(Map<String, String> parametersValueByName) {
            this.parametersValueByName = parametersValueByName;
        }

        public void visit(Node node) {
            visitInspectInstance(node);
        }

        public void visitGeneric(Node node) {
            if (node.hasLeft()) {
                visitInspectInstance(node.getLeft());
            }
            if (node.hasRight()) {
                visitInspectInstance(node.getRight());
            }
        }

        public void visitInspectInstance(Node node) {

            if (node instanceof BinaryOperatorNode) {
                visit((BinaryOperatorNode) node);
            } else if (node instanceof LiteralNode) {
                visit((LiteralNode) node);
            } else if (node instanceof DotNode) {
                visit((DotNode) node);
            } else if (node instanceof AndNode) {
                visit((AndNode) node);
            } else if (node instanceof OrNode) {
                visit((OrNode) node);
            } else if (node instanceof NotNode) {
                visit((NotNode) node);
            } else if (node instanceof ParameterNode) {
                visit(((ParameterNode) node));
            } else if (node instanceof InNode) {
                visit((InNode) node);
            } else if (node instanceof BetweenNode) {
                visit((BetweenNode) node);
            } else if (node instanceof NullComparisonNode) {
                visit((NullComparisonNode) node);
            } else {
                visitGeneric(node);
            }

        }

        protected void endOperatorContext() {
            // Check if the current operator context has got a parameter
            final OperatorContext operatorContext = operatorContexts.removeLast();
            final String parameterName = operatorContext.parameterName;
            if (parameterName != null) {
                // this is a parameter context, let's check if the parameter is already registered
                QueryParameter queryParameter = queryParametersByParameterName.get(parameterName);
                if (queryParameter == null) {
                    // Let's create that new query parameter
                    queryParameter = new QueryParameter(parameterName, parametersValueByName.get(parameterName));
                    queryParametersByParameterName.put(parameterName, queryParameter);
                }
                // check that the type is compatible if already defined by a previous literal
                final Class<?> literalClass = operatorContext.literalClass;
                if (queryParameter.className != null && literalClass != null) {
                    if (!literalClass.equals(queryParameter.className)) {
                        throw new IllegalStateException("Incompatible types encountered for parameter '" + parameterName + "': "+queryParameter.className+" vs "+literalClass);
                    }
                } else if (literalClass != null) {
                    // this is a literal, we have directly the className
                    queryParameter.className = literalClass.getName();
                } else {
                    // this is an identifier path to resolve lately
                    identifierPathToResolveByParameterName.put(parameterName, operatorContext.identifierPath);
                }
            }
        }

        protected void startOperatorContext() {
            operatorContexts.addLast(new OperatorContext());
        }

        public void visit(AndNode node) {
            visitGenericQueryDslMethod(node, "and");
        }

        public void visit(OrNode node) {
            visitGenericQueryDslMethod(node, "or");
        }

        public void visit(BinaryOperatorNode node) {
            startOperatorContext();
            if (node instanceof LessThanNode) {
                visitGenericQueryDslMethod(node, "lt");
            } else if (node instanceof LessThanEqualToNode) {
                visitGenericQueryDslMethod(node, "loe");
            } else if (node instanceof GreaterThanNode) {
                visitGenericQueryDslMethod(node, "gt");
            } else if (node instanceof GreaterThanEqualToNode) {
                visitGenericQueryDslMethod(node, "goe");
            } else if (node instanceof EqualsNode) {
                visitGenericQueryDslMethod(node, "eq");
            } else if (node instanceof NotEqualsNode) {
                visitGenericQueryDslMethod(node, "ne");
            } else if (node instanceof PlusNode) {
                visitGenericQueryDslMethod(node, "add");
            } else if (node instanceof MinusNode) {
                visitGenericQueryDslMethod(node, "substract");
            } else if (node instanceof MultiplyNode) {
                visitGenericQueryDslMethod(node, "multiply");
            } else if (node instanceof DivideNode) {
                visitGenericQueryDslMethod(node, "divide");
            }
            endOperatorContext();
        }

        protected void visitGenericQueryDslMethod(Node node, String queryDslMethod) {
            final Node leftNode = node.getLeft();
            final boolean leftNodeIsLiteral = leftNode instanceof LiteralNode;
            if (leftNodeIsLiteral) {
                // Must transform this left node literal to an expression in order to use QueryDSL expression syntax for operation and right node
                queryDslCode.append(useClass(Expressions.class)).append(".");
                if (leftNode instanceof TemporalLiteralNode) {
                    queryDslCode.append("comparableTemplate(").append(useClass(Date.class)).append(".class, \"{0}\", ");
                } else if (leftNode instanceof StringLiteralNode) {
                    queryDslCode.append("stringTemplate(\"{0}\", ");
                } else if (leftNode instanceof FloatLiteralNode) {
                    queryDslCode.append("numberTemplate(").append(useClass(Float.class)).append(".class, \"{0}\", ");
                } else if (leftNode instanceof LongLiteralNode) {
                    queryDslCode.append("numberTemplate(").append(useClass(Long.class)).append(".class, \"{0}\", ");
                } else if (leftNode instanceof DoubleLiteralNode) {
                    queryDslCode.append("numberTemplate(").append(useClass(Double.class)).append(".class, \"{0}\", ");
                } else if (leftNode instanceof IntegerLiteralNode) {
                    queryDslCode.append("numberTemplate(").append(useClass(Integer.class)).append(".class, \"{0}\", ");
                } else if (leftNode instanceof BooleanLiteralNode) {
                    queryDslCode.append("booleanTemplate(\"{0}\", ");
                }
            }
            visitInspectInstance(leftNode);
            if (leftNodeIsLiteral) {
                queryDslCode.append(")");
            }
            queryDslCode.append("." + queryDslMethod + "(");
            visitInspectInstance(node.getRight());
            queryDslCode.append(")");
        }

        public void visit(DotNode node) {
            final String identifierPath = node.getAsString();
            queryDslCode.append(identifierPath);
            final OperatorContext currentOperatorContext = getCurrentOperatorContext();
            if (currentOperatorContext != null) {
                currentOperatorContext.identifierPath = identifierPath;
            }
        }

        public void visit(NotNode node) {
            if (node.getLeft() instanceof NullComparisonNode) {
                // this is a "IS NOT NULL" tree
                visitInspectInstance(node.getLeft().getLeft());
                queryDslCode.append(".isNotNull()");
            } else {
                visitInspectInstance(node.getLeft());
                queryDslCode.append(".not()");
            }
        }

        public void visit(InNode node) {
            startOperatorContext();
            visit(node.getLeft());
            queryDslCode.append(".in(");
            boolean first = true;
            for (Object object : node.getTheObjects()) {
                if (first) {
                    first = false;
                } else {
                    queryDslCode.append(", ");
                }
                visit((Node) object);
            }
            queryDslCode.append(")");
            endOperatorContext();
        }

        public void visit(BetweenNode node) {
            startOperatorContext();
            queryDslCode.append(".between(");
            visit(node.getLeft());
            queryDslCode.append(", ");
            visit(node.getRight());
            queryDslCode.append(")");
            endOperatorContext();
        }

        public void visit(NullComparisonNode node) {
            visit(node.getLeft());
            queryDslCode.append(".isNull()");
        }

        private OperatorContext getCurrentOperatorContext() {
            return !operatorContexts.isEmpty() ? operatorContexts.getLast() : null;
        }
        
        public void visit(LiteralNode node) {
            final Object literal = node.getLiteral();
            final OperatorContext currentOperatorContext = getCurrentOperatorContext();
            currentOperatorContext.literalNode = node;
            if (node instanceof TemporalLiteralNode) {
                queryDslCode.append("new ").append(useClass(DateFormat.class)).append(".parse(\"").append(literal).append("\")");
                currentOperatorContext.literalClass = Date.class;
            } else if (node instanceof StringLiteralNode) {
                queryDslCode.append("\"").append(literal).append("\"");
                currentOperatorContext.literalClass = String.class;
            } else if (node instanceof FloatLiteralNode) {
                queryDslCode.append(literal.toString()).append("f");
                currentOperatorContext.literalClass = float.class;
            } else if (node instanceof LongLiteralNode) {
                queryDslCode.append(literal.toString()).append("l");
                currentOperatorContext.literalClass = long.class;
            } else if (node instanceof DoubleLiteralNode) {
                queryDslCode.append(literal.toString()).append("d");
                currentOperatorContext.literalClass = double.class;
            } else if (node instanceof IntegerLiteralNode) {
                queryDslCode.append(literal.toString());
                currentOperatorContext.literalClass = int.class;
            } else if (node instanceof BooleanLiteralNode) {
                if (BooleanUtils.isTrue((Boolean) literal)) {
                    queryDslCode.append("true");
                } else {
                    queryDslCode.append("false");
                }
                currentOperatorContext.literalClass = boolean.class;
            }
        }

        public void visit(ParameterNode node) {
            final String parameterName = node.getParameterName();
            getCurrentOperatorContext().parameterName = parameterName;

            final String parameterValue = parametersValueByName.get(parameterName);
            if (parameterValue != null) {
                // The value of this parameter is specified, let's append its value
                queryDslCode.append(parameterValue);
            } else {
                // The value of this param is not specified, will be a parameter to pass
                queryDslCode.append("parameters.").append(parameterName);
            }
        }
    }

    public static String useClass(Class<?> klass) {
        return "${util.useClass(\"" + klass.getName() + "\")}";
    }

    public static Map<String, From> createDefaultFroms(EntityTargetFileId baseEntityId) {
        Map<String, From> froms = new LinkedHashMap<>();
        // Add $record from
        final From recordFrom = new From();
        recordFrom.name = RECORD_NAME;
        recordFrom.path = RECORD_NAME;
        recordFrom.setEntityTargetFileId(baseEntityId);
        froms.put(RECORD_NAME, recordFrom);

        return froms;
    }

    public static ParsedQuery parse(Query element, EntityTargetFileId baseEntityId, Object waiter, JAGeneratorContext context) throws Exception {

        Map<String, String> parametersValueByName = new HashMap<>();
        if (element != null) {

            // Parse the from
            final String from = element.getFrom();
            Map<String, From> froms = createDefaultFroms(baseEntityId);

            if (StringUtils.isNotBlank(from)) {
                final JPQLParser parser = createJPQLParser("FROM $record " + from);
                final FromNode fromNode = (FromNode) parser.fromClause();
                final LinkedList<IdentificationVariableDeclNode> declarations = new LinkedList<>(fromNode.getDeclarations());
                final IdentificationVariableDeclNode first = declarations.removeFirst();
                // first must be $record
                if (!(first instanceof RangeDeclNode) || !RECORD_NAME.equals(first.getVariableName())) {
                    throw new IllegalStateException("From reference must start with '$record', encountered: '" + first.getAsString() + "' (in from: '" + from + "'");
                }
                for (IdentificationVariableDeclNode node : declarations) {
                    if (!(node instanceof JoinDeclNode)) {
                        throw new IllegalStateException("A reference in from was not a join: '" + node.getAsString() + "' (in from: '" + from + "'");
                    } else {
                        final Join join = new Join((JoinDeclNode) node);
                        froms.put(join.name, join);
                        resolveEntityTargetFileIdAndQEntityClassName(join, froms, waiter, context, null);
                    }
                }
            }

            // Parsing parameter values
            for (Parameter parameter : element.getParameter()) {
                final String parameterName = parameter.getName();
                parametersValueByName.put(parameterName, JavaParser.parseExpression(parameter.getValue()));
            }

            // Now visit the where query
            final NodeVisitor nodeVisitor = new NodeVisitor(parametersValueByName);
            final String where = element.getWhere();
            if (StringUtils.isNotBlank(where)) {
                final JPQLParser parser = createJPQLParser(where);
                final Node tree = (Node) parser.conditionalExpression();
                nodeVisitor.visit(tree);

                // And finally, resolve the parameters specified without value and with identifier paths
                for (Entry<String, String> identifierPathForParameterName : nodeVisitor.identifierPathToResolveByParameterName.entries()) {
                    final String parameterName = identifierPathForParameterName.getKey();
                    final String identifierPath = identifierPathForParameterName.getValue();
                    final QueryParameter queryParameter = nodeVisitor.queryParametersByParameterName.get(parameterName);
                    Deque<String> identifierPathDeque = new LinkedList<>(Arrays.asList(identifierPath.split("\\.")));
                    resolveParameterClassName(queryParameter, identifierPathDeque, froms, waiter, context);
                }
            }

            // Parse the default order by
            final String defaultOrderByStr = element.getDefaultOrderBy();
            final ArrayList<OrderBy> defaultOrderBy;
            if (StringUtils.isNotBlank(defaultOrderByStr)) {
                final JPQLParser parser = createJPQLParser("ORDER BY " + defaultOrderByStr);
                final OrderByNode orderByNode = (OrderByNode) parser.orderByClause();
                defaultOrderBy = new ArrayList<>(orderByNode.getOrderByItems().size());
                for (Object orderByItemNode : orderByNode.getOrderByItems()) {
                    defaultOrderBy.add(new OrderBy((OrderByItemNode) orderByItemNode));
                }
            } else {
                defaultOrderBy = null;
            }

            return new ParsedQuery(froms, nodeVisitor.queryDslCode.toString(), new ArrayList<>(nodeVisitor.queryParametersByParameterName.values()), defaultOrderBy);
        } else {
            return null;
        }
    }

    private static JPQLParser createJPQLParser(String queryText) {
        final JPQLParser parser = JPQLParserBuilder.buildParser(queryText);
        final String queryInfo = parser.getQueryInfo();
        final NodeFactoryImpl factory = new NodeFactoryImpl(queryInfo);
        parser.setNodeFactory(factory);
        return parser;
    }

    private static interface EntityTargetFileIdWaiter {
        void onEntityTargetFileIdResolved(EntityTargetFileId entityTargetFileId) throws Exception;
    }
    private static interface EntityTargetFileWaiter {
        void onEntityTargetFileResolved(EntityTargetFile entityTargetFile);
    }

    private static void resolveEntityTargetFileIdAndQEntityClassName(final Join join, Map<String, From> froms, final Object waiter, final JAGeneratorContext context, final EntityTargetFileIdWaiter entityTargetFileIdWaiter) throws Exception {
        if (join.qEntityClassName == null) {
            final Deque<String> identifierPath = new LinkedList<>(Lists.newArrayList(join.getPath().split("\\.")));
            final String fromName = identifierPath.removeFirst();
            final From from = froms.get(fromName);
            resolveFromEntityTargetFileId(from, froms, waiter, context, new EntityTargetFileIdWaiter() {
                @Override
                public void onEntityTargetFileIdResolved(EntityTargetFileId entityTargetFileId) throws Exception {
                    resolveEntityTargetFileIdAndQEntityClassName(join, entityTargetFileId, identifierPath, waiter, context);
                    if (entityTargetFileIdWaiter != null) {
                        entityTargetFileIdWaiter.onEntityTargetFileIdResolved(entityTargetFileId);
                    }
                }
            });
        }
    }

    private static void resolveEntityTargetFileIdAndQEntityClassName(final Join join, final EntityTargetFileId entityTargetFileId, final Deque<String> identifierPath, final Object waiter, final JAGeneratorContext context) throws Exception {
        String currentPathPart = identifierPath.removeFirst();
        context.waitForEvent(new EntityAttributePreparedWaiter(currentPathPart, entityTargetFileId, waiter) {
            @Override
            protected void onEntityAttributePrepared(EntityAttribute entityAttribute) throws Exception {
                final JAConfiguration configuration = entityTargetFileId.getConfiguration();
                if (identifierPath.isEmpty()) {
                    // last part, can resolve type
                    join.setEntityAttribute(entityAttribute, configuration);
                } else {
                    // Continue recursive iteration
                    resolveEntityTargetFileIdAndQEntityClassName(join, new EntityTargetFileId(entityAttribute.getType(), configuration), identifierPath, waiter, context);
                }
            }
        });
    }

    private static void resolveFromEntityTargetFileId(From from, Map<String, From> froms, Object waiter, JAGeneratorContext context, EntityTargetFileIdWaiter entityTargetFileIdWaiter) throws Exception {
        if (from.entityTargetFileId != null) {
            entityTargetFileIdWaiter.onEntityTargetFileIdResolved(from.entityTargetFileId);
        } else {
            // TODO resolve other "root" froms => must add the type in the from, else here
            if (!(from instanceof Join)) {
                throw new IllegalStateException("Root froms other than $record are not yet supported");
            } else {
                resolveEntityTargetFileIdAndQEntityClassName((Join) from, froms, waiter, context, entityTargetFileIdWaiter);
            }
        }
    }

    private static void resolveParameterClassName(final QueryParameter parameter, final Deque<String> identifierPath, Map<String, From> froms, final Object waiter, final JAGeneratorContext context) throws Exception {
        final String firstIdentifier = identifierPath.removeFirst();
        final From from = froms.get(firstIdentifier);
        if (from.entityTargetFileId != null) {
            resolveParameterClassName(parameter, identifierPath, from.entityTargetFileId, waiter, context);
        } else {
            resolveFromEntityTargetFileId(from, froms, waiter, context, new EntityTargetFileIdWaiter() {
                @Override
                public void onEntityTargetFileIdResolved(EntityTargetFileId entityTargetFileId) throws Exception {
                    resolveParameterClassName(parameter, identifierPath, entityTargetFileId, waiter, context);
                }
            });
        }
    }

    private static void resolveParameterClassName(final QueryParameter parameter, final Deque<String> identifierPath, final EntityTargetFileId entityTargetFileId, final Object waiter, final JAGeneratorContext context) throws Exception {
        String currentPathPart = identifierPath.removeFirst();
        context.waitForEvent(new EntityAttributePreparedWaiter(currentPathPart, entityTargetFileId, waiter) {
            @Override
            protected void onEntityAttributePrepared(EntityAttribute entityAttribute) throws Exception {
                final String entityAttributeType = entityAttribute.getType();
                if (identifierPath.isEmpty()) {
                    // last part, can resolve type
                    if (parameter.className != null) {
                        // Check compatibility if already exists
                        if (!entityAttributeType.equals(parameter.className)) {
                            throw new IllegalStateException("Incompatible types encountered for parameter '" + parameter.getName() + "': " + parameter.className + " vs " + entityAttributeType);
                        }
                    } else {
                        parameter.className = entityAttributeType;
                    }
                } else {
                    // Continue recursive iteration
                    resolveParameterClassName(parameter, identifierPath, new EntityTargetFileId(entityAttributeType, entityTargetFileId.getConfiguration()), waiter, context);
                }
            }
        });
    }
}

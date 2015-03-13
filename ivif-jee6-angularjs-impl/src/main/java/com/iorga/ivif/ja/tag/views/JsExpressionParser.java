package com.iorga.ivif.ja.tag.views;

import com.antlr.v4.grammars.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.antlr.v4.grammars.ECMAScriptParser.*;

public class JsExpressionParser {

    public static final String LINE_NAME = "$line";
    public static final String RECORD_NAME = "$record";

    public static class LineRef {
        private final String ref;
        private final String refVariableName;

        public LineRef(String fullLineRef, String refPrefix) {
            ref = StringUtils.substringAfter(fullLineRef, refPrefix);
            refVariableName = ref.replaceAll("\\.", "_");
        }

        public String getRef() {
            return ref;
        }

        public String getRefVariableName() {
            return refVariableName;
        }

        @Override
        public int hashCode() {
            return ref.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LineRef lineRef1 = (LineRef) o;

            if (!ref.equals(lineRef1.ref)) return false;

            return true;
        }
    }

    public static class JsExpression {
        private Set<String> injections = new LinkedHashSet<>();
        private Set<String> actions = new LinkedHashSet<>();
        private String expression;
        private Set<LineRef> lineRefs = new LinkedHashSet<>();

        public Set<String> getInjections() {
            return injections;
        }

        public String getExpression() {
            return expression;
        }

        public Set<LineRef> getLineRefs() {
            return lineRefs;
        }

        public Set<String> getActions() {
            return actions;
        }
    }

    public static JsExpression parseExpression(final String jsExpression, final String lineReplacement, final String recordReplacement) {
        final ECMAScriptParser parser = createParser(jsExpression);
        final ExpressionSequenceContext tree = parser.expressionSequence();

        return parse(lineReplacement, recordReplacement, parser, tree);
    }

    private static ECMAScriptParser createParser(final String jsExpression) {
        final ECMAScriptParser parser = new ECMAScriptParser(new CommonTokenStream(new ECMAScriptLexer(new ANTLRInputStream(jsExpression))));
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                throw new RuntimeException("Syntax error in '"+jsExpression+"': "+msg);
            }
        });
        return parser;
    }

    public static JsExpression parseActions(String jsExpression, final String lineReplacement, final String recordReplacement) {
        final ECMAScriptParser parser = createParser(jsExpression);
        final StatementListContext tree = parser.statementList();

        return parse(lineReplacement, recordReplacement, parser, tree);
    }

    protected static JsExpression parse(final String lineReplacement, final String recordReplacement, final ECMAScriptParser parser, ParseTree tree) {
        final StringBuilder expressionBuilder = new StringBuilder();
        final JsExpression expression = new JsExpression();

        new ECMAScriptBaseVisitor<Void>() {

            @Override
            public Void visitArgumentsExpression(ArgumentsExpressionContext ctx) {
                final SingleExpressionContext singleExpression = ctx.singleExpression();
                if (singleExpression != null && singleExpression.getChildCount() == 1) {
                    final String methodName = singleExpression.getChild(0).getText();
                    final boolean isDollarAction = "$action".equals(methodName);
                    if ("$inject".equals(methodName) || isDollarAction) {
                        // we are in an injection ($inject or $action)
                        // get the injection argument, the path is : singleExpression > arguments > ( argumentList ) > singleExpression > terminalNode
                        String injection = ctx.arguments().argumentList().singleExpression().get(0).getChild(0).getText();
                        if (isDollarAction) {
                            // this is an action, must append "Action" to the action name
                            injection += "Action";
                            addAction(injection, expression);
                        }
                        expression.injections.add(injection);
                        // replace the "$inject(serviceName)" with "serviceName" or "$action(actionName)" with "actionNameAction" directly
                        expressionBuilder.append(injection);
                    } else {
                        // we are in a single method name call
                        expressionBuilder.append(methodName);

                        visitArguments(ctx.arguments()); // only visit arguments now that the method name is "resolved"
                    }
                } else {
                    super.visitArgumentsExpression(ctx);
                }
                return null;
            }

            @Override
            public Void visitMemberDotExpression(MemberDotExpressionContext ctx) {
                String ref = parser.getTokenStream().getText(ctx);
                if (ref.startsWith(LINE_NAME + ".")) {
                    final LineRef lineRef = new LineRef(ref, LINE_NAME + ".");
                    expression.lineRefs.add(lineRef);
                    // must change $line.field.subfield to lineReplacement.field_subfield
                    expressionBuilder.append(lineReplacement + "." + lineRef.refVariableName);
                } else if (ref.startsWith(RECORD_NAME + ".")) {
                    final LineRef lineRef = new LineRef(ref, RECORD_NAME + ".");
                    expression.lineRefs.add(lineRef);
                    // must change $record.field.subfield to lineReplacement.field_subfield
                    expressionBuilder.append(recordReplacement + "." + lineRef.refVariableName);
                } else {
                    super.visitMemberDotExpression(ctx);
                }
                return null;
            }

            @Override
            public Void visitIdentifierExpression(IdentifierExpressionContext ctx) {
                // catch lonely $line or $record references
                if (ctx.getChildCount() == 1) {
                    final String firstChildText = ctx.getChild(0).getText();
                    if (LINE_NAME.equals(firstChildText)) {
                        // Catch and stop the visit by replacing the $line with its replacement
                        expressionBuilder.append(lineReplacement);
                    } else if (RECORD_NAME.equals(firstChildText)) {
                        // Catch and stop the visit by replacing the $record with its replacement
                        expressionBuilder.append(recordReplacement);
                    } else {
                        return super.visitIdentifierExpression(ctx);
                    }
                } else {
                    return super.visitIdentifierExpression(ctx);
                }
                return null;
            }

            @Override
            public Void visitTerminal(TerminalNode node) {
                expressionBuilder.append(node.getText());
                return null;
            }
        }.visit(tree);

        expression.expression = expressionBuilder.toString();

        return expression;
    }

    protected static void addAction(String action, JsExpression expression) {
        expression.actions.add(StringUtils.substringBeforeLast(action, "Action"));
    }
}

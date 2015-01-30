package com.iorga.ivif.ja.tag.views;

import com.antlr.v4.grammars.*;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.antlr.v4.grammars.ECMAScriptParser.*;

public class JsExpressionParser {

    public static final String LINE_NAME = "$line";

    public static class LineRef {
        private final String ref;
        private final String refVariableName;

        public LineRef(String fullLineRef) {
            ref = StringUtils.substringAfter(fullLineRef, LINE_NAME + ".");
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
    }

    public static JsExpression parse(String jsExpression, final String lineReplacement) {
        final ECMAScriptParser parser = new ECMAScriptParser(new CommonTokenStream(new ECMAScriptLexer(new ANTLRInputStream(jsExpression))));
        final ExpressionSequenceContext tree = parser.expressionSequence();

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
                        }
                        expression.injections.add(injection);
                        // replace the "$inject(serviceName)" with "serviceName" or "$action(actionName)" with "actionNameAction" directly
                        expressionBuilder.append(injection);
                    } else {
                        // we are in a single method name call, register the method name as an injection, treat it like if it was an action
                        String actionName = methodName + "Action";
                        expression.injections.add(actionName);
                        expressionBuilder.append(actionName);

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
                    final LineRef lineRef = new LineRef(ref);
                    expression.lineRefs.add(lineRef);
                    // must change $line.field.subfield to lineReplacement.field_subfield
                    expressionBuilder.append(lineReplacement + "." + lineRef.refVariableName);
                } else {
                    super.visitMemberDotExpression(ctx);
                }
                return null;
            }

            @Override
            public Void visitIdentifierExpression(IdentifierExpressionContext ctx) {
                // catch lonely $line references
                if (ctx.getChildCount() == 1 && LINE_NAME.equals(ctx.getChild(0).getText())) {
                    // Catch and stop the visit by replacing the $line with its replacement
                    expressionBuilder.append(lineReplacement);
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
}

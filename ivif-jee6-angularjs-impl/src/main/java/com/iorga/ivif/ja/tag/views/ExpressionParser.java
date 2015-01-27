package com.iorga.ivif.ja.tag.views;

import com.antlr.v4.grammars.JavaBaseListener;
import com.antlr.v4.grammars.JavaLexer;
import com.antlr.v4.grammars.JavaParser;
import com.antlr.v4.grammars.JavaParser.ExpressionContext;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.LinkedList;

public class ExpressionParser {
    private static class MethodContext {
        private final ExpressionContext baseMethodContext;
        private final StringBuilder injectionClassNameBuilder = new StringBuilder();
        private boolean inInject = false;

        public MethodContext(ExpressionContext baseMethodContext) {
            this.baseMethodContext = baseMethodContext;
        }

        public String buildClassName() {
            // Should be that form "(com.iorga.OneClass)" so we must remove the left & right parenthesis
            return injectionClassNameBuilder.substring(1, injectionClassNameBuilder.length() - 1).toString();
        }
    }


    public static String parse(String expression) {
        final JavaParser parser = new JavaParser(new CommonTokenStream(new JavaLexer(new ANTLRInputStream(expression))));
        final ExpressionContext tree = parser.expression();
        final StringBuilder compiledJavaExpression = new StringBuilder();

        new ParseTreeWalker().walk(new JavaBaseListener() {
            LinkedList<MethodContext> methodContexts = new LinkedList<MethodContext>();

            @Override
            public void enterExpression(ExpressionContext ctx) {
                final ParseTree secondChild = ctx.getChild(1);
                if (secondChild != null && "(".equals(secondChild.getText())) {
                    // This is a method call, we must test if it's an injection
                    methodContexts.addLast(new MethodContext(ctx));
                }
            }

            @Override
            public void exitExpression(ExpressionContext ctx) {
                if (!methodContexts.isEmpty() && methodContexts.getLast().baseMethodContext == ctx) {
                    final MethodContext methodContext = methodContexts.removeLast();
                    if (methodContext.inInject) {
                        // that method was a $inject, let's build it
                        compiledJavaExpression.append("${util.useInject(\"").append(methodContext.buildClassName()).append("\")}");
                    }
                }
            }

            @Override
            public void visitTerminal(TerminalNode node) {
                final String nodeText = node.getText();
                if (methodContexts.isEmpty()) {
                    compiledJavaExpression.append(nodeText);
                } else {
                    MethodContext currentMethodContext = methodContexts.getLast();
                    if (!currentMethodContext.inInject) {
                        // We are currently in a method expression, we must check if it's a call to "$inject" method
                        if ("$inject".equals(nodeText)) {
                            // Check the 3d parent because the parent should be a primary, the parent an expression which only contains this primary, and the parent the method expression
                            if (getNthParentSafely(node, 3) == currentMethodContext.baseMethodContext) {
                                currentMethodContext.inInject = true;
                            }
                        } else {
                            // we are not in an inject method, so continue to build java expression
                            compiledJavaExpression.append(nodeText);
                        }
                    } else {
                        // We are currently in an inject, so put all terminal node into the inject className builder
                        currentMethodContext.injectionClassNameBuilder.append(nodeText);
                    }
                }
            }

            public ParseTree getNthParentSafely(ParseTree parseTree, int n) {
                if (n == 0) {
                    return parseTree;
                } else {
                    assert n > 0;
                    if (parseTree != null) {
                        return getNthParentSafely(parseTree.getParent(), n - 1);
                    } else {
                        return null;
                    }
                }
            }
        }, tree);

        //tree.inspect(parser); // way to view it graphically
        return compiledJavaExpression.toString();
    }
}

package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.views.JsExpressionParser.JsExpression;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class JsExpressionParserTest {
    
    @Test
    public void dollarInjectTest() {
        final JsExpression expression = JsExpressionParser.parse("$inject(myAction)({id: $line.a.ref, id2: $line.other})", "selectedLine");
        assertThat(expression.getExpression()).isEqualTo("myAction({id:selectedLine.a_ref,id2:selectedLine.other})");
        assertThat(expression.getInjections()).containsExactly("myAction");
    }

    @Test
    public void injectionWithoutDollarInjectTest() {
        final JsExpression expression = JsExpressionParser.parse("myAction({id: $line.a.ref, id2: $line.other})", "myLine");
        assertThat(expression.getExpression()).isEqualTo("myActionAction({id:myLine.a_ref,id2:myLine.other})");
        assertThat(expression.getInjections()).containsExactly("myActionAction");
    }

    @Test
    public void lineRefsTest() {
        final JsExpression expression = JsExpressionParser.parse("myAction({id: $line.a.ref, id2: $line.other}) + $line.other", "myLine");
        assertThat(expression.getLineRefs()).hasSize(2);
    }

    @Test
    public void actionThenTest() {
        JsExpression expression = JsExpressionParser.parse("$action(myAction)().then($action(secondAction)())", "line");
        assertThat(expression.getExpression()).isEqualTo("myActionAction().then(secondActionAction())");
        expression = JsExpressionParser.parse("myAction().then(secondAction())", "line");
        assertThat(expression.getExpression()).isEqualTo("myActionAction().then(secondActionAction())");
    }

    @Test
    public void lineWithoutFieldTest() {
        final JsExpression expression = JsExpressionParser.parse("$inject(anAction)($line)", "line");
        assertThat(expression.getExpression()).isEqualTo("anAction(line)");
    }

    @Test
    public void methodWithDirectLineRefsTest() {
        final JsExpression expression = JsExpressionParser.parse("$inject(anAction)($line.test, $line.field.b)", "line");
        assertThat(expression.getExpression()).isEqualTo("anAction(line.test,line.field_b)");
        assertThat(expression.getInjections()).containsExactly("anAction");
    }
}

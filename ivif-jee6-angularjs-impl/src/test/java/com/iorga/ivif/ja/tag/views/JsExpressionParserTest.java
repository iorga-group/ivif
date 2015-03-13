package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.views.JsExpressionParser.JsExpression;
import com.iorga.ivif.ja.tag.views.JsExpressionParser.LineRef;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class JsExpressionParserTest {
    
    @Test
    public void dollarInjectTest() {
        final JsExpression expression = JsExpressionParser.parseExpression("$inject(myAction)({id: $line.a.ref, id2: $line.other})", "selectedLine", "rec");
        assertThat(expression.getExpression()).isEqualTo("myAction({id:selectedLine.a_ref,id2:selectedLine.other})");
        assertThat(expression.getInjections()).containsExactly("myAction");
    }

    @Test
    public void injectionWithoutDollarInjectTest() {
        final JsExpression expression = JsExpressionParser.parseExpression("myAction({id: $line.a.ref, id2: $line.other})", "myLine", "rec");
        assertThat(expression.getExpression()).isEqualTo("myAction({id:myLine.a_ref,id2:myLine.other})");
        assertThat(expression.getInjections()).isEmpty();
    }

    @Test
    public void lineRefsTest() {
        final JsExpression expression = JsExpressionParser.parseExpression("myAction({id: $line.a.ref, id2: $line.other}) + $line.other", "myLine", "rec");
        assertThat(expression.getLineRefs()).hasSize(2);
    }

    @Test
    public void actionThenTest() {
        JsExpression expression = JsExpressionParser.parseExpression("$action(myAction)().then($action(secondAction)())", "line", "rec");
        assertThat(expression.getExpression()).isEqualTo("myActionAction().then(secondActionAction())");
        expression = JsExpressionParser.parseExpression("myAction().then(secondAction())", "line", "rec");
        assertThat(expression.getExpression()).isEqualTo("myAction().then(secondAction())");
    }

    @Test
    public void lineWithoutFieldTest() {
        final JsExpression expression = JsExpressionParser.parseExpression("$inject(anAction)($line)", "line", "rec");
        assertThat(expression.getExpression()).isEqualTo("anAction(line)");
    }

    @Test
    public void methodWithDirectLineRefsTest() {
        final JsExpression expression = JsExpressionParser.parseExpression("$inject(anAction)($line.test, $line.field.b)", "line", "rec");
        assertThat(expression.getExpression()).isEqualTo("anAction(line.test,line.field_b)");
        assertThat(expression.getInjections()).containsExactly("anAction");
    }

    @Test
    public void actionsTest() {
        JsExpression expression = JsExpressionParser.parseExpression("$inject(anAction)($line)", "line", "rec");
        assertThat(expression.getActions()).isEmpty();
        expression = JsExpressionParser.parseExpression("$action(anAction)($line)", "line", "rec");
        assertThat(expression.getActions()).containsExactly("anAction");
        expression = JsExpressionParser.parseExpression("anAction($line)", "line", "rec");
        assertThat(expression.getActions()).isEmpty();
    }

    @Test(expected = RuntimeException.class)
    public void doubleExpressionTest() {
        JsExpressionParser.parseExpression("$inject(anAction)($line);$inject(anotherAction)($line.aField)", "line", "rec");
    }

    @Test
    public void doubleActionsTest() {
        final JsExpression expression = JsExpressionParser.parseActions("$inject(anAction)($line);$inject(anotherAction)($line.aField)", "line", "rec");
        assertThat(expression.getInjections()).hasSize(2);
    }

    @Test
    public void dollarRecordTest() {
        JsExpression expression = JsExpressionParser.parseExpression("$record.test.toto || $line.another.field.test", "line", "rec");
        assertThat(expression.getLineRefs()).hasSize(2);
        final LineRef recordLineRef = expression.getLineRefs().iterator().next();
        assertThat(recordLineRef.getRef()).isEqualTo("test.toto");
        assertThat(recordLineRef.getRefVariableName()).isEqualTo("test_toto");
    }
}

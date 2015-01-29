package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.views.JsExpressionParser.JsExpression;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class JsExpressionParserTest {
    
    @Test
    public void dollarInjectTest() {
        final JsExpression expression = JsExpressionParser.parse("$inject(monAction)({id: $line.une.ref, id2: $line.autre})", "selectedLine");
        assertThat(expression.getExpression()).isEqualTo("monAction({id:selectedLine.une_ref,id2:selectedLine.autre})");
        assertThat(expression.getInjections()).containsExactly("monAction");
    }

    @Test
    public void injectionWithoutDollarInjectTest() {
        final JsExpression expression = JsExpressionParser.parse("monAction({id: $line.une.ref, id2: $line.autre})", "myLine");
        assertThat(expression.getExpression()).isEqualTo("monActionAction({id:myLine.une_ref,id2:myLine.autre})");
        assertThat(expression.getInjections()).containsExactly("monActionAction");
    }

    @Test
    public void lineRefsTest() {
        final JsExpression expression = JsExpressionParser.parse("monAction({id: $line.une.ref, id2: $line.autre}) + $line.autre", "myLine");
        assertThat(expression.getLineRefs()).hasSize(2);
    }
}

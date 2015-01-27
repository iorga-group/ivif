package com.iorga.ivif.ja.tag.views;


import org.junit.Assert;
import org.junit.Test;

public class ExpressionParserTest {

    @Test
    public void injectionTest() throws Exception {
        String parsedExpression = ExpressionParser.parse("$inject(com.iorga.Test).method(\"param\") + 3");
        Assert.assertEquals("${util.useInject(\"com.iorga.Test\")}.method(\"param\")+3", parsedExpression);
        parsedExpression = ExpressionParser.parse("\"qsdf\" + $inject(com.iorga.AnotherTest).anotherMethod()");
        Assert.assertEquals("\"qsdf\"+${util.useInject(\"com.iorga.AnotherTest\")}.anotherMethod()", parsedExpression);
    }
}

package com.iorga.ivif.ja.tag.views;


import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

public class JavaParserTest {

    @Test
    public void injectionTest() throws Exception {
        String parsedExpression = JavaParser.parseExpression("$inject(com.iorga.Test).method(\"param\") + 3");
        assertThat(parsedExpression).isEqualTo("${util.useInject(\"com.iorga.Test\")}.method(\"param\")+3");

        parsedExpression = JavaParser.parseExpression("\"qsdf\" + $inject(com.iorga.AnotherTest).anotherMethod()");
        assertThat(parsedExpression).isEqualTo("\"qsdf\"+${util.useInject(\"com.iorga.AnotherTest\")}.anotherMethod()");
    }

    @Test
    public void implementsTest() {
        final String parsedImplements = JavaParser.parseImplements("com.iorga.ivif.ja.tag.test.Historizable<java.lang.Long, com.iorga.test.Long>");
        assertThat(parsedImplements).isEqualTo("${util.useClass(\"com.iorga.ivif.ja.tag.test.Historizable\", false)}<${util.useClass(\"java.lang.Long\", false)},${util.useClass(\"com.iorga.test.Long\", false)}>");
    }
}

package com.iorga.ivif.ja.tag.util;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.util.JavaClassGeneratorUtil;
import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;

public class FreemarkerJavaClassGeneratorUtil extends JavaClassGeneratorUtil {

    private final Configuration configuration;
    private final JAGeneratorContext context;

    public FreemarkerJavaClassGeneratorUtil(JAGeneratorContext context) {
        this.context = context;
        this.configuration = context.getConfiguration();
    }

    public String fromTemplateString(String templateString) throws IOException, TemplateException {
        Template template = new Template("templateString", new StringReader(templateString), configuration);

        SimpleHash freemarkerContext = context.createSimpleHash();
        freemarkerContext.put("util", this);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        template.process(freemarkerContext, new OutputStreamWriter(stream));

        return stream.toString();
    }
}

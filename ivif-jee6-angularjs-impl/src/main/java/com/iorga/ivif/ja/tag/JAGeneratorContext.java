package com.iorga.ivif.ja.tag;

import com.iorga.ivif.tag.GeneratorContext;
import freemarker.template.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JAGeneratorContext extends GeneratorContext<JAGeneratorContext> {

    protected Configuration configuration = new Configuration(Configuration.VERSION_2_3_21);
    protected ObjectWrapper wrapper = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_21).build();
    {
        configuration.setClassForTemplateLoading(JAGeneratorContext.class, "/templates");
        configuration.setDefaultEncoding("UTF-8");
    }

    public Path getJavaBaseGenerationPathRelativeToTargetPath() {
        return Paths.get("ivif-generated-sources", "java");
    }

    public Path getWebappBaseGenerationPathRelativeToTargetPath() {
        return Paths.get("ivif-generated-sources", "webapp");
    }

    public Template getTemplate(String name) throws IOException {
        return configuration.getTemplate(name);
    }

    public SimpleHash createSimpleHash() {
        return new SimpleHash(wrapper);
    }
}

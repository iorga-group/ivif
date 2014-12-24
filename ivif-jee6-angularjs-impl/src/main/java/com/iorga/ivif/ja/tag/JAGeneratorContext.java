package com.iorga.ivif.ja.tag;

import com.iorga.ivif.tag.GeneratorContext;
import com.iorga.ivif.tag.SourceFile;
import com.iorga.ivif.tag.SourceFileHandler;
import com.iorga.ivif.tag.TargetFile;
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

    /*
    public <T extends TargetFile> T getOrCreateTargetFileBasedOnBasePackage(String packageExtension, String className, Class<T> targetFileType, SourceFileHandler<JAGeneratorContext, ?> sourceFileHandler, SourceFile sourceFile) throws Exception {
        return getOrCreateTargetFile(getJavaBaseGenerationPath().resolve(getBasePackagePath()).resolve(packageExtension).resolve(className + ".java"), targetFileType, sourceFileHandler, sourceFile);
    }
    */

    public Path getJavaBaseGenerationPathRelativeToProject() {
        return Paths.get("target", "generated-sources", "ivif-ja");
    }

    public Template getTemplate(String name) throws IOException {
        return configuration.getTemplate(name);
    }

    public SimpleHash createSimpleHash() {
        return new SimpleHash(wrapper);
    }
}

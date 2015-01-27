package com.iorga.ivif.ja.tag;

import com.iorga.ivif.ja.tag.configurations.JAConfiguration;
import com.iorga.ivif.util.TargetFileUtils;
import com.iorga.ivif.tag.TargetFile;
import com.iorga.ivif.util.JavaClassGeneratorUtil;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.iorga.ivif.ja.tag.JavaTargetFile.JavaTargetFileId;

public abstract class JavaTargetFile<I extends JavaTargetFileId> extends TargetFile<I, JAGeneratorContext> {

    protected final String variableName;
    protected JavaClassGeneratorUtil util;

    public static class JavaTargetFileId {
        protected String simpleClassName;
        protected String packageName;
        protected String className;
        protected JAConfiguration configuration;

        public JavaTargetFileId(String simpleOrFullClassName, String packageNameOrNull, String packageNameRelativeToBase, JAConfiguration configuration) {
            if (simpleOrFullClassName.contains(".")) {
                // this is a full class name, let's split it
                simpleClassName = StringUtils.substringAfterLast(simpleOrFullClassName, ".");
                packageName = StringUtils.substringBeforeLast(simpleOrFullClassName, ".");
            } else {
                // this is a partial class name, let's complete it
                simpleClassName = simpleOrFullClassName;
                if (StringUtils.isBlank(packageNameOrNull)) {
                    // package name has not been given, let's determine it
                    packageName = configuration.getBasePackage();
                    if (StringUtils.isNotBlank(packageNameRelativeToBase)) {
                        packageName = (StringUtils.isNotBlank(packageName) ? packageName + "." : "") + packageNameRelativeToBase;
                    }
                } else {
                    // package name has been given, let's use it
                    packageName = packageNameOrNull;
                }
            }
            className = (StringUtils.isNotBlank(packageName) ? packageName + "." : "") + simpleClassName;
        }

        @Override
        public String toString() {
            return className;
        }

        @Override
        public int hashCode() {
            return className.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof JavaTargetFileId ? className.equals(((JavaTargetFileId) obj).className) : false;
        }

        public String getClassName() {
            return className;
        }

        public String getSimpleClassName() {
            return simpleClassName;
        }

        public String getPackageName() {
            return packageName;
        }

        public JAConfiguration getConfiguration() {
            return configuration;
        }
    }

    public JavaTargetFile(I id, JAGeneratorContext context) {
        super(id, context);

        this.variableName = TargetFileUtils.getVariableNameFromCamelCasedName(id.simpleClassName);
    }


    @Override
    public void render(JAGeneratorContext context) throws Exception {
        util = new JavaClassGeneratorUtil();
        ByteArrayOutputStream bodyStream = renderBody(context);
        // Now render header in case there are injections
        ByteArrayOutputStream headerStream = renderHeader(context);
        // Now add the header
        SimpleHash freemarkerContext = context.createSimpleHash();
        freemarkerContext.put("model", this);
        freemarkerContext.put("util", util);
        freemarkerContext.put("context", context);
        Template template = context.getTemplate("JavaHeader.ftl");
        File file = getPath(context).toFile();
        // create file structure
        file.getParentFile().mkdirs();
        // before writing to it
        FileOutputStream outputStream = new FileOutputStream(file);
        template.process(freemarkerContext, new OutputStreamWriter(outputStream));
        // And append the header & body
        if (headerStream != null) {
            headerStream.writeTo(outputStream);
        }
        bodyStream.writeTo(outputStream);
    }

    private ByteArrayOutputStream renderHeader(JAGeneratorContext context) throws IOException, TemplateException {
        String headerTemplate = getFreemarkerHeaderTemplateName();
        if (headerTemplate != null) {
            SimpleHash freemarkerContext = context.createSimpleHash();
            freemarkerContext.put("model", getFreemarkerModel());
            freemarkerContext.put("util", util);
            freemarkerContext.put("context", context);
            // First process body
            Template template = context.getTemplate(headerTemplate);
            ByteArrayOutputStream headerStream = new ByteArrayOutputStream();
            template.process(freemarkerContext, new OutputStreamWriter(headerStream));
            return headerStream;
        } else {
            return null;
        }
    }

    protected String getFreemarkerHeaderTemplateName() {
        // By default, no header is generated
        return null;
    }

    protected ByteArrayOutputStream renderBody(JAGeneratorContext context) throws IOException, TemplateException {
        SimpleHash freemarkerContext = context.createSimpleHash();
        freemarkerContext.put("model", getFreemarkerModel());
        freemarkerContext.put("util", util);
        freemarkerContext.put("context", context);
        // First process body
        Template template = context.getTemplate(getFreemarkerBodyTemplateName());
        ByteArrayOutputStream bodyStream = new ByteArrayOutputStream();
        template.process(freemarkerContext, new OutputStreamWriter(bodyStream));
        return bodyStream;
    }

    protected Object getFreemarkerModel() {
        return this;
    }

    protected String getFreemarkerBodyTemplateName() {
        return null;
    }

    @Override
    public Path getPathRelativeToTargetPath(JAGeneratorContext context) {
        return context.getJavaBaseGenerationPathRelativeToTargetPath().resolve(getPackageNamePath()).resolve(getSimpleClassName() + ".java");
    }

    public String getPackageName() {
        return getId().packageName;
    }

    public Path getPackageNamePath() {
        return Paths.get(getPackageName().replaceAll("\\.", "/"));
    }

    public String getClassName() {
        return getId().className;
    }

    public String getSimpleClassName() {
        return getId().simpleClassName;
    }

    public String getVariableName() {
        return variableName;
    }
}

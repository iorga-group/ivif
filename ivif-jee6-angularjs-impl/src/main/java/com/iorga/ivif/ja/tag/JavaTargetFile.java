package com.iorga.ivif.ja.tag;

import com.iorga.ivif.tag.TargetFile;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class JavaTargetFile extends TargetFile<JAGeneratorContext, String> {
    protected String classSimpleName;
    protected String packageName;
    protected String packageNameRelativeToBase;


    public JavaTargetFile(String classSimpleName, String packageName, boolean packageRelativeToBase, JAGeneratorContext context) {
        super(getPackageName(packageName, packageRelativeToBase, context)+classSimpleName, context);
        this.classSimpleName = classSimpleName;
        this.packageName = getPackageName(packageName, packageRelativeToBase, context);
        this.packageNameRelativeToBase = packageRelativeToBase ? packageName : StringUtils.removeStart(this.packageName, context.getBasePackage());
    }

    protected static String getPackageName(String packageName, boolean packageRelativeToBase, JAGeneratorContext context) {
        String basePackage = context.getBasePackage();
        return packageRelativeToBase ? (StringUtils.isNotBlank(basePackage) ? (basePackage + ".") : "") + packageName : packageName;
    }

    @Override
    public Path getPathRelativeToBasePath(JAGeneratorContext context) {
        return context.getJavaBaseGenerationPathRelativeToProject().resolve(getPackageNamePath()).resolve(getClassSimpleName()+".java");
    }

    public String getPackageName() {
        return packageName;
    }

    public Path getPackageNamePath() {
        return Paths.get(getPackageName().replaceAll("\\.", "/"));
    }

    public String getPackageNameRelativeToBase() {
        return packageNameRelativeToBase;
    }

    public String getClassName() {
        return getId();
    }

    public String getClassSimpleName() {
        return classSimpleName;
    }
}

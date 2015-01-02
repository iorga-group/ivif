package com.iorga.ivif.ja.tag;

import com.iorga.ivif.tag.TargetFile;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class JavaTargetFile extends TargetFile<JAGeneratorContext, String> {
    protected String simpleClassName;
    protected String packageName;
    protected String packageNameRelativeToBase;


    public JavaTargetFile(String simpleClassName, String packageName, boolean packageRelativeToBase, JAGeneratorContext context) {
        super(getClassName(simpleClassName, packageName, packageRelativeToBase, context), context);
        this.simpleClassName = simpleClassName;
        this.packageName = getPackageName(packageName, packageRelativeToBase, context);
        this.packageNameRelativeToBase = packageRelativeToBase ? packageName : StringUtils.removeStart(this.packageName, context.getBasePackage());
    }

    protected static String getClassName(String simpleClassName, String packageName, boolean packageRelativeToBase, JAGeneratorContext context) {
        String fullPackageName = getPackageName(packageName, packageRelativeToBase, context);
        return fullPackageName + (StringUtils.isNotBlank(fullPackageName) ? "." : "") + simpleClassName;
    }

    protected static String getPackageName(String packageName, boolean packageRelativeToBase, JAGeneratorContext context) {
        String basePackage = context.getBasePackage();
        return packageRelativeToBase ? (StringUtils.isNotBlank(basePackage) ? (basePackage + ".") : "") + packageName : packageName;
    }

    @Override
    public Path getPathRelativeToTargetPath(JAGeneratorContext context) {
        return context.getJavaBaseGenerationPathRelativeToTargetPath().resolve(getPackageNamePath()).resolve(getSimpleClassName()+".java");
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

    public String getSimpleClassName() {
        return simpleClassName;
    }
}

package com.iorga.ivif.ja.tag.configurations;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.bean.Configurations;
import com.iorga.ivif.tag.AbstractTarget;

public class JAConfiguration extends AbstractTarget<Void, JAGeneratorContext> {
    private Configurations element;
    private String basePackage;
    private String angularModuleName;


    public JAConfiguration(Configurations element) {
        this.element = element;
        this.basePackage = element.getBasePackage().getValue();
        this.angularModuleName = element.getAngularModuleName().getValue();
    }

    public Configurations getElement() {
        return element;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public String getAngularModuleName() {
        return angularModuleName;
    }
}

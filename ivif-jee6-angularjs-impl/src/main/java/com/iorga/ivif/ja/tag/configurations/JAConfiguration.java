package com.iorga.ivif.ja.tag.configurations;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.bean.AngularModuleImport;
import com.iorga.ivif.ja.tag.bean.Configurations;
import com.iorga.ivif.tag.AbstractTarget;

import java.util.ArrayList;
import java.util.List;

public class JAConfiguration extends AbstractTarget<Void, JAGeneratorContext> {
    private Configurations element;
    private String basePackage;
    private String angularModuleName;
    private List<String> angularModuleImports;


    public JAConfiguration(Configurations element) {
        this.element = element;
        this.basePackage = element.getBasePackage().getValue();
        this.angularModuleName = element.getAngularModuleName().getValue();
        this.angularModuleImports = new ArrayList<>(element.getAngularModuleImport().size());
        for (AngularModuleImport angularModuleImport : element.getAngularModuleImport()) {
            angularModuleImports.add(angularModuleImport.getValue());
        }
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

    public List<String> getAngularModuleImports() {
        return angularModuleImports;
    }
}

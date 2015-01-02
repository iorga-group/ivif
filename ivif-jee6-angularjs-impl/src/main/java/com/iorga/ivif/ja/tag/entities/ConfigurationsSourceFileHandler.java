package com.iorga.ivif.ja.tag.entities;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.tag.JAXBSourceFile;
import com.iorga.ivif.tag.SimpleJAXBSourceFileHandler;
import com.iorga.ivif.tag.bean.BasePackage;
import com.iorga.ivif.tag.bean.Configurations;

public class ConfigurationsSourceFileHandler extends SimpleJAXBSourceFileHandler<Configurations, JAGeneratorContext> {
    public ConfigurationsSourceFileHandler() {
        super(Configurations.class);
    }

    @Override
    public void init(JAXBSourceFile<Configurations> sourceFile, JAGeneratorContext context) throws Exception {
        super.init(sourceFile, context);
        // Change the context
        BasePackage basePackage = sourceFile.getContext().getBasePackage();
        if (basePackage != null) {
            context.setBasePackage(basePackage.getValue());
        }
    }
}

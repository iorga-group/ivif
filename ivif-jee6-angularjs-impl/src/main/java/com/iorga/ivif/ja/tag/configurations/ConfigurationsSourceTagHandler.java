package com.iorga.ivif.ja.tag.configurations;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.tag.JAXBSourceTagHandler;
import com.iorga.ivif.tag.bean.BasePackage;
import com.iorga.ivif.tag.bean.Configurations;

import javax.xml.bind.JAXBException;

public class ConfigurationsSourceTagHandler extends JAXBSourceTagHandler<Configurations, JAGeneratorContext> {

    public ConfigurationsSourceTagHandler() throws JAXBException {
        super(Configurations.class);
    }

    @Override
    public void init(JAGeneratorContext context) throws Exception {
        super.init(context);
        // Change the context
        BasePackage basePackage = element.getBasePackage();
        if (basePackage != null) {
            context.setBasePackage(basePackage.getValue());
        }
    }
}

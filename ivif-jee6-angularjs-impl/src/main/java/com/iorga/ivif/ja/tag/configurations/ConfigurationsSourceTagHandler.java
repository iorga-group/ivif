package com.iorga.ivif.ja.tag.configurations;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.bean.Configurations;
import com.iorga.ivif.ja.tag.views.AppJsTargetFile;
import com.iorga.ivif.tag.JAXBSourceTagHandler;
import com.iorga.ivif.tag.TargetFactory;

import javax.xml.bind.JAXBException;

public class ConfigurationsSourceTagHandler extends JAXBSourceTagHandler<Configurations, JAGeneratorContext> {
    public ConfigurationsSourceTagHandler() throws JAXBException {
        super(Configurations.class);
    }

    @Override
    public void declareTargets(JAGeneratorContext context) throws Exception {
        super.declareTargets(context);

        context.getOrCreateTarget(JAConfiguration.class, new TargetFactory<JAConfiguration, Void, JAGeneratorContext>() {
            @Override
            public JAConfiguration createTarget() throws Exception {
                return new JAConfiguration(element);
            }
        });

        context.getOrCreateTarget(AppJsTargetFile.class);
    }
}

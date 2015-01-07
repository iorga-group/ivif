package com.iorga.ivif.ja.tag;

import com.iorga.ivif.ja.tag.configurations.ConfigurationsSourceTagHandler;
import com.iorga.ivif.ja.tag.entities.EntitySourceTagHandler;
import com.iorga.ivif.ja.tag.views.GridSourceTagHandler;
import com.iorga.ivif.tag.JAXBGenerator;

public class JAGenerator extends JAXBGenerator<JAGeneratorContext> {

    public JAGenerator() {
        registerSourceTagHandlerClassForTagName("entity", EntitySourceTagHandler.class);
        registerSourceTagHandlerClassForTagName("configurations", ConfigurationsSourceTagHandler.class);
        registerSourceTagHandlerClassForTagName("grid", GridSourceTagHandler.class);
    }

    @Override
    public JAGeneratorContext createGeneratorContext() {
        return new JAGeneratorContext();
    }
}

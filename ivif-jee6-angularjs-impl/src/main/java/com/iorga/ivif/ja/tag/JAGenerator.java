package com.iorga.ivif.ja.tag;

import com.iorga.ivif.ja.tag.configurations.ConfigurationsSourceTagHandler;
import com.iorga.ivif.ja.tag.entities.EntitySourceTagHandler;
import com.iorga.ivif.ja.tag.entities.SelectionSourceTagHandler;
import com.iorga.ivif.ja.tag.views.ActionOpenViewSourceTagHandler;
import com.iorga.ivif.ja.tag.views.GridSourceTagHandler;
import com.iorga.ivif.tag.Generator;
import com.iorga.ivif.tag.SourceFileHandler;
import com.iorga.ivif.tag.JAXBSourceFileHandlerFactory;

import java.nio.file.Path;

public class JAGenerator extends Generator<JAGeneratorContext> {
    private JAXBSourceFileHandlerFactory<JAGeneratorContext> jaxbSourceFileHandlerFactory = new JAXBSourceFileHandlerFactory<>();

    public JAGenerator() {
        try {
            jaxbSourceFileHandlerFactory.registerSourceTagHandlerClass(EntitySourceTagHandler.class);
            jaxbSourceFileHandlerFactory.registerSourceTagHandlerClass(GridSourceTagHandler.class);
            jaxbSourceFileHandlerFactory.registerSourceTagHandlerClass(ActionOpenViewSourceTagHandler.class);
            jaxbSourceFileHandlerFactory.registerSourceTagHandlerClass(ConfigurationsSourceTagHandler.class);
            jaxbSourceFileHandlerFactory.registerSourceTagHandlerClass(SelectionSourceTagHandler.class);
        } catch (Exception e) {
            throw new IllegalStateException("Problem while initializing " + getClass().getName(), e);
        }
    }

    @Override
    public JAGeneratorContext createGeneratorContext() {
        return new JAGeneratorContext();
    }

    @Override
    public SourceFileHandler createSourceFileHandler(Path file, JAGeneratorContext context) {
        // TODO handle other types of file
        return jaxbSourceFileHandlerFactory.createSourceFileHandler(file);
    }
}

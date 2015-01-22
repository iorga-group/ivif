package com.iorga.ivif.ja.tag;

import com.iorga.ivif.ja.tag.configurations.ConfigurationsSourceTagHandler;
import com.iorga.ivif.ja.tag.entities.EntitySourceTagHandler;
import com.iorga.ivif.ja.tag.views.ActionOpenViewSourceTagHandler;
import com.iorga.ivif.ja.tag.views.GridSourceTagHandler;
import com.iorga.ivif.tag.Generator;
import com.iorga.ivif.tag.SourceFileHandler;
import com.iorga.ivif.tag.JAXBSourceFileHandlerFactory;

import java.nio.file.Path;

public class JAGenerator extends Generator<JAGeneratorContext> {
    private JAXBSourceFileHandlerFactory<JAGeneratorContext> JAXBSourceFileHandlerFactory = new JAXBSourceFileHandlerFactory<>();

    public JAGenerator() {
        try {
            JAXBSourceFileHandlerFactory.registerSourceTagHandlerClass(EntitySourceTagHandler.class);
            JAXBSourceFileHandlerFactory.registerSourceTagHandlerClass(GridSourceTagHandler.class);
            JAXBSourceFileHandlerFactory.registerSourceTagHandlerClass(ActionOpenViewSourceTagHandler.class);
            JAXBSourceFileHandlerFactory.registerSourceTagHandlerClass(ConfigurationsSourceTagHandler.class);
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
        return JAXBSourceFileHandlerFactory.createSourceFileHandler(file);
    }
}

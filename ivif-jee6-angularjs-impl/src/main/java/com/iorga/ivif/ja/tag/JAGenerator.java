package com.iorga.ivif.ja.tag;

import com.iorga.ivif.ja.tag.entities.EntitiesSourceFileHandler;
import com.iorga.ivif.tag.JAXBGenerator;
import com.iorga.ivif.tag.JAXBSourceFile;
import com.iorga.ivif.tag.JAXBSourceFileHandler;
import com.iorga.ivif.tag.bean.Entities;

public class JAGenerator extends JAXBGenerator<JAGeneratorContext> {

    public JAGenerator() {
        registerXmlRootElementClassForTagName("entities", Entities.class);
    }

    @Override
    protected <T, S extends JAXBSourceFile<T>> JAXBSourceFileHandler<T, JAGeneratorContext, S> createSourceFileHandler(Class<T> xmlRootElementClass) {
        if (Entities.class.isAssignableFrom(xmlRootElementClass)) {
            return (JAXBSourceFileHandler<T, JAGeneratorContext, S>) new EntitiesSourceFileHandler();
        }
        return null;
    }

    @Override
    public JAGeneratorContext createGeneratorContext() {
        return new JAGeneratorContext();
    }
}

package com.iorga.ivif.tag;

import com.google.common.collect.Maps;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Map;

public abstract class JAXBGenerator<C extends GeneratorContext<C>> extends Generator<C> {

    protected Map<String, Class<?>> registeredXmlRootElementClassByTagName = Maps.newHashMap();

    protected <T> void registerXmlRootElementClassForTagName(String tagName, Class<T> xmlRootElementClass) {
        registeredXmlRootElementClassByTagName.put(tagName, xmlRootElementClass);
    }

    protected abstract <T, S extends JAXBSourceFile<T>> JAXBSourceFileHandler<T,C,S> createSourceFileHandler(Class<T> xmlRootElementClass);

    @Override
    public SourceFileHandler<C, ?> getSourceFileHandler(Document document) {
        Element rootElement = document.getDocumentElement();
        Class xmlRootElementClass = getXmlRootElementClassByTagName(rootElement.getTagName());
        return xmlRootElementClass != null ? createSourceFileHandler(xmlRootElementClass) : null;
    }

    protected <T> Class<T> getXmlRootElementClassByTagName(String tagName) {
        return (Class<T>) registeredXmlRootElementClassByTagName.get(tagName);
    }
}

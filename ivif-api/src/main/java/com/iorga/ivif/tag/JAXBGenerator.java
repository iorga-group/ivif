package com.iorga.ivif.tag;

import com.google.common.collect.Maps;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Map;

public abstract class JAXBGenerator<C extends GeneratorContext<C>> extends Generator<C> {

    protected Map<Class<?>, JAXBSourceFileHandler<?, C, ? extends JAXBSourceFile<?>>> sourceFileHandlers = Maps.newHashMap();
    protected Map<String, Class<?>> registeredXmlRootElementClassByTagName = Maps.newHashMap();

    protected <T> void registerXmlRootElementClassForTagName(String tagName, Class<T> xmlRootElementClass) {
        registeredXmlRootElementClassByTagName.put(tagName, xmlRootElementClass);
    }

    protected abstract <T, S extends JAXBSourceFile<T>> JAXBSourceFileHandler<T,C,S> createSourceFileHandler(Class<T> xmlRootElementClass);

    @Override
    public SourceFileHandler<C, ?> getSourceFileHandler(Document document) {
        Element rootElement = document.getDocumentElement();
        Class xmlRootElementClass = getXmlRootElementClassByTagName(rootElement.getTagName());
        return getOrCreateSourceFileHandler(xmlRootElementClass);
    }

    public <T, S extends JAXBSourceFile<T>> JAXBSourceFileHandler<T, C, S> getOrCreateSourceFileHandler(Class<T> xmlRootElementClass) {
        JAXBSourceFileHandler<T, C, S> sourceFileHandler = (JAXBSourceFileHandler<T, C, S>) sourceFileHandlers.get(xmlRootElementClass);
        if (sourceFileHandler == null) {
            sourceFileHandler = createSourceFileHandler(xmlRootElementClass);
            sourceFileHandlers.put(xmlRootElementClass, sourceFileHandler);
        }
        return sourceFileHandler;
    }

    protected <T> Class<T> getXmlRootElementClassByTagName(String tagName) {
        return (Class<T>) registeredXmlRootElementClassByTagName.get(tagName);
    }
}

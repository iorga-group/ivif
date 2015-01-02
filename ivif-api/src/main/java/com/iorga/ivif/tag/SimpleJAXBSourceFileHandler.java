package com.iorga.ivif.tag;

public abstract class SimpleJAXBSourceFileHandler<T, C extends GeneratorContext<C>> extends JAXBSourceFileHandler<T, C, JAXBSourceFile<T>> {
    public SimpleJAXBSourceFileHandler(Class<T> xmlRootElementClass) {
        super(xmlRootElementClass);
    }

    @Override
    protected JAXBSourceFile<T> createSourceFile(T rootElement, DocumentToProcess documentToProcess, C context) {
        return new JAXBSourceFile<>(rootElement, documentToProcess.getPath());
    }
}

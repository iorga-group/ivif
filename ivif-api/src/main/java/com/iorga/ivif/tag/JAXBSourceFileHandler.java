package com.iorga.ivif.tag;

import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

public abstract class JAXBSourceFileHandler<T, C extends GeneratorContext<C>, S extends JAXBSourceFile<T>> implements SourceFileHandler<C, S> {
    protected Class<T> xmlRootElementClass;

    protected abstract S createSourceFile(T rootElement, DocumentToProcess documentToProcess, C context);

    public JAXBSourceFileHandler(Class<T> xmlRootElementClass) {
        this.xmlRootElementClass = xmlRootElementClass;
    }

    @Override
    public S parse(DocumentToProcess documentToProcess, C context) throws Exception {
        Document document = documentToProcess.getDocument();
        Unmarshaller unmarshaller = JAXBContext.newInstance(xmlRootElementClass).createUnmarshaller();
        T rootElement = (T) unmarshaller.unmarshal(document);
        S sourceFile = createSourceFile(rootElement, documentToProcess, context);
        context.declareCreatedSourceFile(sourceFile, this);
        return sourceFile;
    }

    @Override
    public void init(S sourceFile, C context) throws Exception {
        // Do nothing
    }

    @Override
    public void prepareTargetFiles(S sourceFile, C context) throws Exception {
        // Do nothing
    }
}

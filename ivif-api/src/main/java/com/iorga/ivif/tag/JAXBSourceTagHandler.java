package com.iorga.ivif.tag;

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamReader;

public abstract class JAXBSourceTagHandler<T, C extends GeneratorContext<C>> implements SourceTagHandler<C> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JAXBSourceTagHandler.class);

    protected final Class<T> xmlElementClass;
    protected final Unmarshaller unmarshaller;
    protected T element;
    protected DocumentToProcess documentToProcess;
    protected Location location;

    public JAXBSourceTagHandler(Class<T> xmlElementClass) throws JAXBException {
        this.xmlElementClass = xmlElementClass;
        unmarshaller = JAXBContext.newInstance(xmlElementClass).createUnmarshaller();
    }

    @Override
    public boolean parse(DocumentToProcess documentToProcess, C context) {
        this.documentToProcess = documentToProcess;
        XMLStreamReader xmlStreamReader = documentToProcess.getXmlStreamReader();
        this.location = xmlStreamReader.getLocation();
        try {
            this.element = (T) unmarshaller.unmarshal(xmlStreamReader);
            return true;
        } catch (JAXBException e) {
            LOGGER.warn("Problem while parsing "+documentToProcess.getPath(), e);
            return false;
        }
    }

    @Override
    public void init(C context) throws Exception {
        // Do nothing by default
    }

    @Override
    public void prepareTargetFiles(C context) throws Exception {
        // Do nothing by default
    }

    public T getElement() {
        return element;
    }

    @Override
    public String toString() {
        StringBuilder toString = new StringBuilder();
        toString.append(getClass().getName());
        if (documentToProcess != null) {
            toString.append("[").append(documentToProcess.getPath());
            if (location != null) {
                toString.append(":").append(location.getLineNumber()).append(",").append(location.getColumnNumber());
            }
            toString.append("]");
        }
        return toString.toString();
    }
}

package com.iorga.ivif.tag;

import javax.xml.stream.XMLStreamReader;
import java.nio.file.Path;

public class DocumentToProcess {
    private Path path;
    private XMLStreamReader xmlStreamReader;

    public DocumentToProcess(Path path, XMLStreamReader xmlStreamReader) {
        this.path = path;
        this.xmlStreamReader = xmlStreamReader;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public XMLStreamReader getXmlStreamReader() {
        return xmlStreamReader;
    }

    public void setXmlStreamReader(XMLStreamReader xmlStreamReader) {
        this.xmlStreamReader = xmlStreamReader;
    }
}

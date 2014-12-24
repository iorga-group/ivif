package com.iorga.ivif.tag;

import org.w3c.dom.Document;

import java.nio.file.Path;

public class DocumentToProcess {
    private Path path;
    private Document document;

    public DocumentToProcess(Path path, Document document) {
        this.path = path;
        this.document = document;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}

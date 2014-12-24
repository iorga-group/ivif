package com.iorga.ivif.tag;

import java.nio.file.Path;

public class JAXBSourceFile<T> implements SourceFile {
    protected final T context;
    protected final Path path;

    public JAXBSourceFile(T context, Path path) {
        this.context = context;
        this.path = path;
    }

    public T getContext() {
        return context;
    }

    @Override
    public Path getPath() {
        return path;
    }
}

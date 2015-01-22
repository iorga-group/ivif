package com.iorga.ivif.tag;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class TargetFile<I, C extends GeneratorContext<C>> implements Target<I, C> {
    protected Path path;
    protected I id;

    public TargetFile(I id, C context) {
        this.id = id;
    }

    public void prepare(C context) throws Exception {
        // Do nothing by default
    }

    public void render(C context) throws Exception {
        // Do nothing by default
    }

    public Path getPathRelativeToTargetPath(C context) {
        // By default, path is in the base directory
        return Paths.get("");
    }

    public Path getPath(C context) {
        return context.getTargetPath().resolve(getPathRelativeToTargetPath(context));
    }

    public I getId() {
        return id;
    }

    @Override
    public String toString() {
        return getClass()+"{" +
                "path=" + path +
                ", id=" + id +
                '}';
    }
}

package com.iorga.ivif.tag;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class TargetFile<C extends GeneratorContext<C>, I> {
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

    public Path getPathRelativeToBasePath(C context) {
        // By default, path is in the base directory
        return Paths.get("");
    }

    public Path getPath(C context) {
        return context.getBasePath().resolve(getPathRelativeToBasePath(context));
    }

    public I getId() {
        return id;
    }
}

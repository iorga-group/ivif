package com.iorga.ivif.tag;

public interface SourceFileHandler<GC extends GeneratorContext, SF extends SourceFile> {
    SF parse(DocumentToProcess documentToProcess, GC context) throws Exception;

    void init(SF sourceFile, GC context) throws Exception;

    void prepareTargetFiles(SF sourceFile, GC context) throws Exception;
}

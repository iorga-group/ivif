package com.iorga.ivif.tag;

public interface SourceTagHandler<C extends GeneratorContext<C>> {
    boolean parse(DocumentToProcess documentToProcess, C context) throws Exception;

    void declareTargets(C context) throws Exception;
}

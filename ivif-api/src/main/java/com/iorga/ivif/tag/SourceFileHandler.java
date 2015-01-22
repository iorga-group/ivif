package com.iorga.ivif.tag;

public interface SourceFileHandler<C> {
    public abstract void parse(C context);

    public abstract void declareTargets(C context) throws Exception;
}

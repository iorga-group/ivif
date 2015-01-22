package com.iorga.ivif.tag;

public interface Target<I, C extends GeneratorContext<C>> extends Identifiable<I> {
    void prepare(C context) throws Exception;
}

package com.iorga.ivif.tag;

public interface TargetFactory<T extends Target<I, C>, I, C extends GeneratorContext<C>> {
    public T createTarget() throws Exception;
}

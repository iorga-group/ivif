package com.iorga.ivif.tag;

public class AbstractTarget<I, C extends GeneratorContext<C>> implements Target<I, C> {
    private I id;

    public AbstractTarget() {
    }

    public AbstractTarget(I id) {
        this.id = id;
    }

    public void prepare(C context) throws Exception {
        // Do nothing
    }
    public I getId() {
        return id;
    }
}

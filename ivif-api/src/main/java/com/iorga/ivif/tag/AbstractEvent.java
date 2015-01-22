package com.iorga.ivif.tag;

public abstract class AbstractEvent<I> implements Event<I> {
    private I id;

    public AbstractEvent() {
    }

    public AbstractEvent(I id) {
        this.id = id;
    }

    @Override
    public I getId() {
        return id;
    }
}

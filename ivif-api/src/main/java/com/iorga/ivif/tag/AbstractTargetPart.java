package com.iorga.ivif.tag;

public class AbstractTargetPart<I, T extends Target<TI, C>, TI, C extends GeneratorContext<C>> implements TargetPart<I, T, TI, C> {
    protected T target;
    protected I partId;
    protected TargetPartId<I, T, TI, C> id;

    public AbstractTargetPart(I partId, T target) {
        this.target = target;
        this.partId = partId;
        this.id = new TargetPartId<I, T, TI, C>(partId, (Class<? extends T>)target.getClass(), target.getId());
    }

    @Override
    public T getTarget() {
        return target;
    }

    public I getPartId() {
        return partId;
    }

    @Override
    public TargetPartId<I, T, TI, C> getId() {
        return id;
    }
}

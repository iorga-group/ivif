package com.iorga.ivif.tag;

public abstract class Waiter<T, I, W> {
    protected final I targetId;
    protected final W waiter;
    protected final Class<T> targetClass;

    public Waiter(Class<T> targetClass, I targetId, W waiter) {
        this.targetClass = targetClass;
        this.targetId = targetId;
        this.waiter = waiter;
    }

    public abstract void onPrepared(T target) throws Exception;

    public Class<T> getTargetClass() {
        return targetClass;
    }

    public I getTargetId() {
        return targetId;
    }

    public W getWaiter() {
        return waiter;
    }
}

package com.iorga.ivif.tag;

public abstract class PartWaiter<T, I, C extends GeneratorContext<C>> extends Waiter<T, I, Object> {

    public PartWaiter(Class<T> targetClass, I targetId, Object waiter) {
        super(targetClass, targetId, waiter);
    }
}

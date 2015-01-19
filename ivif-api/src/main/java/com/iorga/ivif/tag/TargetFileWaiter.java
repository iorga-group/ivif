package com.iorga.ivif.tag;

public abstract class TargetFileWaiter<T extends TargetFile<C, I>, I, C extends GeneratorContext<C>> extends Waiter<T, I, Object> {

    public TargetFileWaiter(Class<T> targetClass, I targetId, Object waiter) {
        super(targetClass, targetId, waiter);
    }
}

package com.iorga.ivif.tag;

import com.iorga.ivif.tag.TargetPreparedEvent.TargetPreparedEventId;

public abstract class TargetPreparedWaiter<T extends Target<I, C>, I, C extends GeneratorContext<C>> extends AbstractEventWaiter<TargetPreparedEvent<T, I, C>, TargetPreparedEventId<T, I, C>> {
    public TargetPreparedWaiter(Class<? extends T> targetClass, I targetId, Object waiterSource) {
        super((Class<TargetPreparedEvent<T, I, C>>)(Class)TargetPreparedEvent.class, new TargetPreparedEventId<>(targetClass, targetId), waiterSource);
    }

    @Override
    public void onThrown(TargetPreparedEvent<T, I, C> event) throws Exception {
        onTargetPrepared(event.getTarget());
    }

    protected abstract void onTargetPrepared(T target) throws Exception;
}

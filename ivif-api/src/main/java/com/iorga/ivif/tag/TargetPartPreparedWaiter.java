package com.iorga.ivif.tag;

import com.iorga.ivif.tag.TargetPartPreparedEvent.TargetPartPreparedEventId;

public abstract class TargetPartPreparedWaiter<P extends TargetPart<I, T, TI, C>, I, T extends Target<TI, C>, TI, C extends GeneratorContext<C>> extends AbstractEventWaiter<TargetPartPreparedEvent<P, I, T, TI, C>, TargetPartPreparedEventId<P, I, T, TI, C>> {

    public TargetPartPreparedWaiter(Class<? extends P> targetPartClass, I targetPartId, T target, Object waiterSource) {
        this(targetPartClass, targetPartId, (Class<? extends T>) target.getClass(), target.getId(), waiterSource);
    }

    public TargetPartPreparedWaiter(Class<? extends P> targetPartClass, I targetPartId, Class<? extends T> targetClass, TI targetId, Object waiterSource) {
        super((Class<TargetPartPreparedEvent<P, I, T, TI, C>>)(Class)TargetPartPreparedEvent.class, new TargetPartPreparedEventId<>(targetPartClass, targetPartId, targetClass, targetId), waiterSource);
    }

    @Override
    public void onThrown(TargetPartPreparedEvent<P, I, T, TI, C> event) throws Exception {
        onTargetPartPrepared(event.getTargetPart());
    }

    protected abstract void onTargetPartPrepared(P targetPart) throws Exception;
}

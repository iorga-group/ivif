package com.iorga.ivif.tag;

public interface EventWaiter<E extends Event<I>, I> {
    public void onThrown(E event) throws Exception;

    public Class<E> getEventClass();

    public I getEventId();

    public Object getWaiterSource();
}

package com.iorga.ivif.tag;

public abstract class AbstractEventWaiter<E extends Event<I>, I> implements EventWaiter<E, I> {
    protected Class<E> eventClass;
    protected I eventId;
    protected Object waiterSource;

    public AbstractEventWaiter(Class<E> eventClass, I eventId, Object waiterSource) {
        this.eventClass = eventClass;
        this.eventId = eventId;
        this.waiterSource = waiterSource;
    }

    @Override
    public Class<E> getEventClass() {
        return eventClass;
    }

    @Override
    public I getEventId() {
        return eventId;
    }

    @Override
    public Object getWaiterSource() {
        return waiterSource;
    }
}

package com.iorga.ivif.tag;

import com.google.common.collect.*;
import com.iorga.ivif.util.QueueRemoverIterable;

import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public abstract class GeneratorContext<C extends GeneratorContext<C>> {


    protected class EventContext<I, E extends Event<I>> {
        protected E event;
        protected Deque<EventWaiter<E, I>> waiters = new LinkedList<>();
    }
    protected Map<Class<? extends Event<?>>, Map<?, ? extends EventContext<?, ?>>> eventContexts = Maps.newHashMap();
    protected Multimap<Target<?, C>, EventWaiter<?, ?>> targetSourceWaitersByTarget = HashMultimap.create();

    protected class TargetContext<I, T extends Target<I, C>> {
        protected T target;
    }
    protected Map<Class<? extends Target<?, C>>, Map<?, ? extends TargetContext<?, ?>>> targetContexts = Maps.newHashMap();

    private Deque<Target<?, C>> newTargets = new LinkedList<>();
    private Set<Target<?, C>> preparedTargetsButTargetPreparedNotThrown = new HashSet<>();

    private Deque<TargetFile<?, C>> preparedTargetsButNotRendered = new LinkedList<>();

    private Path sourcePath = Paths.get("");
    private Path targetPath;


    protected <I, E extends Event<I>> EventContext<I, E> getOrCreateEventContext(Class<E> eventClass, I eventId) {
        Map<I, EventContext<I, E>> eventContextsForThatClass = (Map<I, EventContext<I, E>>) eventContexts.get(eventClass);
        if (eventContextsForThatClass == null) {
            eventContextsForThatClass = Maps.newHashMap();
            eventContexts.put(eventClass, eventContextsForThatClass);
        }
        EventContext<I, E> eventContext = eventContextsForThatClass.get(eventId);
        if (eventContext == null) {
            eventContext = new EventContext<>();
            eventContextsForThatClass.put(eventId, eventContext);
        }
        return eventContext;
    }

    public <I, E extends Event<I>> void waitForEvent(EventWaiter<E, I> waiter) throws Exception {
        EventContext<I, E> eventContext = getOrCreateEventContext(waiter.getEventClass(), waiter.getEventId());
        if (eventContext.event == null) {
            // event not yet thrown, must register the waiter
            eventContext.waiters.add(waiter);
            Object waiterSource = waiter.getWaiterSource();
            if (waiterSource instanceof Target) {
                // this is a Target, will register it also
                targetSourceWaitersByTarget.put((Target<?, C>) waiterSource, waiter);
            }
        } else {
            // event already thrown, let's say the waiter so
            waiter.onThrown(eventContext.event);
        }
    }

    public <I, E extends Event<I>> void throwEvent(E event) throws Exception {
        EventContext<I, E> eventContext = getOrCreateEventContext((Class<E>)event.getClass(), event.getId());
        if (eventContext.event == null) {
            eventContext.event = event;
            Deque<EventWaiter<E, I>> waiters = eventContext.waiters;
            while (!waiters.isEmpty()) {
                EventWaiter<E, I> waiter = waiters.remove();
                Object waiterSource = waiter.getWaiterSource();
                if (waiterSource instanceof Target) {
                    // this is a Target, will deregister it it also
                    targetSourceWaitersByTarget.remove(waiterSource, waiter);
                }
                waiter.onThrown(event);
            }
        } else {
            // weird, this has already been thrown
            throw new IllegalStateException("Event has already been thrown : " + eventContext.event + ". Wanted to throw " + event);
        }
    }


    protected <I, T extends Target<I, C>> TargetContext<I, T> getOrCreateTargetContext(Class<T> targetClass, I targetId) {
        Map<I, TargetContext<I, T>> targetContextsForThatClass = (Map<I, TargetContext<I, T>>) targetContexts.get(targetClass);
        if (targetContextsForThatClass == null) {
            targetContextsForThatClass = Maps.newHashMap();
            targetContexts.put(targetClass, targetContextsForThatClass);
        }
        TargetContext<I, T> targetContext = targetContextsForThatClass.get(targetId);
        if (targetContext == null) {
            targetContext = new TargetContext<>();
            targetContextsForThatClass.put(targetId, targetContext);
        }
        return targetContext;
    }

    public <I, T extends Target<I, C>> T getOrCreateTarget(Class<T> targetClass, I targetId, TargetFactory<T, I, C> targetFactory) throws Exception {
        TargetContext<I, T> targetContext = getOrCreateTargetContext(targetClass, targetId);
        T target = targetContext.target;
        if (target == null) {
            // create target file
            target = targetFactory.createTarget();
            targetContext.target = target;
            // Append to new targets
            newTargets.add(target);
        }
        return target;
    }

    public <I, T extends Target<I, C>> T getOrCreateTarget(Class<T> targetClass) throws Exception {
        return getOrCreateTarget(targetClass, (I)null);
    }

    public <I, T extends Target<I, C>> T getOrCreateTarget(Class<T> targetClass, TargetFactory<T, I, C> targetFactory) throws Exception {
        return getOrCreateTarget(targetClass, null, targetFactory);
    }

    public <I, T extends Target<I, C>> T getOrCreateTarget(final Class<T> targetClass, final I targetId) throws Exception {
        return getOrCreateTarget(targetClass, targetId, new TargetFactory<T, I, C>() {
            @Override
            public T createTarget() throws Exception {
                if (targetId != null) {
                    Constructor<T> constructor = targetClass.getConstructor(targetId.getClass(), GeneratorContext.this.getClass());
                    return constructor.newInstance(targetId, GeneratorContext.this);
                } else {
                    Constructor<T> constructor = targetClass.getConstructor(GeneratorContext.this.getClass());
                    return constructor.newInstance(GeneratorContext.this);
                }
            }
        });
    }

    public Iterable<Target<?, C>> iterateOnNewTargetsToPrepareThem() {
        return new QueueRemoverIterable<>(newTargets);
    }

    public void declarePreparedCalled(Target<?, C> target) throws Exception {
        preparedTargetsButTargetPreparedNotThrown.add(target);
        // now must throw "TargetPrepared" for each Target which are not waiting for something
        // First duplicate the current preparedTargetsButTargetPreparedNotThrown in order to remove from them the waiting targets
        throwTargetPreparedEvents();
    }

    protected void throwTargetPreparedEvents() throws Exception {
        boolean atLeastOneTargetPreparedEventThrown = true;
        while (atLeastOneTargetPreparedEventThrown) {
            atLeastOneTargetPreparedEventThrown = false;
            Set<Target<?, C>> canThrowTargetPreparedTargets = new HashSet<>(preparedTargetsButTargetPreparedNotThrown);
            for (Target<?, C> targetWaiter : targetSourceWaitersByTarget.keySet()) {
                // remove each waiting targets : they are still waiting so they are not yet fully "prepared"
                canThrowTargetPreparedTargets.remove(targetWaiter);
            }
            for (Target<?, C> canThrowTargetPreparedTarget : canThrowTargetPreparedTargets) {
                preparedTargetsButTargetPreparedNotThrown.remove(canThrowTargetPreparedTarget);
                atLeastOneTargetPreparedEventThrown = true;
                throwEvent(new TargetPreparedEvent(canThrowTargetPreparedTarget));
                if (canThrowTargetPreparedTarget instanceof TargetFile) {
                    preparedTargetsButNotRendered.add((TargetFile<?, C>) canThrowTargetPreparedTarget);
                }
            }
        }
    }

    public Iterable<TargetFile<?, C>> iterateOnPreparedTargetFilesToRenderThem() {
        return new QueueRemoverIterable<>(preparedTargetsButNotRendered);
    }

    public Collection<EventWaiter<? extends Event<?>, ?>> getAllEventWaiters() {
        List<EventWaiter<? extends Event<?>, ?>> list = new ArrayList<>();
        for (Map<?, ? extends EventContext<?, ?>> eventContextsMap : eventContexts.values()) {
            for (EventContext<?, ?> eventContext : eventContextsMap.values()) {
                list.addAll(eventContext.waiters);
            }
        }
        return list;
    }

    /// Getters & Setters


    public void setSourcePath(Path sourcePath) {
        this.sourcePath = sourcePath;
    }

    public Path getSourcePath() {
        return sourcePath;
    }

    public void setTargetPath(Path targetPath) {
        this.targetPath = targetPath;
    }

    public Path getTargetPath() {
        return targetPath;
    }
}

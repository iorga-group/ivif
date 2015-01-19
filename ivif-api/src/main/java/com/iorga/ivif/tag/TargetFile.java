package com.iorga.ivif.tag;

import com.google.common.collect.Maps;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public abstract class TargetFile<C extends GeneratorContext<C>, I> {
    protected Path path;
    protected I id;

    protected Map<Class<?>, Map<?, ? extends PartAndWaiters<?, ?>>> partsByClass = new HashMap<>();

    protected class PartAndWaiters<TI, T> {
        protected T part;
        protected Deque<PartWaiter<T, TI, C>> waiters = new LinkedList<>();
    }

    public TargetFile(I id, C context) {
        this.id = id;
    }

    public void prepare(C context) throws Exception {
        // Do nothing by default
    }

    public void render(C context) throws Exception {
        // Do nothing by default
    }

    public Path getPathRelativeToTargetPath(C context) {
        // By default, path is in the base directory
        return Paths.get("");
    }

    public <T, TI> void waitForPartToBePrepared(PartWaiter<T, TI, C> partWaiter) throws Exception {
        PartAndWaiters<TI, T> partAndWaiters = getOrCreatePartAndWaiters(partWaiter.getTargetClass(), partWaiter.getTargetId());
        if (partAndWaiters.part == null) {
            // this part does not exists yet, must register the part waiter
            partAndWaiters.waiters.add(partWaiter);
        } else {
            // this part already exists, we just have to call the waiter on it
            partWaiter.onPrepared(partAndWaiters.part);
        }
    }

    protected <T, TI> PartAndWaiters<TI, T> getOrCreatePartAndWaiters(Class<T> partClass, TI id) {
        Map<TI, PartAndWaiters<TI, T>> parts = (Map<TI, PartAndWaiters<TI, T>>) partsByClass.get(partClass);
        if (parts == null) {
            parts = Maps.newHashMap();
            partsByClass.put(partClass, parts);
        }
        PartAndWaiters<TI, T> partAndWaiters = parts.get(id);
        if (partAndWaiters == null) {
            partAndWaiters = new PartAndWaiters<>();
            parts.put(id, partAndWaiters);
        }
        return partAndWaiters;
    }

    public <T, TI> void declarePartPrepared(T part, TI partId) throws Exception {
        PartAndWaiters<TI, T> partAndWaiters = getOrCreatePartAndWaiters((Class<T>)part.getClass(), partId);
        partAndWaiters.part = part;
        // now call each waiter
        Deque<PartWaiter<T, TI, C>> waiters = partAndWaiters.waiters;
        while (!waiters.isEmpty()) {
            PartWaiter<T, TI, C> partWaiter = waiters.remove();
            partWaiter.onPrepared(part);
        }
    }

    public Collection<PartWaiter<?, ?, C>> getPartWaiters() {
        List<PartWaiter<?, ?, C>> finalList = new ArrayList<>();
        for (Map<?, ? extends PartAndWaiters<?, ?>> partsMap : partsByClass.values()) {
            for (PartAndWaiters<?, ?> partAndWaiters : partsMap.values()) {
                finalList.addAll(partAndWaiters.waiters);
            }
        }
        return finalList;
    }

    public Path getPath(C context) {
        return context.getTargetPath().resolve(getPathRelativeToTargetPath(context));
    }

    public I getId() {
        return id;
    }
}

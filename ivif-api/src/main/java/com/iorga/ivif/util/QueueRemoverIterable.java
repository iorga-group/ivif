package com.iorga.ivif.util;

import java.util.Iterator;
import java.util.Queue;

public class QueueRemoverIterable<T> implements Iterable<T> {
    private final Queue<T> queue;

    public QueueRemoverIterable(Queue<T> queue) {
        this.queue = queue;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return !queue.isEmpty();
            }

            @Override
            public T next() {
                return queue.remove();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}

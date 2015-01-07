package com.iorga.ivif.tag;

public abstract class PartWaiter<T, I, C extends GeneratorContext<C>> {
    private final I partId;
    private final TargetFile<C, ?> targetFileWaiter;

    public PartWaiter(TargetFile<C, ?> targetFileWaiter, I partId) {
        this.targetFileWaiter = targetFileWaiter;
        this.partId = partId;
    }

    public abstract void onPrepared(T part) throws Exception;

    public TargetFile<C, ?> getTargetFileWaiter() {
        return targetFileWaiter;
    }

    public I getPartId() {
        return partId;
    }
}

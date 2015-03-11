package com.iorga.ivif.ja;

public interface IEntity<I> {
    public I entityId();

    public void entityId(I id);

    public String displayName();
}

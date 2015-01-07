package com.iorga.ivif.ja;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GridSearchParam<F> {
    public int limit;
    public int offset;

    public F filter;
    public Sorting sorting;
}

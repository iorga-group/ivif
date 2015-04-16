package com.iorga.ivif.ja;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GridSearchParam<F> {
    public Integer limit;
    public Integer offset;

    public F filter;
    public Sorting sorting;
}

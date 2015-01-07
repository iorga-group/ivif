package com.iorga.ivif.ja;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum SortingType {
    @XmlEnumValue("asc")
    ASCENDING,
    @XmlEnumValue("desc")
    DESCENDING
}

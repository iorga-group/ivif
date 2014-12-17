package com.iorga.ivif.util;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum SortingType {
    @XmlEnumValue("asc")
    ASCENDING,
    @XmlEnumValue("desc")
    DESCENDING
}

package com.iorga.ivif.ja;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import java.util.HashMap;
import java.util.Map;

@XmlEnum
public enum SortingType {
    @XmlEnumValue("asc")
    ASCENDING,
    @XmlEnumValue("desc")
    DESCENDING;

    private static Map<String, SortingType> sortingTypeByValue = new HashMap<>();
    private static Map<SortingType, String> valueBySortingType = new HashMap<>();
    static {
        SortingType[] enumConstants = SortingType.class.getEnumConstants();
        for (SortingType sortingType : enumConstants) {
            try {
                String value = SortingType.class.getField(sortingType.name()).getAnnotation(XmlEnumValue.class).value();
                sortingTypeByValue.put(value, sortingType);
                valueBySortingType.put(sortingType, value);
            } catch (NoSuchFieldException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @JsonCreator
    public static SortingType create(String value) {
        return sortingTypeByValue.get(value);
    }

    @JsonValue
    public String getValue() {
        return valueBySortingType.get(this);
    }
}

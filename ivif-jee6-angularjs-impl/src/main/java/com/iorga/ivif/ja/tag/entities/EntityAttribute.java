package com.iorga.ivif.ja.tag.entities;

import com.iorga.ivif.tag.bean.AttributeType;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.JAXBElement;

public class EntityAttribute {
    private final JAXBElement<? extends AttributeType> element;
    private String type;
    private final String capitalizedName;
    private final boolean manyToOne;

    public EntityAttribute(JAXBElement<? extends AttributeType> element) {
        this.element = element;
        capitalizedName = StringUtils.capitalize(element.getValue().getName());
        manyToOne = "many-to-one".equals(element.getName().getLocalPart());
    }

    /// Getters & Setters

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JAXBElement<? extends AttributeType> getElement() {
        return element;
    }

    public String getCapitalizedName() {
        return capitalizedName;
    }

    public boolean isManyToOne() {
        return manyToOne;
    }
}

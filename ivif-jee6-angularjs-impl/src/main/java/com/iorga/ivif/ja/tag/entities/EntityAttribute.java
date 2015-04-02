package com.iorga.ivif.ja.tag.entities;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.JavaStaticField;
import com.iorga.ivif.ja.tag.entities.EntityTargetFile.EntityTargetFileId;
import com.iorga.ivif.tag.AbstractTargetPart;
import com.iorga.ivif.tag.TargetPart;
import com.iorga.ivif.tag.bean.AttributeType;
import com.iorga.ivif.util.TargetFileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.JAXBElement;

public class EntityAttribute extends AbstractTargetPart<String, EntityTargetFile, EntityTargetFileId, JAGeneratorContext> {
    private final JAXBElement<? extends AttributeType> element;
    private String type;
    private final String capitalizedName;
    private final boolean manyToOne;
    private final boolean _enum;
    private final String title;
    private String fromType;
    private JavaStaticField trueValueStaticField;
    private JavaStaticField falseValueStaticField;
    private String getterName;
    private String setterName;

    public EntityAttribute(JAXBElement<? extends AttributeType> element, EntityTargetFile entityTargetFile) {
        super(element.getValue().getName(), entityTargetFile);

        this.element = element;
        AttributeType elementValue = element.getValue();
        capitalizedName = StringUtils.capitalize(elementValue.getName());
        getterName = "get" + capitalizedName;
        setterName = "set" + capitalizedName;
        final String nameLocalPart = element.getName().getLocalPart();
        manyToOne = "many-to-one".equals(nameLocalPart);
        _enum = "enum".equals(nameLocalPart);
        String elementTitle = elementValue.getTitle();
        if (StringUtils.isNotBlank(elementTitle)) {
            title = elementTitle;
        } else {
            title = TargetFileUtils.getTitleFromCamelCasedName(elementValue.getName());
        }
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

    public String getTitle() {
        return title;
    }


    public void setFromType(String fromType) {
        this.fromType = fromType;
    }

    public String getFromType() {
        return fromType;
    }

    public void setTrueValueStaticField(JavaStaticField trueValueStaticField) {
        this.trueValueStaticField = trueValueStaticField;
    }

    public JavaStaticField getTrueValueStaticField() {
        return trueValueStaticField;
    }

    public void setFalseValueStaticField(JavaStaticField falseValueStaticField) {
        this.falseValueStaticField = falseValueStaticField;
    }

    public JavaStaticField getFalseValueStaticField() {
        return falseValueStaticField;
    }

    public String getGetterName() {
        return getterName;
    }

    public String getSetterName() {
        return setterName;
    }

    public boolean isEnum() {
        return _enum;
    }
}

package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.entities.EntityAttribute;
import com.iorga.ivif.tag.bean.Column;
import org.apache.commons.lang3.StringUtils;

public class GridColumn {
    private final Column element;
    private final EntityAttribute entityAttribute;
    private final String title;
    private final String refVariableName;

    public GridColumn(Column element, EntityAttribute entityAttribute) {
        this.element = element;
        this.entityAttribute = entityAttribute;
        String elementTitle = element.getTitle();
        this.title = StringUtils.isNotBlank(elementTitle) ? elementTitle : entityAttribute.getTitle();
        this.refVariableName = element.getRef().replaceAll("\\.", "_");
    }

    /// Getters & Setters

    public String getTitle() {
        return title;
    }

    public Column getElement() {
        return element;
    }

    public EntityAttribute getEntityAttribute() {
        return entityAttribute;
    }

    public String getRefVariableName() {
        return refVariableName;
    }
}

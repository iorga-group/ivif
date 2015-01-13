package com.iorga.ivif.ja.tag;

import org.apache.commons.lang3.StringUtils;

public class JavaStaticField {
    private String name;
    private String type;
    private String value;

    public JavaStaticField() {}

    public JavaStaticField(String name, String type, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public static JavaStaticField createFromVariableName(String variableName, String type, String value) {
        String[] camelCaseParts = StringUtils.splitByCharacterTypeCamelCase(variableName);
        StringBuilder nameBuilder = new StringBuilder();
        boolean first = true;
        for (String camelCasePart : camelCaseParts) {
            if (first) {
                first = false;
            } else {
                nameBuilder.append("_");
            }
            nameBuilder.append(StringUtils.upperCase(camelCasePart));
        }
        return new JavaStaticField(nameBuilder.toString(), type, value);
    }

    /// Getters & Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

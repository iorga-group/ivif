package com.iorga.ivif.test.entity.select;

import com.iorga.ivif.ja.EnumUserType;
import com.iorga.ivif.ja.Valuable;
import java.lang.Override;
import java.lang.String;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

@XmlEnum
public enum UserStatusType implements Valuable<String> {
    @XmlEnumValue("ACTIVE")
    ACTIVE("ACTIVE"),
    @XmlEnumValue("DIS")
    DISABLED("DIS"),
    @XmlEnumValue("?")
    UNKNOWN("?");

    private String value;
    private static Map<String, UserStatusType> selectionByValue = new HashMap<>();

    static {
        selectionByValue.put("ACTIVE", ACTIVE);
        selectionByValue.put("DIS", DISABLED);
        selectionByValue.put("?", UNKNOWN);
    }

    public static class UserType extends EnumUserType<UserStatusType, String> {

        @Override
        protected UserStatusType getByValue(String value) {
            return UserStatusType.byValue(value);
        }
    }


    UserStatusType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static UserStatusType byValue(String value) {
        return selectionByValue.get(value);
    }

    public static UserStatusType fromString(String value) {
        return byValue(value);
    }

    @JsonValue
    public String value() {
        return value;
    }

}
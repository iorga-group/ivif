package com.iorga.ivif.test.entity.select;

import com.iorga.ivif.ja.EnumUserType;
import com.iorga.ivif.ja.Valuable;
import java.lang.Integer;
import java.lang.Override;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

@XmlEnum
public enum UserPassType implements Valuable<Integer> {
    @XmlEnumValue("1")
    NONE(1),
    @XmlEnumValue("2")
    FULL(2),
    @XmlEnumValue("3")
    LIMITED(3);

    private Integer value;
    private static Map<Integer, UserPassType> selectionByValue = new HashMap<>();

    static {
        selectionByValue.put(1, NONE);
        selectionByValue.put(2, FULL);
        selectionByValue.put(3, LIMITED);
    }

    public static class UserType extends EnumUserType<UserPassType, Integer> {

        @Override
        protected UserPassType getByValue(Integer value) {
            return UserPassType.byValue(value);
        }
    }


    UserPassType(Integer value) {
        this.value = value;
    }

    @JsonCreator
    public static UserPassType byValue(Integer value) {
        return selectionByValue.get(value);
    }

    @JsonValue
    public Integer value() {
        return value;
    }

}
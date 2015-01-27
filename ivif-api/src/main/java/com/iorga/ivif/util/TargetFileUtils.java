package com.iorga.ivif.util;

import org.apache.commons.lang3.StringUtils;

public class TargetFileUtils {

    public static String getVariableNameFromCamelCasedName(String name) {
        String[] parts = StringUtils.splitByCharacterTypeCamelCase(name);
        parts[0] = StringUtils.lowerCase(parts[0]);
        return StringUtils.join(parts);
    }

}

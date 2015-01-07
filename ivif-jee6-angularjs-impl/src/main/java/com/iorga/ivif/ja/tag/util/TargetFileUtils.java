package com.iorga.ivif.ja.tag.util;

import org.apache.commons.lang3.StringUtils;

public class TargetFileUtils {

    public static String getVariableNameFromName(String name) {
        String[] parts = StringUtils.splitByCharacterTypeCamelCase(name);
        parts[0] = StringUtils.lowerCase(parts[0]);
        return StringUtils.join(parts);
    }

}

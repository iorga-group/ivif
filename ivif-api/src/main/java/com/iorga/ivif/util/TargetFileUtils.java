package com.iorga.ivif.util;

import org.apache.commons.lang3.StringUtils;

public class TargetFileUtils {

    public static String getVariableNameFromCamelCasedName(String name) {
        String[] parts = StringUtils.splitByCharacterTypeCamelCase(name);
        parts[0] = StringUtils.lowerCase(parts[0]);
        return StringUtils.join(parts);
    }

    public static String getTitleFromCamelCasedName(String name) {
        String[] tempTitleCrumbs = StringUtils.splitByCharacterTypeCamelCase(name);
        tempTitleCrumbs[0] = StringUtils.capitalize(tempTitleCrumbs[0]);
        return StringUtils.join(tempTitleCrumbs, ' ');
    }

}

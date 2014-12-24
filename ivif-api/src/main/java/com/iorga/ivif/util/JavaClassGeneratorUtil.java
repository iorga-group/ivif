package com.iorga.ivif.util;

import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Set;

public class JavaClassGeneratorUtil {
    private Set<String> imports = Sets.newHashSet();
    private Set<String> importedSimpleNames = Sets.newHashSet();
    private Map<String, String> importsBySimpleName = Maps.newHashMap();

    public String useClass(String fullyQualifiedJavaClassName) {
        String simpleName = getSimpleName(fullyQualifiedJavaClassName);
        if (imports.contains(fullyQualifiedJavaClassName)) {
            // the class is already imported, can use its simple name
            return simpleName;
        } else {
            if (importedSimpleNames.contains(simpleName)) {
                // that class is not imported, but a simple name already exists for that type, let's return its fully qualified name
                return fullyQualifiedJavaClassName;
            } else {
                // import the class
                importClass(fullyQualifiedJavaClassName, simpleName);
                return simpleName;
            }
        }
    }

    private String getSimpleName(String fullyQualifiedJavaClassName) {
        return StringUtils.substringAfterLast(fullyQualifiedJavaClassName, ".");
    }

    protected void importClass(String fullyQualifiedJavaClassName, String simpleName) {
        imports.add(fullyQualifiedJavaClassName);
        importedSimpleNames.add(simpleName);
    }

    public Iterable<String> getOrderedImports() {
        return Ordering.natural().immutableSortedCopy(imports);
    }
}

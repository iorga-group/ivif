package com.iorga.ivif.util;

import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class JavaClassGeneratorUtil {
    private Set<String> imports = Sets.newHashSet();
    private Set<String> importedSimpleNames = Sets.newHashSet();
    private Map<String, String> importsBySimpleName = Maps.newHashMap();

    private Map<String, String> injectionsByClassName = Maps.newLinkedHashMap();

    public static interface Injection {
        public String getClassName();
        public String getVariableName();
    }

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

    public String useInject(String fullyQualifiedJavaClassName) {
        String injection = injectionsByClassName.get(fullyQualifiedJavaClassName);
        if (injection == null) {
            // new injection
            String injectionVariableName = TargetFileUtils.getVariableNameFromCamelCasedName(getSimpleName(fullyQualifiedJavaClassName));
            injection = injectionVariableName;
            for (int i = 2 ; injectionsByClassName.values().contains(injection) ; i++) { // TODO optimize this with O(1) contains check
                injection = injectionVariableName + i;
            }
            injectionsByClassName.put(fullyQualifiedJavaClassName, injection);
        }
        return injection;
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

    public Iterable<Injection> getInjections() {
        return new Iterable<Injection>() {
            @Override
            public Iterator<Injection> iterator() {
                return new Iterator<Injection>() {
                    final Iterator<Entry<String, String>> iterator = injectionsByClassName.entrySet().iterator();

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public Injection next() {
                        final Entry<String, String> entry = iterator.next();
                        return new Injection() {
                            @Override
                            public String getClassName() {
                                return entry.getKey();
                            }

                            @Override
                            public String getVariableName() {
                                return entry.getValue();
                            }
                        };
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                };
            }
        };
    }
}

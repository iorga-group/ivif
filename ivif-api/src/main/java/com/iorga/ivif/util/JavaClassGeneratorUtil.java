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
    private final String currentPackage;
    private final String currentClassName;

    private Set<String> imports = Sets.newHashSet();

    private Set<String> usedClassNames = Sets.newHashSet();
    private Set<String> usedClassSimpleNames = Sets.newHashSet();

    private Map<String, String> injectionsByClassName = Maps.newLinkedHashMap();

    public static interface Injection {
        public String getClassName();
        public String getVariableName();
    }


    public JavaClassGeneratorUtil(String currentClassName) {
        this.currentClassName = currentClassName;
        this.currentPackage = StringUtils.substringBeforeLast(currentClassName, ".");
    }


    public String useClass(String fullyQualifiedJavaClassName, boolean inCurrentClass) {
        String simpleName = getSimpleName(fullyQualifiedJavaClassName);
        if (usedClassNames.contains(fullyQualifiedJavaClassName)) {
            // the class is already imported, can use its simple name
            return simpleName;
        } else {
            if (usedClassSimpleNames.contains(simpleName)) {
                // that class is not imported, but a simple name already exists for that type, let's return its fully qualified name
                return fullyQualifiedJavaClassName;
            } else {
                // import the class
                importClass(fullyQualifiedJavaClassName, simpleName, inCurrentClass);
                return simpleName;
            }
        }
    }

    public String useClass(String fullyQualifiedJavaClassName) {
        return useClass(fullyQualifiedJavaClassName, true);
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

    protected void importClass(String fullyQualifiedJavaClassName, String simpleName, boolean inCurrentClass) {
        usedClassNames.add(fullyQualifiedJavaClassName);
        usedClassSimpleNames.add(simpleName);

        final String ownerElement = StringUtils.substringBeforeLast(fullyQualifiedJavaClassName, ".");
        if (currentPackage.equals(ownerElement) || (inCurrentClass && currentClassName.equals(ownerElement))) {
            // no need to add this import, it's implicit
        } else {
            imports.add(fullyQualifiedJavaClassName);
        }
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

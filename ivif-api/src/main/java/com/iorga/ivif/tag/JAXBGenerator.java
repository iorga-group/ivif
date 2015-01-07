package com.iorga.ivif.tag;

import com.google.common.collect.Maps;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public abstract class JAXBGenerator<C extends GeneratorContext<C>> extends Generator<C> {

    protected Map<String, Class<? extends JAXBSourceTagHandler<?, C>>> registeredSourceTagHandlerClassByTagName = Maps.newHashMap();

    protected <T> void registerSourceTagHandlerClassForTagName(String tagName, Class<? extends JAXBSourceTagHandler<T, C>> sourceTagHandlerClass) {
        registeredSourceTagHandlerClassByTagName.put(tagName, sourceTagHandlerClass);
    }

    @Override
    protected SourceTagHandler<C> createSourceTagHandler(QName name, C context) throws JAXBException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Class sourceTagHandlerClass = registeredSourceTagHandlerClassByTagName.get(name.getLocalPart());
        if (sourceTagHandlerClass != null) {
            return createSourceTagHandler(sourceTagHandlerClass);
        } else {
            return null;
        }
    }

    protected <T> SourceTagHandler<C> createSourceTagHandler(Class<? extends JAXBSourceTagHandler<T, C>> sourceTagHandlerClass) throws JAXBException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return (SourceTagHandler<C>) sourceTagHandlerClass.newInstance();
    }
}

package com.iorga.ivif.tag;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Map;

public class JAXBSourceFileHandlerFactory<C extends GeneratorContext<C>> {
    private final static Logger LOG = LoggerFactory.getLogger(JAXBSourceFileHandlerFactory.class);

    protected Map<String, Class<? extends JAXBSourceTagHandler<?, C>>> registeredSourceTagHandlerClassByTagName = Maps.newHashMap();

    protected final XMLInputFactory xmlInputFactory;

    public JAXBSourceFileHandlerFactory() {
        xmlInputFactory = XMLInputFactory.newInstance();
    }

    public <T> void registerSourceTagHandlerClassForTagName(String tagName, Class<? extends JAXBSourceTagHandler<T, C>> sourceTagHandlerClass) {
        registeredSourceTagHandlerClassByTagName.put(tagName, sourceTagHandlerClass);
    }

    public <T, S extends JAXBSourceTagHandler<T, C>> void registerSourceTagHandlerClass(Class<S> sourceTagHandlerClass) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        registerSourceTagHandlerClass(sourceTagHandlerClass, null);
    }

    public <T, S extends JAXBSourceTagHandler<T, C>> void registerSourceTagHandlerClass(Class<S> sourceTagHandlerClass, Object objectFactory) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        TypeToken<S> typeToken = TypeToken.of(sourceTagHandlerClass);
        Class<?> jaxbGeneratedClass = typeToken.resolveType(JAXBSourceTagHandler.class.getTypeParameters()[0]).getRawType();
        XmlRootElement xmlRootElement = jaxbGeneratedClass.getAnnotation(XmlRootElement.class);
        if (xmlRootElement != null) {
            registerSourceTagHandlerClassForTagName(xmlRootElement.name(), sourceTagHandlerClass);
        } else {
            String simpleName = jaxbGeneratedClass.getSimpleName();
            Class<?> objectFactoryClass = objectFactory.getClass();
            String createMethodName = "create" + simpleName;
            T value = (T) objectFactoryClass.getMethod(createMethodName).invoke(objectFactory);
            JAXBElement<T> jaxbElement = (JAXBElement<T>) objectFactoryClass.getMethod(createMethodName, value.getClass()).invoke(objectFactory, value);
            registerSourceTagHandlerClassForTagName(jaxbElement.getName().getLocalPart(), sourceTagHandlerClass);
        }
    }


    public JAXBSourceFileHandler<C> createSourceFileHandler(Path path) {
        return new JAXBSourceFileHandler<>(path, this);
    }

    public SourceTagHandler<C> createSourceTagHandler(QName name, C context) throws JAXBException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Class sourceTagHandlerClass = registeredSourceTagHandlerClassByTagName.get(name.getLocalPart());
        if (sourceTagHandlerClass != null) {
            return createSourceTagHandler(sourceTagHandlerClass);
        } else {
            return null;
        }
    }

    public <T> SourceTagHandler<C> createSourceTagHandler(Class<? extends JAXBSourceTagHandler<T, C>> sourceTagHandlerClass) throws JAXBException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return sourceTagHandlerClass.newInstance();
    }
}

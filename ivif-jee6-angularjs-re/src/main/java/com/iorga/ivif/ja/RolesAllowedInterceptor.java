package com.iorga.ivif.ja;

import org.jboss.resteasy.spi.UnauthorizedException;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;

@Interceptor @RolesAllowed
public class RolesAllowedInterceptor implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject @CurrentRoles
    private Collection<String> currentRoles;

    @AroundInvoke
    public Object interceptRolesAllowed(InvocationContext context) throws Exception {
        final Class<?> targetClass = context.getTarget().getClass();
        final Method method = context.getMethod();

        // Build roles allowed
        Set<String> rolesAllowed = new HashSet<>();

        addRolesAllowed(method, rolesAllowed);
        recursiveAddClassAndInterfaceRolesAllowed(targetClass, method, rolesAllowed, new HashSet<Class<?>>());

        // Now we have all allowed roles, we must check if we have at least one common role
        rolesAllowed.retainAll(currentRoles);

        if (rolesAllowed.isEmpty()) {
            throw new UnauthorizedException();
        } else {
            return context.proceed();
        }
    }

    private void recursiveAddClassAndInterfaceRolesAllowed(Class<?> klass, Method method, Set<String> rolesAllowed, HashSet<Class<?>> alreadyInspectedInterfaces) {
        addRolesAllowed(klass, rolesAllowed);
        recursiveAddInterfaceRolesAllowed(klass, method, rolesAllowed, alreadyInspectedInterfaces);

        final Class<?> superclass = klass.getSuperclass();
        if (superclass != null) {
            recursiveAddClassAndInterfaceRolesAllowed(superclass, method, rolesAllowed, new HashSet<Class<?>>());
        }
    }

    private void recursiveAddInterfaceRolesAllowed(Class<?> klass, Method method, Set<String> rolesAllowed, HashSet<Class<?>> alreadyInspectedInterfaces) {
        final Class<?>[] interfaces = klass.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            if (!alreadyInspectedInterfaces.contains(anInterface)) {
                // not inspected interface, let's inspect it
                alreadyInspectedInterfaces.add(anInterface);
                // First check if it declares the method
                try {
                    final Method anInterfaceMethod = anInterface.getMethod(method.getName(), method.getParameterTypes());
                    addRolesAllowed(anInterfaceMethod, rolesAllowed);
                } catch (NoSuchMethodException e) {
                    // Do nothing
                }
                // Then add the roles for the interface
                addRolesAllowed(anInterface, rolesAllowed);
                // And recurse add its own extending interfaces
                recursiveAddClassAndInterfaceRolesAllowed(anInterface, method, rolesAllowed, alreadyInspectedInterfaces);
            }
        }
    }

    private void addRolesAllowed(AnnotatedElement annotatedElement, Set<String> rolesAllowed) {
        final RolesAllowed annotation = annotatedElement.getAnnotation(RolesAllowed.class);
        if (annotation != null) {
            Collections.addAll(rolesAllowed, annotation.value());
        }
    }
}

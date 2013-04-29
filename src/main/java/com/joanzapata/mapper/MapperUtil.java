package com.joanzapata.mapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

final class MapperUtil {

    public static final Collection createCollectionLike(Collection source) {
        return new ArrayList();
    }

    public static final Map createMapLike(Map source) {
        return new HashMap();
    }

    /**
     * Find a getter on the source object for the given setter name.
     * @param source The source object.
     * @param setter The setter method.
     * @return the corresponding getter method given the setter method, or null if nothing found.
     */
    public static Method findGetter(Object source, Method setter) {

        // A setter must have 1 parameter
        if (setter.getParameterTypes().length != 1) {
            return null;
        }

        // The name of the setter should be "set..."
        if (setter.getName().length() <= 3 || !setter.getName().startsWith("set")) {
            return null;
        }

        String getterMethodName = "get" + setter.getName().substring(3);
        String getterMethodNameForBooleans = "is" + setter.getName().substring(3);

        Class loopClass = source.getClass();
        while (loopClass != Object.class) {
            for (Method method : loopClass.getMethods()) {
                // If it's the exact name, return the getter
                // getBookBO() should be valid for setBookDTO() and vice versa, so...
                if (method.getParameterTypes().length == 0 && (getterMethodName.equals(method.getName()) ||
                        getterMethodNameForBooleans.equals(method.getName()) ||
                        getterMethodName.startsWith(method.getName()) ||
                        method.getName().startsWith(getterMethodName) ||
                        getterMethodNameForBooleans.startsWith(method.getName()) ||
                        method.getName().startsWith(getterMethodNameForBooleans))) {
                    return method;
                }
            }
            loopClass = loopClass.getSuperclass();
        }
        return null;
    }

    public static <D> D mapNativeTypeOrNull(Object source, Class<D> destinationClass) {
        if (source instanceof Byte ||
                source instanceof Short ||
                source instanceof Integer ||
                source instanceof Long ||
                source instanceof Float ||
                source instanceof Double ||
                source instanceof Boolean ||
                source instanceof String ||
                source instanceof Character) {
            return (D) source;
        }
        return null;
    }

    /**
     * Try to find a user defined mapping for the source class that is more precise than
     * the destination type retrieved from target object.
     */
    public static <D> Class<D> findBestDestinationType(
            Class<?> sourceClass, Class<D> destinationClass, MappingContext context) {
        Class explicitMapping = context.getMapping(sourceClass);
        return (explicitMapping == null ||
                explicitMapping.isAssignableFrom(destinationClass)) ?
                destinationClass : explicitMapping;
    }
}

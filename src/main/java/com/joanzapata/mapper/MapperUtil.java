/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.joanzapata.mapper;

import java.lang.reflect.Method;
import java.util.*;

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
    public static Method findGetter(Object source, Method setter, List<String> knownSuffixes) {
        // A setter must have 1 parameter
        if (setter.getParameterTypes().length != 1) {
            return null;
        }

        // The name of the setter should be "set..."
        if (setter.getName().length() <= 3 || !setter.getName().startsWith("set")) {
            return null;
        }

        String expectedGetterName = "get" + setter.getName().substring(3);
        String expectedGetterNameForBooleans = "is" + setter.getName().substring(3);

        Class loopClass = source.getClass();
        while (loopClass != Object.class) {
            for (Method method : loopClass.getMethods()) {
                if (method.getParameterTypes().length != 0) continue;

                String methodName = method.getName();
                if (knownSuffixes != null) {
                    expectedGetterName = removeSuffix(expectedGetterName, knownSuffixes);
                    methodName = removeSuffix(methodName, knownSuffixes);
                }

                if (expectedGetterName.equals(methodName) ||
                        expectedGetterNameForBooleans.equals(methodName)) {
                    return method;
                }
            }
            loopClass = loopClass.getSuperclass();
        }
        return null;
    }

    public static String removeSuffix(String expectedGetterName, List<String> knownSuffixes) {
        for (String suffix : knownSuffixes) {
            if (expectedGetterName.endsWith(suffix)) {
                return expectedGetterName.substring(0,
                        expectedGetterName.length() - suffix.length());
            }
        }
        return expectedGetterName;
    }

    public static <D> D mapPrimitiveTypeOrNull(Object source) {
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

    public static <D> boolean isCompatiblePrimitiveType(D destinationObject, Class<D> expectedClass) {
        Class expectedClassAutoboxed = autoBox(expectedClass);
        return expectedClassAutoboxed.isAssignableFrom(destinationObject.getClass());
    }

    private static Class<?> autoBox(Class<?> destinationClass) {
        if (destinationClass == byte.class) return Byte.class;
        if (destinationClass == short.class) return Short.class;
        if (destinationClass == int.class) return Integer.class;
        if (destinationClass == long.class) return Long.class;
        if (destinationClass == float.class) return Float.class;
        if (destinationClass == double.class) return Double.class;
        if (destinationClass == boolean.class) return Boolean.class;
        if (destinationClass == char.class) return Character.class;
        return destinationClass;
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

    /**
     * Find all accessible methods in the given class and its superclass(es)
     * that start with "set".
     * @param ofClass The class to retrieve the methods from.
     * @return A list of methods, an empty list if no method found.
     */
    public static List<Method> findAllSetterMethods(Class<?> ofClass) {
        List<Method> methods = new ArrayList<Method>();
        Class currentClass = ofClass;
        while (currentClass != Object.class) {
            for (Method method : currentClass.getMethods())
                if (method.getName().startsWith("set"))
                    methods.add(method);
            currentClass = currentClass.getSuperclass();
        }
        return methods;
    }

    public static void applyHooks(List<HookWrapper> hooks, Object source, Object destination) {
        for (HookWrapper hook : hooks) {
            hook.apply(source, destination);
        }
    }
}

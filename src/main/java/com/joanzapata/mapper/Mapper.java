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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.joanzapata.mapper.MapperUtil.*;
import static java.util.Arrays.asList;

public final class Mapper {

    private final Map<Class, Class> mappings;

    private final List<String> knownSuffixes = asList("DTO", "BO");

    private final List<HookWrapper> hooks;

    private boolean strictMode = false;

    public Mapper() {
        mappings = new HashMap<Class, Class>();
        hooks = new ArrayList<HookWrapper>();
    }

    /**
     * If set to true and not getter is found for a property,
     * or if types mismatch between getter and setter,
     * a StrictModeException will be thrown. <b>Default is false.</b>
     */
    public Mapper strictMode(boolean strictMode) {
        this.strictMode = strictMode;
        return this;
    }

    /**
     * Adds an explicit mapping from a source class to a destination class.
     * You shouldn't need this unless you're using inheritance.
     * @param sourceClass      The source class.
     * @param destinationClass The destination class.
     * @return The current mapper for chaining.
     */
    public Mapper mapping(Class<?> sourceClass, Class<?> destinationClass) {
        mappings.put(sourceClass, destinationClass);
        return this;
    }

    /** Same as {@link #mapping(Class, Class)} but adds the mapping in both directions. */
    public Mapper biMapping(Class<?> sourceClass, Class<?> destinationClass) {
        mapping(sourceClass, destinationClass);
        mapping(destinationClass, sourceClass);
        return this;
    }

    /**
     * Add a hook to the mapping process. This hook will be called after the complete mapping of the object.
     * @param hook The hook object.
     * @return The current mapper for chaining.
     */
    public <S, D> Mapper hook(Hook<S, D> hook) {
        hooks.add(new HookWrapper(hook));
        return this;
    }

    /**
     * Map the source object with the destination class using the getters/setters.
     * @param source           The source object.
     * @param destinationClass The destination class.
     * @return A destination instance filled using its setters and the source getters.
     */
    public <D> D map(Object source, Class<D> destinationClass) {
        if (source instanceof Iterable)
            return (D) map((Iterable) source, destinationClass);
        MappingContext context = new MappingContext(mappings);
        return nominalMap(source, destinationClass, context);
    }

    /** Same as {@link #map(Object, Class)}, but applies to iterables objects. */
    public <D, U> List<D> map(Iterable<U> source, Class<D> destinationClass) {
        return mapIterable(source, destinationClass, new MappingContext(mappings));
    }

    /** Same as {@link #map(Object, Class)}, but applies to map objects. */
    public <KS, VS, KD, VD> Map<KD, VD> map(Map<KS, VS> source, Class<KD> destinationKeyClass, Class<VD> destinationValueClass) {
        return mapMap(source, destinationKeyClass, destinationValueClass, new MappingContext(mappings));
    }

    private <D, U> List<D> mapIterable(Iterable<U> source, Class<D> destinationClass, MappingContext context) {
        if (source == null) return null;
        List<D> out = new ArrayList<D>();
        for (Object s : source) {
            out.add(nominalMap(s, destinationClass, context));
        }
        return out;
    }

    private <KS, VS, KD, VD> Map<KD, VD> mapMap(Map<KS, VS> source, Class<KD> keyClass, Class<VD> valueClass, MappingContext context) {
        if (source == null) return null;
        Map<KD, VD> out = new HashMap<KD, VD>();
        for (Map.Entry<KS, VS> s : source.entrySet()) {
            KD mappedKey = nominalMap(s.getKey(), keyClass, context);
            VD mappedValue = nominalMap(s.getValue(), valueClass, context);
            out.put(mappedKey, mappedValue);
        }
        return out;
    }

    private <D> D nominalMap(Object source, Class<D> destinationClass, MappingContext context) {
        // This is the entry point of the nominal mapping process.
        // Special cases directly provided by the user (lists, etc...) must have been processed before.
        return nominalMap(source, null, destinationClass, context);
    }

    /**
     * @param source           The source object.
     * @param field            Generic type of the target field
     * @param destinationClass The destination class.
     * @param context          The mapping context.
     * @return The mapped source object.
     */
    private <D> D nominalMap(Object source, Type field, Class<D> destinationClass, MappingContext context) {
        if (source == null) return null;

        if (source instanceof Iterable) {
            ParameterizedType type = (ParameterizedType) field;
            return (D) mapIterable((Iterable) source, (Class) type.getActualTypeArguments()[0], context);
        }

        if (source instanceof Map) {
            ParameterizedType type = (ParameterizedType) field;
            return (D) mapMap((Map) source, (Class) type.getActualTypeArguments()[0],
                    (Class) type.getActualTypeArguments()[1], context);
        }

        // First, use already existing if possible (prevents cyclic mapping)
        D alreadyMapped = context.getAlreadyMapped(source);
        if (alreadyMapped != null) {
            return alreadyMapped;
        }

        // Map native types if possible
        D nativeMapped = mapPrimitiveTypeOrNull(source);
        if (nativeMapped != null) {
            if (isCompatiblePrimitiveType(nativeMapped, destinationClass)) {
                applyHooks(hooks, source, destinationClass);
                return nativeMapped;
            } else {
                if (strictMode) {
                    throw new StrictModeException("Unable to map "
                            + nativeMapped.getClass().getCanonicalName()
                            + " -> " + destinationClass.getCanonicalName());
                }
            }
        }

        // Otherwise, create appropriate instance and store it in context
        Class<D> bestDestinationClass = findBestDestinationType(source.getClass(), destinationClass, context);
        D destinationInstance = context.createInstanceForDestination(bestDestinationClass);
        context.putAlreadyMapped(source, destinationInstance);

        for (Method setterMethod : findAllSetterMethods(bestDestinationClass)) {

            Method getterMethod = findGetter(source, setterMethod, knownSuffixes);

            if (getterMethod == null) {
                if (strictMode) {
                    throw new StrictModeException("No suitable getter for "
                            + setterMethod.getName() + "() method in "
                            + source.getClass().getCanonicalName());
                } else continue;
            }

            try {

                Object objectBeingTransferred = getterMethod.invoke(source);

                if (objectBeingTransferred == null) {
                    continue;
                }

                // NOTE This is a recursive call, but the stack is unlikely to explode
                // because the cyclic dependencies are managed, and the depth of a model
                // isn't supposed to get that high.
                Object mappedObjectBeingTransferred = nominalMap(objectBeingTransferred,
                        setterMethod.getGenericParameterTypes()[0],
                        setterMethod.getParameterTypes()[0],
                        context);

                // Apply setter
                setterMethod.invoke(destinationInstance, mappedObjectBeingTransferred);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        applyHooks(hooks, source, destinationInstance);
        return destinationInstance;
    }

}

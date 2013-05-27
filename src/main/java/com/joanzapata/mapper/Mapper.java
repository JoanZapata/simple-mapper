package com.joanzapata.mapper;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public final class Mapper {

    private final Map<Class, Class> mappings;

    private final List<String> knownSuffixes = Arrays.asList("DTO", "BO");

    private final List<HookWrapper> hooks;

    private boolean throwExceptionIfPropertyNotFoundInSource = false;

    public Mapper() {
        mappings = new HashMap<Class, Class>();
        hooks = new ArrayList<HookWrapper>();
    }

    /**
     * If set to true and not getter is found for a property,
     * a PropertyNotFoundException will be thrown. <b>Default is false.</b>
     */
    public Mapper setThrowExceptionIfPropertyNotFoundInSource(boolean throwExceptionIfPropertyNotFoundInSource) {
        this.throwExceptionIfPropertyNotFoundInSource = throwExceptionIfPropertyNotFoundInSource;
        return this;
    }

    /**
     * Adds an explicit mapping from a source class to a destination class.
     * You shouldn't need this unless you're using inheritance.
     * @param sourceClass      The source class.
     * @param destinationClass The destination class.
     * @return The current mapper for chaining.
     */
    public Mapper addMapping(Class<?> sourceClass, Class<?> destinationClass) {
        mappings.put(sourceClass, destinationClass);
        return this;
    }

    /** Same as {@link #addMapping(Class, Class)} but adds the mapping in both directions. */
    public Mapper addBidirectionalMapping(Class<?> sourceClass, Class<?> destinationClass) {
        addMapping(sourceClass, destinationClass);
        addMapping(destinationClass, sourceClass);
        return this;
    }

    /**
     * Add a hook to the mapping process. This hook will be called after the complete mapping of the object.
     * @param hook The hook object.
     * @return The current mapper for chaining.
     */
    public <S, D> Mapper addHook(Hook<S, D> hook) {
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
        return map(source, destinationClass, context);
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
        List<D> out = new ArrayList<D>();
        for (Object s : source) {
            out.add(map(s, destinationClass, context));
        }
        return out;
    }

    private <KS, VS, KD, VD> Map<KD, VD> mapMap(Map<KS, VS> source, Class<KD> keyClass, Class<VD> valueClass, MappingContext context) {
        Map<KD, VD> out = new HashMap<KD, VD>();
        for (Map.Entry<KS, VS> s : source.entrySet()) {
            KD mappedKey = map(s.getKey(), keyClass, context);
            VD mappedValue = map(s.getValue(), valueClass, context);
            out.put(mappedKey, mappedValue);
        }
        return out;
    }

    private <D> D map(Object source, Class<D> destinationClass, MappingContext context) {
        return map(source, null, destinationClass, context);
    }

    /**
     * @param source           The source object.
     * @param field            Generic type for generic fields.
     * @param destinationClass The destination class.
     * @param context          The mapping context.
     * @return The mapped source object.
     */
    private <D> D map(Object source, Type field, Class<D> destinationClass, MappingContext context) {
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
        D nativeMapped = MapperUtil.mapNativeTypeOrNull(source, destinationClass);
        if (nativeMapped != null) {
            return nativeMapped;
        }

        // Otherwise, find appropriate instance
        Class<D> bestDestinationClass = MapperUtil.findBestDestinationType(source.getClass(), destinationClass, context);
        D destinationInstance = context.createInstanceForDestination(bestDestinationClass);
        context.putAlreadyMapped(source, destinationInstance);

        // Then fill it (loop to get all methods, including superclass' ones)
        Class currentClass = bestDestinationClass;
        while (currentClass != Object.class) {
            for (Method setterMethod : currentClass.getMethods()) {
                if (setterMethod.getName().startsWith("set")) {

                    // Find a getter for this setter
                    Method getterMethod = MapperUtil.findGetter(source, setterMethod, knownSuffixes);
                    if (getterMethod == null) {
                        if (throwExceptionIfPropertyNotFoundInSource) {
                            throw new PropertyNotFoundException("Unable to find a getter for " + setterMethod.getName() + " method in " + source.getClass().getCanonicalName());
                        } else continue;
                    }

                    try {

                        // Apply getter
                        Object objectBeingTransferred = getterMethod.invoke(source);

                        if (objectBeingTransferred != null) {
                            // NOTE This is a recursive call, but the stack is unlikely to explode 
                            // because the cyclic dependencies are managed, and the depth of a model 
                            // isn't supposed to get that high.
                            Object mappedObjectBeingTransferred = map(objectBeingTransferred, setterMethod.getGenericParameterTypes()[0], setterMethod.getParameterTypes()[0], context);

                            // Apply setter
                            setterMethod.invoke(destinationInstance, mappedObjectBeingTransferred);
                        }

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            currentClass = currentClass.getSuperclass();
        }

        // Apply hooks if possible
        for (HookWrapper hook : hooks) {
            hook.apply(source, destinationInstance);
        }

        // Then return the constructed object
        return destinationInstance;

    }

}

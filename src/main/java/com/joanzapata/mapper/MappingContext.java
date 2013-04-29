package com.joanzapata.mapper;

import java.util.HashMap;
import java.util.Map;

class MappingContext {

    /** Contains all the already mapped objects */
    private final Map<Object, Object> sourceToDestination;

    private final Map<Class, Class> mappings;

    public MappingContext(Map<Class, Class> mappings) {
        this.mappings = mappings;
        sourceToDestination = new HashMap<Object, Object>();
    }

    public void addMapping(Class source, Class destination) {
        mappings.put(source, destination);
    }

    public Class getMapping(Class source) {
        return mappings.get(source);
    }

    /**
     * Get the destination for an already mapped source.
     * @param source The source object.
     * @return the destination object, or null if not mapped already.
     */
    public <D> D getAlreadyMapped(Object source) {
        return (D) sourceToDestination.get(source);
    }

    public <D> D createInstanceForDestination(Class<D> destinationClass) {
        try {
            return destinationClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to create instance of " + destinationClass.getName());
        }
    }

    /** Store the destination object for a given source */
    public <D> void putAlreadyMapped(Object source, D destination) {
        sourceToDestination.put(source, destination);
    }
}

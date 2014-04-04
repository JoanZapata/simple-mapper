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

import java.util.HashMap;
import java.util.Map;

public class MappingContext {

    /** Contains all the already mapped objects */
    private final Map<Object, Object> sourceToDestination;

    private final Map<Class<?>, Class<?>> mappings;

    MappingContext(Map<Class<?>, Class<?>> mappings) {
        this(null, mappings);
    }

    /** @param mappingContext Optional mapping context to merge with.F */
    MappingContext(MappingContext mappingContext, Map<Class<?>, Class<?>> mappings) {
        this.mappings = mappings;
        sourceToDestination = new HashMap<Object, Object>();
        if (mappingContext != null) {
            this.mappings.putAll(mappingContext.mappings);
            this.sourceToDestination.putAll(mappingContext.sourceToDestination);
        }
    }

    public void addMapping(Class<?> source, Class<?> destination) {
        mappings.put(source, destination);
    }

    Class<?> getMapping(Class<?> source) {
        return mappings.get(source);
    }

    /**
     * Get the destination for an already mapped source.
     * @param source The source object.
     * @return the destination object, or null if not mapped already.
     */
    @SuppressWarnings("unchecked")
	public <D> D getAlreadyMapped(Object source) {
        return (D) sourceToDestination.get(source);
    }

    public <D> D createInstanceForDestination(Class<D> destinationClass) {
        try {
            return destinationClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to create instance of " + destinationClass.getName() + ", please check there is a public no-arg constructor.");
        }
    }

    /** Store the destination object for a given source */
    public <D> void putAlreadyMapped(Object source, D destination) {
        sourceToDestination.put(source, destination);
    }
}

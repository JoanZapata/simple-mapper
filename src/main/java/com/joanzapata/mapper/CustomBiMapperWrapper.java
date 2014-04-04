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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CustomBiMapperWrapper<S, D> {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomBiMapperWrapper.class);

    private final CustomBiMapper<S, D> customBiMapper;

    CustomBiMapperWrapper(CustomBiMapper<S, D> customBiMapper) {
        this.customBiMapper = customBiMapper;
    }

    public boolean isApplicableForward(Object source, Class<?> destination) {
        return isApplicable(source, destination, "mapForward");
    }

    @SuppressWarnings("unchecked")
    public D applyForward(Object source, Class<?> destination, MappingContext mappingContext) {
        if (isApplicableForward(source, destination)) {
            return applySafeForward((S) source, mappingContext);
        } else return null;
    }

    private D applySafeForward(S source, MappingContext mappingContext) {
        return customBiMapper.mapForward(source, mappingContext);
    }
    
    public boolean isApplicableBackward(Object source, Class<?> destination) {
        return isApplicable(source, destination, "mapBackward");
    }
    
    @SuppressWarnings("unchecked")
    public S applyBackward(Object source, Class<?> destination, MappingContext mappingContext) {
        if (isApplicableBackward(source, destination)) {
            return applySafeBackward((D) source, mappingContext);
        } else return null;
    }

    private S applySafeBackward(D source, MappingContext mappingContext) {
        return customBiMapper.mapBackward(source, mappingContext);
    }
    
    private boolean isApplicable(Object source, Class<?> destination, String methodName) {
        final Method[] methods = customBiMapper.getClass().getMethods();

        for (Method method : methods) {
            if (methodName.equals(method.getName())) {
                Class<?> parameterType = method.getParameterTypes()[0];
                Class<?> returnType = method.getReturnType();
                
                logger.debug("source: {}, destination: {}", source.getClass().getSimpleName(), destination.getSimpleName());
                logger.debug("param: {}, return: {}", parameterType.getSimpleName(), returnType.getSimpleName());
                
                // Ignore Object because it's too large
                if (parameterType != Object.class && returnType != Object.class &&
                        // Parameter type of the user function will be assigned the source object
                        parameterType.isAssignableFrom(source.getClass()) &&
                        // The destination object will be assigned the object returned from user function
                        destination.isAssignableFrom(returnType)) {
                    return true;
                }
            }
        }
        return false;
    }

}

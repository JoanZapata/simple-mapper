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

class CustomMapperWrapper<S, D> {

    private final CustomMapper<S, D> customMapper;

    CustomMapperWrapper(CustomMapper<S, D> customMapper) {
        this.customMapper = customMapper;
    }

    public boolean isApplicable(Object source, Class destination) {
        final Method[] methods = customMapper.getClass().getMethods();

        for (Method method : methods) {
            if ("map".equals(method.getName())) {
                Class<?> parameterType = method.getParameterTypes()[0];
                Class<?> returnType = method.getReturnType();
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

    public D apply(S source, Class<D> destination, MappingContext mappingContext) {
        if (isApplicable(source, destination)) {
            return applySafe((S) source, mappingContext);
        } else return null;
    }

    private D applySafe(S source, MappingContext mappingContext) {
        return customMapper.map(source, mappingContext);
    }
}

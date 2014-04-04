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

class HookWrapper<S, D> {

    private Hook<S, D> hook;

    HookWrapper(Hook<S, D> hook) {
        this.hook = hook;
    }

    @SuppressWarnings("unchecked")
	public void apply(Object source, Object destination) {
        for (Method method : hook.getClass().getMethods()) {
            if ("extraMapping".equals(method.getName())) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                Class<?> sourceClass = parameterTypes[0];
                Class<?> destinationClass = parameterTypes[1];
                if (sourceClass.isAssignableFrom(source.getClass()) &&
                        destinationClass.isAssignableFrom(destination.getClass())) {
                    applySafe((S) source, (D) destination);
                }
                return;
            }
        }
    }

    private void applySafe(S source, D destination) {
        hook.extraMapping(source, destination);
    }
}

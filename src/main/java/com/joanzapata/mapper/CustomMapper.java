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

/**
 * Define a CustomMapper when you want
 * to map parts of your model by yourself,
 * or using another mapper. If you call map()
 * on another mapper in this method don't forget
 * to give it the context as an argument.
 * @param <S> Source type
 * @param <D> Destination type
 */
public interface CustomMapper<S, D> {

    /**
     * Map the given source into the destination.
     * @param source  The source object.
     * @param context The current mapping context.
     * @return The destination object you've created using the source object.
     */
    D map(S source, MappingContext context);

}

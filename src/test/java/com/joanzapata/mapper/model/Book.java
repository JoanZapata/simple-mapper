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
package com.joanzapata.mapper.model;

import java.util.List;
import java.util.Map;

public class Book {

    private Long id;

    private String name;

    private List<BookEntry> entries;

    private Map<Long, BookEntry> entriesById;

    public Book() { }

    public Book(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public List<BookEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<BookEntry> entries) {
        this.entries = entries;
    }

    public Map<Long, BookEntry> getEntriesById() {
        return entriesById;
    }

    public void setEntriesById(Map<Long, BookEntry> entriesById) {
        this.entriesById = entriesById;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

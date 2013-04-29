package com.joanzapata.mapper.model;

import java.util.List;

public class Book {

    private Long id;

    private String name;

    private List<BookEntry> entries;

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

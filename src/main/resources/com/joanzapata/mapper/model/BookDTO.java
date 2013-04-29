package com.joanzapata.mapper.model;

import java.util.List;

public class BookDTO {

    private Long id;

    private String name;

    private List<BookEntryDTO> entries;

    public List<BookEntryDTO> getEntries() {
        return entries;
    }

    public void setEntries(List<BookEntryDTO> entries) {
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



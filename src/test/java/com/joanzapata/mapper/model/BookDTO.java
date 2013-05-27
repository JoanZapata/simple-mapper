package com.joanzapata.mapper.model;

import java.util.List;
import java.util.Map;

public class BookDTO {

    private Long id;

    private String name;

    private List<BookEntryDTO> entries;

    private Map<Long, BookEntryDTO> entriesById;

    public List<BookEntryDTO> getEntries() {
        return entries;
    }

    public Map<Long, BookEntryDTO> getEntriesById() {
        return entriesById;
    }

    public void setEntriesById(Map<Long, BookEntryDTO> entriesById) {
        this.entriesById = entriesById;
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



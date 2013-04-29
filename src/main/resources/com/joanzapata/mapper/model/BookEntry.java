package com.joanzapata.mapper.model;

public class BookEntry {

    private long id;

    private Book book;

    public BookEntry(long id, Book book) {
        this.id = id;
        this.book = book;
    }

    public BookEntry() {
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

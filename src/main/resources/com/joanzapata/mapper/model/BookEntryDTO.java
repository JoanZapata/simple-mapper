package com.joanzapata.mapper.model;

public class BookEntryDTO {

    private BookDTO bookDTO;

    private long id;

    public BookDTO getBookDTO() {
        return bookDTO;
    }

    public void setBookDTO(BookDTO bookDTO) {
        this.bookDTO = bookDTO;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

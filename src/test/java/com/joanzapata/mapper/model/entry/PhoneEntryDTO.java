package com.joanzapata.mapper.model.entry;

import com.joanzapata.mapper.model.BookEntryDTO;

public class PhoneEntryDTO extends BookEntryDTO {

    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

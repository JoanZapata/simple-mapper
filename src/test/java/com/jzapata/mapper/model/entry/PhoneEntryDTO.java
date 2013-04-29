package com.jzapata.mapper.model.entry;

import com.jzapata.mapper.model.BookEntryDTO;

public class PhoneEntryDTO extends BookEntryDTO {

    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

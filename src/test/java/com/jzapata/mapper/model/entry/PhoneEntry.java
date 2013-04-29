package com.jzapata.mapper.model.entry;

import com.jzapata.mapper.model.BookEntry;

public class PhoneEntry extends BookEntry {

    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

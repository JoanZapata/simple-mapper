package com.joanzapata.mapper.model.entry;

import com.joanzapata.mapper.model.BookEntry;

public class AddressEntry extends BookEntry {

    private String country, city;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}

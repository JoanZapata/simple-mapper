package com.joanzapata.mapper.model.entry;

import com.joanzapata.mapper.model.BookEntryDTO;

public class AddressEntryDTO extends BookEntryDTO {

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

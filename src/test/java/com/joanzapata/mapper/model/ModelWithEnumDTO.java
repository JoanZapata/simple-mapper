package com.joanzapata.mapper.model;

import java.util.List;

public class ModelWithEnumDTO {

    private List<MyEnumDTO> myEnumsDTO;

    public List<MyEnumDTO> getMyEnumsDTO() {
        return myEnumsDTO;
    }

    public void setMyEnumsDTO(List<MyEnumDTO> myEnumsDTO) {
        this.myEnumsDTO = myEnumsDTO;
    }

    public static enum MyEnumDTO {
        A, B
    }
}

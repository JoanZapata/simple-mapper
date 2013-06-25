package com.joanzapata.mapper.model;

import java.util.Arrays;
import java.util.List;

public class ModelWithEnum {

    private List<MyEnum> myEnums = Arrays.asList(MyEnum.A, MyEnum.B);

    public List<MyEnum> getMyEnums() {
        return myEnums;
    }

    public void setMyEnums(List<MyEnum> myEnums) {
        this.myEnums = myEnums;
    }

    public static enum MyEnum {
        A, B
    }
}

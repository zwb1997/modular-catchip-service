package com.zzz.service.module.params;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TestParams {

    @JsonProperty("lastName")
    private String lastName;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "TestParams{" +
                "lastName='" + lastName + '\'' +
                '}';
    }
}

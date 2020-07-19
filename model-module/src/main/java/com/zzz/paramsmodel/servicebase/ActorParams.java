package com.zzz.paramsmodel.servicebase;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActorParams {
    @JsonProperty(value = "IdentityCard")
    private String lastName;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "ActorParams{" +
                "lastName='" + lastName + '\'' +
                '}';
    }
}

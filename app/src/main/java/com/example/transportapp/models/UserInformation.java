package com.example.transportapp.models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserInformation {

    String name;
    String lastName;
    String identification;
    String phone;
    String birthdate;

    public UserInformation(String name, String lastName, String identification, String phone, String birthdate) {
        this.name = name;
        this.lastName = lastName;
        this.identification = identification;
        this.phone = phone;
        this.birthdate = birthdate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthDate() {return birthdate;}

    public void setBirthDate(String birthDate) {this.birthdate = birthdate;}
}

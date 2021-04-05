package com.example.healthza;

public class Doctor {

    private String doctor_id;
    private String name;
    private String email;
    private String phoneNumber;
    private String birthDate;
    private String sex;
    private boolean completeInfo;

    public Doctor(String doctor_id, String name, String email, String phoneNumber, String birthDate, String sex, boolean completeInfo)
    {
        this.doctor_id = doctor_id;
        this.name = name;
        this.email = email;
        this.phoneNumber =phoneNumber;
        this.birthDate = birthDate;
        this.sex = sex;
        this.completeInfo = completeInfo;
    }

    public Doctor() {

    }

    public String getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

}
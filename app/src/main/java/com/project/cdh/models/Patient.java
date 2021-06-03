package com.project.cdh.models;

public class Patient
{
    private String patientId;
    private String name;
    private String identificationNumber;
    private String email;
    private String phoneNumber;
    private String birthDate;
    private String sex;
    private double weight, height, bmi;
    private boolean completeInfo;

    public Patient(String patientId, String name, String identificationNumber, String email, String phoneNumber, String birthDate, String sex, boolean completeInfo)
    {
        this.patientId = patientId;
        this.name = name;
        this.identificationNumber = identificationNumber;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.sex = sex;
        this.completeInfo = completeInfo;
    }

    public Patient()
    {

    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
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

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public boolean isCompleteInfo() {
        return completeInfo;
    }

    public void setCompleteInfo(boolean completeInfo) {
        this.completeInfo = completeInfo;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }
}

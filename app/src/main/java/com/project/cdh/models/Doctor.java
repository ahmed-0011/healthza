package com.project.cdh.models;

import java.util.List;

public class Doctor
{
    List<String> workdays;
    private String doctorId;
    private String name;
    private String identificationNumber;
    private String email;
    private String phoneNumber;
    private String birthDate;
    private String sex;
    private String speciality;
    private String workplace;
    private int yearsOfExperience;
    private boolean completeInfo;

    public Doctor(String doctorId, String name, String identificationNumber, String email, String phoneNumber, String birthDate, String sex, boolean completeInfo)
    {
        this.doctorId = doctorId;
        this.name = name;
        this.identificationNumber = identificationNumber;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.sex = sex;
        this.completeInfo = completeInfo;
    }

    public Doctor()
    {

    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
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

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getWorkplace() {
        return workplace;
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }

    public List<String> getWorkdays() {
        return workdays;
    }

    public void setWorkdays(List<String> workdays) {
        this.workdays = workdays;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

}
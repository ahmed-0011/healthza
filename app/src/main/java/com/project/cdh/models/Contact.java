package com.project.cdh.models;

public class Contact
{
    /////////varable////////////
    private String name;
    private String phoneNumber;
    private String contactId;
/////////////////////////////////////
    public Contact(Doctor doctor)
    {
        name = doctor.getName();
        phoneNumber = doctor.getPhoneNumber();
        contactId = doctor.getDoctorId();
    }

    public Contact(Patient patient)
    {
        name = patient.getName();
        phoneNumber = patient.getPhoneNumber();
        contactId = patient.getPatientId();
    }

    public Contact() {

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }
}

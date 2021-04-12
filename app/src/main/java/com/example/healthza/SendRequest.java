package com.example.healthza;

import java.util.Date;

public class SendRequest
{
    private String patientId;
    private String patientName;
    private String phoneNumber;
    private String requestDate;
    private String identificationNumber;
    private String status;

    public SendRequest(String patientId, String patientName, String phoneNumber, String requestDate, String identificationNumber, String status)
    {
        this.patientId = patientId;
        this.patientName = patientName;
        this.phoneNumber = phoneNumber;
        this.requestDate = requestDate;
        this.identificationNumber = identificationNumber;
        this.status = status;
    }

    public SendRequest()
    {

    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

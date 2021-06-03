package com.project.cdh.models;

public class ReceiveRequest
{
    private String doctorId;
    private String doctorName;
    private String phoneNumber;
    private String requestDate;


    public ReceiveRequest(String doctorId, String doctorName, String phoneNumber, String requestDate)
    {
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.phoneNumber = phoneNumber;
        this.requestDate = requestDate;
    }

    public ReceiveRequest()
    {

    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
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
}

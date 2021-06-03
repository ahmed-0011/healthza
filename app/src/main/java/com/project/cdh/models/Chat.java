package com.project.cdh.models;

import com.google.firebase.Timestamp;

public class Chat
{

    ////////////////varable////////////
    private String chatId;
    private String doctorName;
    private String patientName;
    private String patientId;
    private String doctorId;
    private String latestMessage;
    private Timestamp latestMessageTimestamp;
//////////////////////////////////////////
    public Chat()
    {

    }

    public Chat(String chatId, String doctorName, String patientName, String doctorId, String patientId, String latestMessage, Timestamp latestMessageTimestamp)
    {
        this.chatId = chatId;
        this.doctorName = doctorName;
        this.patientName = patientName;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.latestMessage = latestMessage;
        this.latestMessageTimestamp = latestMessageTimestamp;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getLatestMessage() {
        return latestMessage;
    }

    public void setLatestMessage(String lastMessage) {
        this.latestMessage = lastMessage;
    }

    public Timestamp getLatestMessageTimestamp() {
        return latestMessageTimestamp;
    }

    public void setLatestMessageTimestamp(Timestamp latestMessageTimestamp) {
        this.latestMessageTimestamp = latestMessageTimestamp;
    }

}

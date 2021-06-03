package com.project.cdh.models;

import com.google.firebase.Timestamp;

public class Message
{
    private String content;
    private Timestamp timeStamp;
    private String senderId;
    private String senderName;
    private String messageId;

    public Message(String content, Timestamp timeStamp, String senderId, String senderName, String messageId)
    {
        this.content = content;
        this.timeStamp = timeStamp;
        this.senderId = senderId;
        this.senderName = senderName;
        this.messageId = messageId;
    }

    public Message()
    {

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}

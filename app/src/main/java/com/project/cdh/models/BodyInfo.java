package com.project.cdh.models;


import com.google.firebase.Timestamp;

public class BodyInfo
{
    /////
    private double weight;
    private double height;
    private double bmi;
    private Timestamp timestamp;
    private String bodyInfoId;

    public BodyInfo(String bodyInfoId, double weight, double height, double bmi, Timestamp timestamp)
    {
        this.bodyInfoId = bodyInfoId;
        this.weight = weight;
        this.height = height;
        this.bmi = bmi;
        this.timestamp = timestamp;
    }

    public BodyInfo() { }

    public double getWeight()
    {
        return weight;
    }

    public void setWeight(double weight)
    {
        this.weight = weight;
    }

    public double getHeight()
    {
        return height;
    }

    public void setHeight(double height)
    {
        this.height = height;
    }

    public double getBmi()
    {
        return bmi;
    }

    public void setBmi(double bmi)
    {
        this.bmi = bmi;
    }

    public Timestamp getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getBodyInfoId() {
        return bodyInfoId;
    }

    public void setBodyInfoId(String bodyInfoId) {
        this.bodyInfoId = bodyInfoId;
    }


}

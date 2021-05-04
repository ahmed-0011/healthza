package com.example.healthza.models;

import com.google.firebase.Timestamp;

public class DailyTest implements Comparable<DailyTest>
{
    private String testType;
    private double testLevel;
    private Timestamp testTime;

    public DailyTest(String testType, double testLevel, Timestamp testTime)
    {
        this.testType = testType;
        this.testLevel = testLevel;
        this.testTime = testTime;
    }

    public DailyTest()
    {

    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public double getTestLevel() {
        return testLevel;
    }

    public void setTestLevel(double testLevel) {
        this.testLevel = testLevel;
    }

    public Timestamp getTestTime() {
        return testTime;
    }

    public void setTestTime(Timestamp testTime) {
        this.testTime = testTime;
    }

    @Override
    public int compareTo(DailyTest o)
    {
        if(o != null)
        {
            Timestamp testTime = ((DailyTest) o).getTestTime();
            return testTime.compareTo(this.testTime);
        }
        return 0;
    }
}

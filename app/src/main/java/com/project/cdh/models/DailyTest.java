package com.project.cdh.models;

import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;

public class DailyTest implements Comparable<DailyTest>
{
    private String type;
    private double level;
    private Timestamp timestamp;

    public DailyTest(String type, double level, Timestamp timestamp)
    {
        this.type = type;
        this.level = level;
        this.timestamp = timestamp;
    }

    public DailyTest()
    {

    }

    public String getType()
    {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLevel() {
        return level;
    }

    public void setLevel(double level) {
        this.level = level;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(DailyTest o)
    {
        if(o != null)
        {
            Timestamp timestamp =  o.getTimestamp();
            return timestamp.compareTo(this.timestamp);
        }
        return 0;
    }

    @Override
    public boolean equals(@Nullable Object o)
    {

        DailyTest dailyTest = (DailyTest) o;

        return this.getType().equals(dailyTest.getType())
                && Double.compare(this.getLevel(), dailyTest.getLevel()) == 0
                && this.getTimestamp().equals(dailyTest.getTimestamp());

    }
}

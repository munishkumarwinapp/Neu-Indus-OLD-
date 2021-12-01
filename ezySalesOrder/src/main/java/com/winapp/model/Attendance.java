package com.winapp.model;

/**
 * Created by user on 20-Apr-17.
 */

public class Attendance {
    String code;
    String name;
    String inTime;
    String outTime;
    double inLatitude;
    double inLongitude;
    double outLatitude;
    double outLongitude;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public double getInLatitude() {
        return inLatitude;
    }

    public void setInLatitude(double inLatitude) {
        this.inLatitude = inLatitude;
    }

    public double getInLongitude() {
        return inLongitude;
    }

    public void setInLongitude(double inLongitude) {
        this.inLongitude = inLongitude;
    }

    public double getOutLatitude() {
        return outLatitude;
    }

    public void setOutLatitude(double outLatitude) {
        this.outLatitude = outLatitude;
    }

    public double getOutLongitude() {
        return outLongitude;
    }

    public void setOutLongitude(double outLongitude) {
        this.outLongitude = outLongitude;
    }
}

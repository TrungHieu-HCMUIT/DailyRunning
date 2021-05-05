package com.example.dailyrunning.Model;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

public class Activity {
    private String activityID;
    private String dateCreated;
    private double distance;
    private long duration;
    private ArrayList<LatLng> latLngArrayList;
    private String describe;
    private int pace;

    public Activity(String activityID, String dateCreated, double distance, long duration, ArrayList latLngArrayList, String describe, int pace) {
        this.activityID = activityID;
        this.dateCreated = dateCreated;
        this.distance = distance;
        this.duration = duration;
        this.latLngArrayList = latLngArrayList;
        this.describe = describe;
        this.pace = pace;
    }

    public String getActivityID() {
        return activityID;
    }

    public void setActivityID(String activityID) {
        this.activityID = activityID;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public ArrayList<LatLng> getLatLngArrayList() {
        return latLngArrayList;
    }

    public void setLatLngArrayList(ArrayList<LatLng> latLngArrayList) {
        this.latLngArrayList = latLngArrayList;
    }

    public String getDescribe() { return describe; }

    public void setDescribe(String describe) { this.describe = describe; }

    public int getPace() { return pace; }

    public void setPace(int pace) { this.pace = pace; }

}

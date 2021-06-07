package com.example.dailyrunning.model;

import java.util.ArrayList;

public class Activity {
    private String activityID;
    private String userID;
    private String dateCreated;
    private double distance;
    private long duration;
    private String pictureURI;
    private double pace;
    private String describe;
    private ArrayList<LatLng> latLngArrayList;

    public Activity() { }

    public Activity(String activityID, String userID, String dateCreated, double distance, long duration,
                    String pictureURI, double pace, String describe, ArrayList<LatLng> latLngArrayList) {
        this.activityID = activityID;
        this.userID = userID;
        this.dateCreated = dateCreated;
        this.distance = distance;
        this.duration = duration;
        this.pictureURI = pictureURI;
        this.pace = pace;
        this.describe = describe;
        this.latLngArrayList = latLngArrayList;
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

    public ArrayList<LatLng> getLatLngArrayList() { return latLngArrayList; }

    public void setLatLngArrayList(ArrayList<LatLng> latLngArrayList) { this.latLngArrayList = latLngArrayList; }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public double getPace() {
        return pace;
    }

    public void setPace(int pace) {
        this.pace = pace;
    }

    public String getPictureURI() {
        return pictureURI;
    }

    public void setPictureURI(String pictureURI) {
        this.pictureURI = pictureURI;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}

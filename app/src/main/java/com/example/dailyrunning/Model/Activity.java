package com.example.dailyrunning.Model;

import java.util.Date;

public class Activity {
    private String activityID;
    private String userID;
    private Date dateCreated;
    private double distance;
    private Date duration;
    private String pictureURI;
    private double pace;

    public Activity(String activityID, String userID, Date dateCreated, double distance, Date duration, String pictureURI, double pace) {
        this.activityID = activityID;
        this.userID = userID;
        this.dateCreated = dateCreated;
        this.distance = distance;
        this.duration = duration;
        this.pictureURI = pictureURI;
        this.pace = pace;
    }

    public String getActivityID() {
        return activityID;
    }

    public void setActivityID(String activityID) {
        this.activityID = activityID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Date getDuration() {
        return duration;
    }

    public void setDuration(Date duration) {
        this.duration = duration;
    }

    public String getPictureURI() {
        return pictureURI;
    }

    public void setPictureURI(String pictureURI) {
        this.pictureURI = pictureURI;
    }

    public double getPace() {
        return pace;
    }

    public void setPace(double pace) {
        this.pace = pace;
    }


}

package com.example.dailyrunning.Model;

import java.io.Serializable;
import java.util.Date;

public class UserInfo implements Serializable {
    private String displayName;
    private String email;
    private int point;
    private int gender;
    private String userID;
    private Date dob;
    private double height;
    private double weight;
    private String avatarURI;

    public UserInfo()
    {}
    public UserInfo(String displayName, String email, int point,
                    int gender, String userID, Date dob, double height,
                    double weight, String avatarURI) {
        this.displayName = displayName;
        this.email = email;
        this.point = point;
        this.gender = gender;
        this.userID = userID;
        this.dob = dob;
        this.height = height;
        this.weight = weight;
        this.avatarURI = avatarURI;
    }

    public UserInfo(String displayName, String email, int gender, String userID) {
        this.displayName = displayName;
        this.email = email;
        this.gender = gender;
        this.userID = userID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getAvatarURI() {
        return avatarURI;
    }

    public void setAvatarURI(String avatarURI) {
        this.avatarURI = avatarURI;
    }




    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}

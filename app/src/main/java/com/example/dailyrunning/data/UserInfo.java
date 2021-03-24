package com.example.dailyrunning.data;

import java.util.Date;

public class UserInfo {
    private String displayName;
    private String email;
    private int point;
    private int gender;

    public UserInfo(String displayName, String email, int point, int gender,
                    Date dob, double height, double weight, String avatarURI) {
        this.displayName = displayName;
        this.email = email;
        this.point = point;
        this.gender = gender;
        this.dob = dob;
        this.height = height;
        this.weight = weight;
        this.avatarURI = avatarURI;
    }

    private Date dob;
    private double height;
    private double weight;
    private String avatarURI;

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
}

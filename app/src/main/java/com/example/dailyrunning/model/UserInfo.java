package com.example.dailyrunning.model;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.Date;

public class UserInfo implements Serializable {
    private String displayName;
    private String email;
    private int point;
    private boolean gender;
    private String userID;
    private Date dob;
    private int height;
    private int weight;
    private String avatarURI;

    public void addPoint(int point)
    {
        this.point+=point;
    }
    public boolean exchangeGift(int point)
    {
        if(this.point<point)
            return false;
        this.point-=point;
        return  true;
    }
    public boolean validateData() {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(displayName) || dob==null) {
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return false;
        return true;
    }
    public UserInfo()
    {}
    public UserInfo(String displayName, String email, int point,
                    boolean gender, String userID, Date dob, int height,
                    int weight, String avatarURI) {
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

   /* public UserInfo(String displayName, String email, int gender, String userID) {
        this.displayName = displayName;
        this.email = email;
        this.gender = gender;
        this.userID = userID;
    }*/

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

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
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

package com.example.dailyrunning.model;

public class Like {
    private String userID;
    private String avatarUrl;
    private String userName;

    public Like() { }

    public Like(String userID, String avatarUrl, String userName) {
        this.userID = userID;
        this.avatarUrl = avatarUrl;
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

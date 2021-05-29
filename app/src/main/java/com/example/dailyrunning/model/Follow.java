package com.example.dailyrunning.model;

public class Follow {
    private String followerID;
    private String followingID;

    public Follow(String followerID, String followingID) {
        this.followerID = followerID;
        this.followingID = followingID;
    }

    public String getFollowingID() {
        return followingID;
    }

    public void setFollowingID(String followingID) {
        this.followingID = followingID;
    }

    public String getFollowerID() {
        return followerID;
    }

    public void setFollowerID(String followerID) {
        this.followerID = followerID;
    }
}

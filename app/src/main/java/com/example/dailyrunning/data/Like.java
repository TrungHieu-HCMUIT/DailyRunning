package com.example.dailyrunning.data;

public class Like {
    private String postID;
    private String userID;

    public Like(String postID, String userID) {
        this.postID = postID;
        this.userID = userID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}

package com.example.dailyrunning.data;

import java.util.Date;

public class Post {
    private String postID;
    private String userID;
    private Date dateCreated;
    private String content;
    private int likeNum;
    private String activityID;

    public Post(String postID, String userID, Date dateCreated, String content, int likeNum, String activityID) {
        this.postID = postID;
        this.userID = userID;
        this.dateCreated = dateCreated;
        this.content = content;
        this.likeNum = likeNum;
        this.activityID = activityID;
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

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public String getActivityID() {
        return activityID;
    }

    public void setActivityID(String activityID) {
        this.activityID = activityID;
    }
}

package com.example.dailyrunning.Model;

import java.util.Date;

public class Comment {
    private String commentID;
    private String postID;
    private String content;
    private Date timeCreated;

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Comment(String commentID, String postID, String content, Date timeCreated) {
        this.commentID = commentID;
        this.postID = postID;
        this.content = content;
        this.timeCreated = timeCreated;
    }
}

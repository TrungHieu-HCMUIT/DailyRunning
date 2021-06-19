package com.example.dailyrunning.model;

public class Comment {
    private String commentID;
    private String content;
    private String dateCreated;
    private String ownerID;


    public Comment() { }

    public Comment(String commentID, String content, String dateCreated, String ownerID) {
        this.commentID = commentID;
        this.content = content;
        this.dateCreated = dateCreated;
        this.ownerID = ownerID;

    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

}

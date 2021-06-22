package com.example.dailyrunning.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Post implements Comparable<Post>{
    private String postID;
    private List<Comment> comments;
    private List<String> likesUserId;
    private Activity activity;
    private String ownerID;
    private String ownerAvatarUrl;
    private String ownerName;

    public Post() {}

    public Post(String postID, List<Comment> comments, List<String> likesUserId, Activity activity, String ownerID, String ownerAvatarUrl, String ownerName) {
        this.postID = postID;
        this.comments = comments;
        this.likesUserId = likesUserId;
        this.activity = activity;
        this.ownerID = ownerID;
        this.ownerAvatarUrl = ownerAvatarUrl;
        this.ownerName = ownerName;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<String> getLikesUserId() {
        return likesUserId;
    }

    public void setLikesUserId(List<String> likesUserId) {
        this.likesUserId = likesUserId;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getOwnerAvatarUrl() {
        return ownerAvatarUrl;
    }

    public void setOwnerAvatarUrl(String ownerAvatarUrl) {
        this.ownerAvatarUrl = ownerAvatarUrl;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @Override
    public int compareTo(Post o) {
        try {
            Date thisDate = convertStringToDate(this.activity.getDateCreated());
            Date otherDate = convertStringToDate(o.activity.getDateCreated());
            if (thisDate.before(otherDate)) {
                return 1;
            }
            else {
                return -1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Date convertStringToDate(String date) throws ParseException {
        return new SimpleDateFormat("dd-MM-yyy HH:mm").parse(date);
    }
}

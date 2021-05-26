package com.example.dailyrunning.Model;

public class PostDataTest {
    private String userAvatarUri;
    private String username;
    private String dateTime;
    private String content;
    private String distance;
    private String duration;
    private String pace;
    private String image;
    private int numOfLike;
    private int numOfComment;

    public PostDataTest(String userAvatarUri, String username, String dateTime, String content, String distance, String duration, String pace, String image, int numOfLike, int numOfComment) {
        this.userAvatarUri = userAvatarUri;
        this.username = username;
        this.dateTime = dateTime;
        this.content = content;
        this.distance = distance;
        this.duration = duration;
        this.pace = pace;
        this.image = image;
        this.numOfLike = numOfLike;
        this.numOfComment = numOfComment;
    }


    public String getUserAvatarUri() {
        return userAvatarUri;
    }

    public void setUserAvatarUri(String userAvatarUri) {
        this.userAvatarUri = userAvatarUri;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPace() {
        return pace;
    }

    public void setPace(String pace) {
        this.pace = pace;
    }

    public String getImage() { return image; }

    public void setImage(String image) { this.image = image; }

    public int getNumOfLike() {
        return numOfLike;
    }

    public void setNumOfLike(int numOfLike) {
        this.numOfLike = numOfLike;
    }

    public int getNumOfComment() {
        return numOfComment;
    }

    public void setNumOfComment(int numOfComment) {
        this.numOfComment = numOfComment;
    }
}

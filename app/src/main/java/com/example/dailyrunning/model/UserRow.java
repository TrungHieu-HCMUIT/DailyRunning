package com.example.dailyrunning.model;

public class UserRow {
    private String avatarURI;
    private String displayName;

    public UserRow(String avatarURI, String displayName) {
        this.avatarURI = avatarURI;
        this.displayName = displayName;
    }

    public String getAvatarURI() {
        return avatarURI;
    }

    public void setAvatarURI(String avatarURI) {
        this.avatarURI = avatarURI;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}

package com.example.dailyrunning.model;

public class MedalInfo {
    private int imageID;
    private String medalName;
    private String medalDetail;

    public MedalInfo(int imageID, String medalName, String medalDetail) {
        this.imageID = imageID;
        this.medalName = medalName;
        this.medalDetail = medalDetail;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public String getMedalName() {
        return medalName;
    }

    public void setMedalName(String medalName) {
        this.medalName = medalName;
    }

    public String getMedalDetail() {
        return medalDetail;
    }

    public void setMedalDetail(String medalDetail) {
        this.medalDetail = medalDetail;
    }
}

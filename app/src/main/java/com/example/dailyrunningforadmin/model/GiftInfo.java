package com.example.dailyrunningforadmin.model;

public class GiftInfo {
    private String ID;
    private String photoUri;
    private String providerName;
    private String giftDetail;
    private int point;

    public GiftInfo(){}

    public GiftInfo(String ID, String photoUri, String providerName, String giftDetail, int point) {
        this.ID = ID;
        this.photoUri = photoUri;
        this.providerName = providerName;
        this.giftDetail = giftDetail;
        this.point = point;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getGiftDetail() {
        return giftDetail;
    }

    public void setGiftDetail(String giftDetail) {
        this.giftDetail = giftDetail;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}


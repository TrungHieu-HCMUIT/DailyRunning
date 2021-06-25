package com.example.dailyrunning.model;

import android.net.Uri;

public class GiftInfo {
    private String photoUri;
    private String providerName;
    private String giftDetail;
    private int point;
    private String ID;

    public GiftInfo(){}
    public GiftInfo(String photoUri, String providerName, String giftDetail, int point,String ID) {
        this.photoUri = photoUri;
        this.providerName = providerName;
        this.giftDetail = giftDetail;
        this.point = point;
        this.ID=ID;
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

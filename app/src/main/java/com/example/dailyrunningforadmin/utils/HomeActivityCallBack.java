package com.example.dailyrunningforadmin.utils;

import com.example.dailyrunningforadmin.model.GiftInfo;

public interface HomeActivityCallBack {
    public void pickImageFromGallery();
    public void initBottomSheet(GiftInfo gift);
}
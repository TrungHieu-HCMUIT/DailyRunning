package com.example.dailyrunningforadmin.viewmodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dailyrunningforadmin.model.GiftInfo;
import com.example.dailyrunningforadmin.repository.Repo;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {
    MutableLiveData<ArrayList<GiftInfo>> giftList;

    public void init(Context context) {
        if (giftList != null && FirebaseAuth.getInstance().getCurrentUser() != null) {
            return;
        }
        giftList = Repo.getInstance(context).getGiftList();
    }

    public LiveData<ArrayList<GiftInfo>> getGiftList() {
        return giftList;
    }

    public void addGift(Context context, GiftInfo gift, Bitmap bitmap) {
        Repo.getInstance(context).addGift(gift, bitmap);
    }

    public void deleteGift(Context context, GiftInfo gift) {
        Repo.getInstance(context).deleteGift(gift);
    }
}

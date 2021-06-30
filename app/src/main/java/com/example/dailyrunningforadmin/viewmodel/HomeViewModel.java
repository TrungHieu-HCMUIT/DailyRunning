package com.example.dailyrunningforadmin.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dailyrunningforadmin.model.GiftInfo;
import com.example.dailyrunningforadmin.repository.Repo;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {
    MutableLiveData<ArrayList<GiftInfo>> giftList;

    public void init(Context context) {
        if (giftList != null) {
            return;
        }
        giftList = Repo.getInstance(context).getGiftList();
    }

    public LiveData<ArrayList<GiftInfo>> getGiftList() {
        return giftList;
    }
}

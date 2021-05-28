package com.example.dailyrunning.utils;

import android.os.Parcelable;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dailyrunning.home.HomeActivity;

public class HomeViewModel extends ViewModel {
    public Parcelable userRecyclerViewState;
    public Parcelable followingRecyclerViewState;
    public Integer tabPosition;
    public Boolean isExpanded;
    public MutableLiveData<HomeActivity>mHomeActivity=new MutableLiveData<>();
}

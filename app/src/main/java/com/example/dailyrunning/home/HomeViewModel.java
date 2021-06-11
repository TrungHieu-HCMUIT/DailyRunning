package com.example.dailyrunning.home;

import android.os.Parcelable;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dailyrunning.home.HomeActivity;
import com.example.dailyrunning.model.UserInfo;
import com.example.dailyrunning.user.UserNavigator;

public class HomeViewModel extends ViewModel {

    public MutableLiveData<HomeActivity>mHomeActivity=new MutableLiveData<>();

    public Parcelable userRecyclerViewState;
    public Parcelable followingRecyclerViewState;
    public Integer tabPosition;
    public Boolean isExpanded;

    public boolean isActivityShow = true;
}

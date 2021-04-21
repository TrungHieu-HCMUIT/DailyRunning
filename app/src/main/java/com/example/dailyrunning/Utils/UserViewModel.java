package com.example.dailyrunning.Utils;


import android.os.Parcelable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dailyrunning.Model.UserInfo;
import com.google.firebase.auth.FirebaseAuth;

public class UserViewModel extends ViewModel {
    public final MutableLiveData<UserInfo> currentUser = new MutableLiveData<UserInfo>();
    public final MutableLiveData<FirebaseAuth> mFirebaseAuth = new MutableLiveData<FirebaseAuth>();
    public Parcelable mMedalRecyclerViewState;
    public Parcelable mGiftRecyclerViewState;
    public Integer mScrollViewPosition;


}

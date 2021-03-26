package com.example.dailyrunning.helper;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dailyrunning.data.UserInfo;
import com.google.firebase.auth.FirebaseAuth;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<UserInfo> selected = new MutableLiveData<UserInfo>();
    private final MutableLiveData<FirebaseAuth> mFirebaseAuth = new MutableLiveData<FirebaseAuth>();
    public void select(UserInfo item) {
        selected.setValue(item);
    }

    public LiveData<UserInfo> getSelected() {
        return selected;
    }
    public void selectFirebaseAuth(FirebaseAuth item) {
        mFirebaseAuth.setValue(item);
    }

    public LiveData<FirebaseAuth> getFirebaseAuth() {
        return mFirebaseAuth;
    }

}

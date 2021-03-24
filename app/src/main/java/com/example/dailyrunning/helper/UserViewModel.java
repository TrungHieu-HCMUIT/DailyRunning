package com.example.dailyrunning.helper;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dailyrunning.data.UserInfo;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<UserInfo> selected = new MutableLiveData<UserInfo>();

    public void select(UserInfo item) {
        selected.setValue(item);
    }

    public LiveData<UserInfo> getSelected() {
        return selected;
    }

}

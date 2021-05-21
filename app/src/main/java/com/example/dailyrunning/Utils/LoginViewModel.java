package com.example.dailyrunning.Utils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dailyrunning.Model.UserInfo;
import com.facebook.CallbackManager;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<UserInfo> mNewUser;
    public UserInfo tempUser;
    public MutableLiveData<UserInfo> getNewUser()
    {
        if(mNewUser==null)
            mNewUser=new MutableLiveData<>();
        return mNewUser;
    }
    public CallbackManager mCallbackManager;
    public Boolean isFromRegister=false;
}

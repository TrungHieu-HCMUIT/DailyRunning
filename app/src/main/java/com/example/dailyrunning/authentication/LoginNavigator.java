package com.example.dailyrunning.authentication;

public interface LoginNavigator {
    void onGoogleLoginClick();
    void navToRegister();
    void navToUpdateInfo();
    void navToForgotPassword();
    void popBack();
}

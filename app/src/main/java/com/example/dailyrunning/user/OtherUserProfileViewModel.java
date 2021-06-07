package com.example.dailyrunning.user;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.example.dailyrunning.R;
import com.example.dailyrunning.authentication.LoginActivity;
import com.example.dailyrunning.model.UserInfo;
import com.example.dailyrunning.utils.MedalAdapter;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class OtherUserProfileViewModel extends ViewModel {
    private MutableLiveData<String> avatarUrl=new MutableLiveData<>();
    private MutableLiveData<String> userName=new MutableLiveData<>();
    private MutableLiveData<Integer> followerCount=new MutableLiveData<>();
    private MutableLiveData<Integer> followingCount=new MutableLiveData<>();
    private MutableLiveData<Integer> runningPoint=new MutableLiveData<>();
    //Huy hiệu cập nhật sau


    @BindingAdapter({"avatarUrl"})
    public static void setProfilePicture(ImageView imageView, String url) {
        if (url == null)
            Glide.with(imageView.getContext()).load(LoginActivity.DEFAULT_AVATAR_URL).into(imageView);
        else
            Glide.with(imageView.getContext()).load(url).into(imageView);

    }

    public LiveData<String> getAvatarUrl() {
        return avatarUrl;
    }

    public LiveData<String> getUserName() {
        return userName;
    }

    public LiveData<Integer> getFollowerCount() {
        return followerCount;
    }

    public LiveData<Integer> getFollowingCount() {
        return followingCount;
    }

    public LiveData<Integer> getRunningPoint() {
        return runningPoint;
    }


}

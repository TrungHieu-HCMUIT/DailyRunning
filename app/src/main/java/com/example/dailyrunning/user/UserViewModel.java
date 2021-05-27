package com.example.dailyrunning.user;


import android.net.Uri;
import android.os.Parcelable;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.example.dailyrunning.model.GiftInfo;
import com.example.dailyrunning.model.UserInfo;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserViewModel extends ViewModel {



    public MutableLiveData<UserInfo> currentUser = new MutableLiveData<UserInfo>();
    public Parcelable mMedalRecyclerViewState;
    public Parcelable mGiftRecyclerViewState;
    public Integer mScrollViewPosition;
    private UserNavigator mUserNavigator;
    private static final String EMAIL_PROVIDER_ID = "password";
    private static final String GOOGLE_PROVIDER_ID = "google.com";
    private static final String FACEBOOK_PROVIDER_ID = "facebook.com";
    private  MutableLiveData<List<GiftInfo>> gifts;


    public void onChangeAvatarClick()
    {
        mUserNavigator.updateAvatarClick();
    }
    public LiveData<List<GiftInfo>> getGifts()
    {
        if(gifts==null)
        {
            gifts=new MutableLiveData<>();
            getGiftData();
        }

        return gifts;
    }
    private void getGiftData()
    {
        List<GiftInfo> giftData=new ArrayList<>();
        giftData.add(new GiftInfo(Uri.parse("Temp_uri"), "Provider 1", "Gift detail 1", (int) (Math.random() * 100), "temp_id"));
        giftData.add(new GiftInfo(Uri.parse("Temp_uri"), "Provider 1", "Gift detail 1", (int) (Math.random() * 100), "temp_id"));
        giftData.add(new GiftInfo(Uri.parse("Temp_uri"), "Provider 1", "Gift detail 1", (int) (Math.random() * 100), "temp_id"));
        giftData.add(new GiftInfo(Uri.parse("Temp_uri"), "Provider 1", "Gift detail 1", (int) (Math.random() * 100), "temp_id"));
        giftData.add(new GiftInfo(Uri.parse("Temp_uri"), "Provider 1", "Gift detail 1", (int) (Math.random() * 100), "temp_id"));
        giftData.add(new GiftInfo(Uri.parse("Temp_uri"), "Provider 1", "Gift detail 1", (int) (Math.random() * 100), "temp_id"));
        giftData.add(new GiftInfo(Uri.parse("Temp_uri"), "Provider 1", "Gift detail 1", (int) (Math.random() * 100), "temp_id"));
        giftData.add(new GiftInfo(Uri.parse("Temp_uri"), "Provider 1", "Gift detail 1", (int) (Math.random() * 100), "temp_id"));
        giftData.add(new GiftInfo(Uri.parse("Temp_uri"), "Provider 1", "Gift detail 1", (int) (Math.random() * 100), "temp_id"));
        gifts.setValue(giftData);
    }

    @BindingAdapter({"user"})
    public static void setProfilePicture(ImageView imageView, FirebaseUser userInfo) {
        switch (userInfo.getProviderData().get(1).getProviderId()) {
            case EMAIL_PROVIDER_ID:
            case GOOGLE_PROVIDER_ID:
                Glide.with(imageView.getContext()).load(userInfo.getPhotoUrl()).into(imageView);
                break;
            case FACEBOOK_PROVIDER_ID:
                //user chưa update avt thì lấy của fb
                if (userInfo.getPhotoUrl().toString().contains("graph.facebook.com")) {
                    //https://graph.facebook.com/2511714412307915/picture
                    String fbUID = userInfo.getPhotoUrl().toString().
                            replace("https://graph.facebook.com/", "")
                            .replace("/picture", "");
                    GraphRequest request = GraphRequest.newGraphPathRequest(
                            AccessToken.getCurrentAccessToken(),
                            "/" +fbUID + "/picture?redirect=0&type=normal",
                            response -> {
                                JSONObject res = response.getJSONObject();
                                try {
                                    String avatarUrl = res.getJSONObject("data").getString("url");
                                    Glide.with(imageView.getContext()).load(avatarUrl).into(imageView);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });

                    request.executeAsync();
                }
                else
                {
                    Glide.with(imageView.getContext()).load(userInfo.getPhotoUrl()).into(imageView);
                }
                break;

        }
    }
    public void setNavigator(UserNavigator mUserNavigator)
    {
        this.mUserNavigator=mUserNavigator;
    }
    public void settingOnClick()
    {
        mUserNavigator.settingOnClick();
    }
    public void allGiftOnClick()
    {
        mUserNavigator.allGiftOnClick();
    }
    public void onLogOutClick()
    {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
    }


}

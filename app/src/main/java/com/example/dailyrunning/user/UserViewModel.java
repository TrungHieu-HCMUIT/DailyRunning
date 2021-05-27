package com.example.dailyrunning.user;


import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.dailyrunning.R;
import com.example.dailyrunning.model.GiftInfo;
import com.example.dailyrunning.model.UserInfo;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

public class UserViewModel extends ViewModel {


    private MutableLiveData<UserInfo> currentUser;
    public Parcelable mMedalRecyclerViewState;
    public Parcelable mGiftRecyclerViewState;
    public Integer mScrollViewPosition;
    private UserNavigator mUserNavigator;
    private static final String EMAIL_PROVIDER_ID = "password";
    private static final String GOOGLE_PROVIDER_ID = "google.com";
    private static final String FACEBOOK_PROVIDER_ID = "facebook.com";
    private  MutableLiveData<List<GiftInfo>> gifts;
    private final DatabaseReference mUserInfoRef= FirebaseDatabase.getInstance().getReference().child("UserInfo");
    public MutableLiveData<String> avatarUri;

    public LiveData<UserInfo> getCurrentUser()
    {
        if(currentUser==null)
        {
            currentUser=new MutableLiveData<>();
            getUserInfo();
        }
        return currentUser;
    }
    public LiveData<String> getAvatarUri()
    {
        if(avatarUri==null)
        {
            avatarUri=new MutableLiveData<>();
            avatarUri.setValue(currentUser.getValue().getAvatarURI());
        }
        return avatarUri;
    }

    public void getUserInfo()
    {
        DatabaseReference mCurrentUserRef = mUserInfoRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mCurrentUserRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(this.getClass().getName(), task.getException().toString());
                return;
            }
            DataSnapshot taskRes = task.getResult();
            currentUser.setValue(taskRes.getValue(UserInfo.class));
            if (currentUser.getValue() == null) {
                Log.e(this.getClass().getName(), "current user is nulll");
                return;
            }
        });
    }
    public void putAvatarToFireStorage(Intent data)
    {
        Uri selectedImageUri = data.getData();
        FirebaseUser userInfo = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference mAvatarStorageReference = FirebaseStorage.getInstance().getReference().child("avatar_photos");

        //tạo ref mới trong folder avatar_photos/
        StorageReference photoRef = mAvatarStorageReference.child(selectedImageUri.getLastPathSegment());
        //up hình lên
        photoRef.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot -> photoRef.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Uri userAvatarUri = uri;
                    //update avatarURI trong UserInfo
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(currentUser.getValue().getUserID());
                    userRef.child("avatarURI").setValue(userAvatarUri.toString());
                    //update profile của firebase user
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(userAvatarUri).build();
                    userInfo.updateProfile(profileUpdates).addOnCompleteListener(task ->avatarUri.setValue(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString()));
                }));
    }
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

    @BindingAdapter({"dobText"})
    public static void setText(View view, Date date)
    {
        SimpleDateFormat mDateFormat=new SimpleDateFormat("dd/MM/yyyy");

        String res=mDateFormat.format(date);
        ((TextView)view).setText(res);
    }

    @BindingAdapter({"pickerMin"})
    public static void setMinValueForNumberPicker(View view, int value)
    {
        NumberPicker numberPicker= (NumberPicker) view;
        numberPicker.setMinValue(value);
    }
    @BindingAdapter({"pickerMax"})
    public static void setMaxValueForNumberPicker(View view, int value)
    {
        NumberPicker numberPicker= (NumberPicker) view;
        numberPicker.setMaxValue(value);
    }
    @BindingAdapter({"pickerValue"})
    public static void setValueForNumberPicker(View view, int value)
    {
        NumberPicker numberPicker= (NumberPicker) view;
        numberPicker.setValue(value);
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
    public void updateInfo(UserInfo mNewInfo, onUpdateCallback callBack)
    {
        mUserInfoRef.child(mNewInfo.getUserID()).setValue(mNewInfo).addOnCompleteListener(task->{
            if(task.isSuccessful())
            {
                currentUser.setValue(mNewInfo);
                updateFirebaseUser(mNewInfo);
                callBack.onComplete(true);

            }
            else if(!task.isSuccessful())
            {
                callBack.onComplete(false);
            }
        });
    }
    private void updateFirebaseUser(UserInfo mNewInfo) {
        FirebaseUser mUser= FirebaseAuth.getInstance().getCurrentUser();
        mUser.updateEmail(mNewInfo.getEmail());
        UserProfileChangeRequest mRequest=new UserProfileChangeRequest.Builder().setDisplayName(mNewInfo.getDisplayName()).build();
        mUser.updateProfile(mRequest);

    }
    public void onBackPress()
    {
        mUserNavigator.pop();
    }
    public interface onUpdateCallback{
        void onComplete(boolean result);

    }



}

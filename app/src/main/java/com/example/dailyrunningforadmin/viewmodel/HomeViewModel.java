package com.example.dailyrunningforadmin.viewmodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dailyrunningforadmin.model.GiftInfo;
import com.example.dailyrunningforadmin.repository.Repo;
import com.example.dailyrunningforadmin.utils.DataLoadListener;
import com.example.dailyrunningforadmin.utils.LoginNavigator;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeViewModel extends ViewModel {
    MutableLiveData<ArrayList<GiftInfo>> giftList;

    private static final String adminEmail = "work.trunghieu.0107@gmail.com";

    private Context mContext;

    private DataLoadListener mDataLoadListener;

    public MutableLiveData<String> currentPassword =new MutableLiveData<>();
    public MutableLiveData<String> newPassword =new MutableLiveData<>();
    public MutableLiveData<String> newPasswordRetype =new MutableLiveData<>();

    public void init(Context context) {
        if (giftList != null && FirebaseAuth.getInstance().getCurrentUser() == null) {
            return;
        }
        giftList = Repo.getInstance(context).getGiftList();
    }

    public LiveData<ArrayList<GiftInfo>> getGiftList() {
        return giftList;
    }

    public void addGift(Context context, GiftInfo gift, Bitmap bitmap) {
        Repo.getInstance(context).addGift(gift, bitmap);
    }

    public void updateGift(Context context, GiftInfo gift, Bitmap bitmap) {
        Repo.getInstance(context).updateGift(gift, bitmap);
    }

    public void deleteGift(Context context, GiftInfo gift) {
        Repo.getInstance(context).deleteGift(gift);
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    public void onChangePasswordClick()
    {
        String crPass=currentPassword.getValue(),newPass = newPassword.getValue(),newPassRetype = newPasswordRetype.getValue();
        if (TextUtils.isEmpty(crPass)||TextUtils.isEmpty(newPass)||TextUtils.isEmpty(newPassRetype)) {
            Toast.makeText(mContext, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!isValidPassword(newPass)) {
            Toast.makeText(mContext, "Mật khẩu phải chứa từ 6 ký tự trở lên", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!newPass.equals(newPassRetype)) {
            Toast.makeText(mContext, "Mật khẩu mới không trùng khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthCredential credential = EmailAuthProvider
                        .getCredential(adminEmail, crPass);

        FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credential)  .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        FirebaseAuth.getInstance().getCurrentUser().updatePassword(newPass).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(mContext, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                mDataLoadListener.onSuccess();
                                return;
                            }
                        });
                    } else {
                        Toast.makeText(mContext, "Sai mật khẩu", Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
            }

    private boolean isValidPassword(final String password) {
        return password.length() >= 6;
    }
}

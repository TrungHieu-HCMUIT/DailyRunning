package com.example.dailyrunningforadmin.viewmodel;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dailyrunningforadmin.utils.LoginNavigator;
import com.google.firebase.auth.FirebaseAuth;

public class LoginViewModel extends ViewModel {
    private Context mContext;
    private static final String adminEmail = "work.trunghieu.0107@gmail.com";

    public MutableLiveData<String> forgotPasswordEmail = new MutableLiveData<>();

    private LoginNavigator mNavigator;

    public void setContext(Context context) {
        this.mContext = context;
    }

    public void setNavigator(LoginNavigator nav) {
        mNavigator = nav;
    }

    public void onForgotPasswordClick(){
        mNavigator.navToForgotPassword();
    }

    public void onSendEmailClick() {
        if(forgotPasswordEmail.getValue()==null) {
            Toast.makeText(mContext, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            return;
        }
        String emailString=forgotPasswordEmail.getValue();
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            Toast.makeText(mContext, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (!forgotPasswordEmail.getValue().equals(adminEmail)) {
            Toast.makeText(mContext, "Không phải email admin, vui lòng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(emailString).addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                Toast.makeText(mContext, "Gửi email thành công", Toast.LENGTH_SHORT).show();
                mNavigator.popBack();
            }
        });
    }
}

package com.example.dailyrunning.Authentication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dailyrunning.R;
import com.example.dailyrunning.Model.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText mEmailEditText;
    private TextInputLayout mEmailTextInputLayout;
    private TextInputEditText mPasswordRetypeTextInputEditText;
    private TextInputLayout mPasswordRetypeTextInputLayout;
    private static final int RC_REGISTER = 2 ;
    private static final String TAG=RegisterActivity.class.getName();
    private TextInputEditText mPasswordEditText;
    private TextInputLayout mPasswordTextInputLayout;
    private Button mRegisterButton;
    private View.OnClickListener mRegisterButtonOnClickListener;
    private FirebaseAuth mFirebaseAuth;
    private Context mContext=RegisterActivity.this;
    private TextInputLayout mDisplayNameTextInputLayout;
    private TextView mLoginClickableTextView;
    private TextInputEditText mDisplayNameTextInputEditText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //init firebaseauth
        mFirebaseAuth=FirebaseAuth.getInstance();
        //getView
        findView();


        textChangeCheck();

        setupOnClickListener();
        mRegisterButton.setOnClickListener(mRegisterButtonOnClickListener);

    }

    private void textChangeCheck() {
        //region password
        //kiểm tra 2 password có giống nhau không
        TextWatcher passwordTextWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //TODO: check password==retype password
                String passwordString= Objects.requireNonNull(mPasswordEditText.getText()).toString().trim();
                String retypePasswordString= Objects.requireNonNull(mPasswordRetypeTextInputEditText.getText()).toString().trim();
                if(!passwordString.equals(retypePasswordString) && !retypePasswordString.isEmpty() && !passwordString.isEmpty())
                {
                    mPasswordRetypeTextInputLayout.setError("Mật khẩu không trùng khớp");
                }
                else
                {
                    mPasswordRetypeTextInputLayout.setError(null);
                }
            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mPasswordRetypeTextInputEditText.addTextChangedListener(passwordTextWatcher);
        mPasswordEditText.addTextChangedListener(passwordTextWatcher);
        //endregion
        //region email

        mEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                String emailString=mEmailEditText.getText().toString().trim();
                if (!(emailString.matches(emailPattern)))
                {
                    // or
                    mEmailTextInputLayout.setError("Email không hợp lệ");
                }
                else
                {
                    mEmailTextInputLayout.setError(null);

                }
            }
        });
        //endregion
    }

    private void findView()
    {
        mEmailTextInputLayout=findViewById(R.id.email_outlinedTextField);
        mEmailEditText=(TextInputEditText) mEmailTextInputLayout.getEditText();
        mPasswordTextInputLayout=findViewById(R.id.password_outlinedTextField);
        mPasswordEditText=(TextInputEditText) mPasswordTextInputLayout.getEditText();
        mPasswordRetypeTextInputLayout=findViewById(R.id.reTypePassword_outlinedTextField);
        mPasswordRetypeTextInputEditText=(TextInputEditText) mPasswordRetypeTextInputLayout.getEditText();
        mDisplayNameTextInputLayout=findViewById(R.id.displayName_outlinedTextField);
        mDisplayNameTextInputEditText=(TextInputEditText)mDisplayNameTextInputLayout.getEditText();
        mRegisterButton=findViewById(R.id.register_button);
        mLoginClickableTextView=findViewById(R.id.loginClickable_textView);
    }
    private boolean validateData(String retypePasswordString, String displayNameString, String emailString, String passwordString)
    {

        if(TextUtils.isEmpty(emailString)||TextUtils.isEmpty(passwordString) ||TextUtils.isEmpty(displayNameString)
        ||TextUtils.isEmpty(retypePasswordString) )
        {
            Toast.makeText(mContext, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }
    private void setupOnClickListener() {
        mRegisterButtonOnClickListener =new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailString= Objects.requireNonNull(mEmailEditText.getText()).toString().trim();
                String passwordString= Objects.requireNonNull(mPasswordEditText.getText()).toString().trim();
                String retypePasswordString= Objects.requireNonNull(mPasswordRetypeTextInputEditText.getText()).toString().trim();
                String displayNameString= Objects.requireNonNull(mDisplayNameTextInputEditText.getText()).toString().trim();
               if(!validateData(emailString,passwordString,retypePasswordString,displayNameString))
               {
                   return;
               }

                mFirebaseAuth.createUserWithEmailAndPassword(emailString,passwordString)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(mContext,"Sign up successfully",Toast.LENGTH_LONG);
                            AuthResult mAuthResult=task.getResult();
                            Intent data=new Intent();
                            FirebaseUser firebaseUser=mAuthResult.getUser();
                            //TODO wait for complete ui then add gender,dob,...;
                            UserInfo newUser=new UserInfo(displayNameString,firebaseUser.getEmail(),0,firebaseUser.getUid(),passwordString);
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(displayNameString).build();
                            firebaseUser.updateProfile(profileUpdates);


                            data.putExtra("newUser",newUser);
                            setResult(RESULT_OK,data);
                            finish();
                        }
                        else
                        {
                            Log.v(TAG,"Error: "+task.getException());
                        }
                    }
                });
            }
        };

        mLoginClickableTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

}

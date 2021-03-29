package com.example.dailyrunning.Authentication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dailyrunning.R;
import com.example.dailyrunning.Model.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText mEmailEditText;
    private static final int RC_REGISTER = 2 ;
    private static final String TAG=RegisterActivity.class.getName();
    private EditText mPasswordEditText;
    private Button mRegisterButton;
    private View.OnClickListener mButtonOnClickListener;
    private FirebaseAuth mFirebaseAuth;
    private Context mContext=RegisterActivity.this;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //init firebaseauth
        mFirebaseAuth=FirebaseAuth.getInstance();
        //getView
        mEmailEditText=findViewById(R.id.email_editText);
        mPasswordEditText=findViewById(R.id.password_editText);
        mRegisterButton=findViewById(R.id.register_button);
        setupOnClickListener();
        mRegisterButton.setOnClickListener(mButtonOnClickListener);

    }

    private void setupOnClickListener() {
        mButtonOnClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailString=mEmailEditText.getText().toString().trim();
                String passwordString=mPasswordEditText.getText().toString().trim();
                if(TextUtils.isEmpty(emailString)||TextUtils.isEmpty(passwordString))
                {
                    Toast.makeText(mContext, "Please enter email and password", Toast.LENGTH_SHORT).show();
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
                            UserInfo newUser=new UserInfo("Test",firebaseUser.getEmail(),0,firebaseUser.getUid(),passwordString);
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
    }

}

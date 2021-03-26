package com.example.dailyrunning.Authentication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dailyrunning.R;
import com.example.dailyrunning.data.UserInfo;
import com.example.dailyrunning.helper.UserViewModel;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_REGISTER = 2 ;
    private static final int RC_SIGN_IN = 1;
    private UserViewModel mUserViewModel;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private UserInfo mCurrentUser;
    private Context mContext=LoginActivity.this;
    private View.OnClickListener mLoginOnClickListener;
    private View.OnClickListener mRegisterOnClickListener;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button loginButton;
    private Button registerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Login");
        mEmailEditText =findViewById(R.id.email_editText);
        mPasswordEditText=findViewById(R.id.password_editText);

        //region firebase
        //init firebase auth
        //init userViewModel
       // mUserViewModel=new ViewModelProvider(this).get(UserViewModel.class);
        mFirebaseAuth= FirebaseAuth.getInstance();
        setUpOnClickListener();
        loginButton=(Button) findViewById(R.id.login_button);
        registerButton=(Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(mRegisterOnClickListener);
        loginButton.setOnClickListener(mLoginOnClickListener);



        //endregion
    }

    private void setUpOnClickListener() {
        //region login
        mLoginOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String emailString=  mEmailEditText.getText().toString();
              String passwordString=mPasswordEditText.getText().toString();
              signInWithEmailAndPassword(emailString,passwordString);
            }
        };

        //endregion

        //region register
        mRegisterOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(mContext,RegisterActivity.class),RC_REGISTER);
            }
        };
        //endregion
    }

    //region signInWithEmailAndPassword
    private void signInWithEmailAndPassword(String email,String password)
    {
        mFirebaseAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if(task.isSuccessful()) {
                //TODO: create userinfo instance and return it back postActitivy
                Intent data=new Intent();
                FirebaseUser firebaseUser= task.getResult().getUser();
                UserInfo currentUser=new UserInfo("testAcc",firebaseUser.getEmail(),0,firebaseUser.getUid(),null);
                data.putExtra("newUser",currentUser);
                setResult(RESULT_OK,data);
                finish();
            }
        }
    });
    }
    //endregion

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_REGISTER) {
            if (resultCode==RESULT_OK) {
                UserInfo newUser = (UserInfo) data.getExtras().getSerializable("newUser");
               signInWithEmailAndPassword(newUser.getEmail(),newUser.getPassword());
            }
        }
    }
}

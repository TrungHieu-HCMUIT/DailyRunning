package com.example.dailyrunning.Authentication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.firebase.ui.auth.data.model.IntentRequiredException;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_REGISTER = 2 ;
    private static final int RC_SIGN_IN = 1;
    private static final int RC_SIGN_IN_GOOGLE = 101;
    private static final String TAG = "LoginActivity";
    private  GoogleSignInClient mGoogleSignInClient;
    private UserViewModel mUserViewModel;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private UserInfo mCurrentUser;
    private Context mContext=LoginActivity.this;
    private View.OnClickListener mLoginOnClickListener;
    private View.OnClickListener mRegisterOnClickListener;
    private View.OnClickListener mLoginWithGoogleListener;

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button loginButton;
    private Button registerButton;
    private Button loginWithGoogleButton;
    //firebase database
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserInfoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //region init firebase database
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mUserInfoRef=mFirebaseDatabase.getReference().child("UserInfo");
        //endregion
        setUpGoogleAuth();
        setTitle("Login");
        mEmailEditText =findViewById(R.id.email_editText);
        mPasswordEditText=findViewById(R.id.password_editText);

        //region firebase
        mFirebaseAuth= FirebaseAuth.getInstance();
        setUpOnClickListener();
        loginButton=(Button) findViewById(R.id.login_button);
        registerButton=(Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(mRegisterOnClickListener);
        loginButton.setOnClickListener(mLoginOnClickListener);
        loginWithGoogleButton=(Button) findViewById(R.id.loginGmail);
        loginWithGoogleButton.setOnClickListener(mLoginWithGoogleListener);



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

        mLoginWithGoogleListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
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

    //region google auth
    private void setUpGoogleAuth()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    private void signInGoogle() {
        mGoogleSignInClient.signOut();
        mGoogleSignInClient.revokeAccess();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                            Intent data=new Intent();
                            UserInfo currentUser=new UserInfo("testAcc",firebaseUser.getEmail(),0,firebaseUser.getUid(),null);
                            mUserInfoRef.child(currentUser.getUserID()).setValue(currentUser);
                            data.putExtra("newUser",currentUser);
                            setResult(RESULT_OK,data);
                            finish();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //updateUI(null);
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
                mUserInfoRef.child(newUser.getUserID()).setValue(newUser);
               signInWithEmailAndPassword(newUser.getEmail(),newUser.getPassword());
            }
        }
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
}

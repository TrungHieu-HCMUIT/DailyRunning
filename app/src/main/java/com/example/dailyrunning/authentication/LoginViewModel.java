package com.example.dailyrunning.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ParseException;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dailyrunning.R;
import com.example.dailyrunning.model.UserInfo;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<UserInfo> mNewUser=new MutableLiveData<>();
    private DatabaseReference mUserInfoRef = FirebaseDatabase.getInstance().getReference().child("UserInfo");
    public MutableLiveData<UserInfo> mRegisterUser=new MutableLiveData<>();


    {
        mRegisterUser.setValue(new UserInfo());
    }

    public MutableLiveData<UserInfo> getNewUser() {

        return mNewUser;
    }

    public CallbackManager mCallbackManager;
    public Boolean isFromRegister = false;
    private LoginNavigator mNavigator;

    public void setNavigator(LoginNavigator nav) {
        mNavigator = nav;
    }

    public void onNormalLoginClick(String email, String password, TaskCallBack mTaskCallBack) {
        if(email.isEmpty() || password.isEmpty()) {
            mTaskCallBack.onError(new Exception("Empty email or password"));
            return;
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser user = task.getResult().getUser();
                        mUserInfoRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.exists()) //user đăng nhập lần đầu
                                {
                                    UserInfo currentUser = new UserInfo(user.getDisplayName(), user.getEmail(), 0, true,
                                            user.getUid(), null, 150, 50, LoginActivity.DEFAULT_AVATAR_URL);
                                    mNewUser.setValue(currentUser);
                                    mNavigator.navToUpdateInfo();

                                } else //user đã đăng nhập trước đó
                                {
                                    mTaskCallBack.onSuccess();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        Log.v("NormalLoginError", task.getException().toString());
                        mTaskCallBack.onError(task.getException());
                    }
                });
    }

    public interface TaskCallBack {
        void onSuccess();

        void onError(Exception exception);
    }


    //region google auth
    public GoogleSignInClient setUpGoogleAuth(Context mContext) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(mContext.getString(R.string.default_web_client_id))
                .requestEmail().requestProfile()
                .build();
        return GoogleSignIn.getClient(mContext, gso);
    }

    public void handleGoogleLoginResult(Intent data, Context mContext, TaskCallBack mGoogleTaskCallBack)
    {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Log.d(getClass().getName(), "firebaseAuthWithGoogle:" + account.getId());

            firebaseAuthWithGoogle(account.getIdToken(),mContext, mGoogleTaskCallBack);
        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            Log.w(getClass().getName(), "Google sign in failed", e);
        }
    }
    private void firebaseAuthWithGoogle(String idToken, Context mContext, TaskCallBack mGoogleTaskCallBack) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener((Activity) mContext, task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser user = task.getResult().getUser();
                        mUserInfoRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.exists()) //user đăng nhập lần đầu
                                {
                                    UserInfo currentUser = new UserInfo(user.getDisplayName(), user.getEmail(), 0, true,
                                            user.getUid(), null, 150, 50, user.getPhotoUrl().toString());
                                    mNewUser.setValue(currentUser);
                                    mNavigator.navToUpdateInfo();

                                } else //user đã đăng nhập trước đó
                                {
                                    mGoogleTaskCallBack.onSuccess();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                        // Sign in success, update UI with the signed-in user's information
                        //updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(this.getClass().getName(), "signInWithCredentialGoogle:failure", task.getException());
                        //updateUI(null);
                    }
                });
    }

    public void onGoogleLoginClick() {
        mNavigator.onGoogleLoginClick();
    }
    //endregion

    //region fb auth
    public void handleFacebookAccessToken(AccessToken token, Activity activity, TaskCallBack mTaskCallBack) {
        Log.d(this.getClass().getName(), "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {


                        // Sign in success, update UI with the signed-in user's information
                        Log.d(this.getClass().getName(), "signInWithCredential:success");

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        mUserInfoRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.exists()) //user đăng nhập lần đầu
                                {
                                    UserInfo currentUser = new UserInfo(user.getDisplayName(), user.getEmail(), 0, true,
                                            user.getUid(), null, 150, 50, null);
                                    mNewUser.setValue(currentUser);
                                    mNavigator.navToUpdateInfo();
                                } else //user đã đăng nhập trước đó
                                {
                                    mTaskCallBack.onSuccess();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                mTaskCallBack.onError(error.toException());
                            }
                        });

                    } else {


                        // If sign in fails, display a message to the user.
                        Log.w(this.getClass().getName(), "signInWithCredential:failure", task.getException());
                        mTaskCallBack.onError(task.getException());

                        //updateUI(null);
                    }
                });
    }
    //endregion

    //region register

    public void onRegisterClick()
    {
        mNavigator.navToRegister();
    }

    public void registerUser(String password, TaskCallBack mTaskCallBack)
    {

        UserInfo registerUser=mRegisterUser.getValue();
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(registerUser.getEmail(), password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        AuthResult mAuthResult = task.getResult();
                        FirebaseUser firebaseUser = mAuthResult.getUser();
                        UserInfo newUser = new UserInfo(registerUser.getDisplayName(), firebaseUser.getEmail(), 0, true,
                                firebaseUser.getUid(), null, 150, 50, LoginActivity.DEFAULT_AVATAR_URL);
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(registerUser.getDisplayName()).setPhotoUri(Uri.parse(LoginActivity.DEFAULT_AVATAR_URL)).build();
                        firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(upTask -> {
                            if (!upTask.isSuccessful())
                                upTask.getException().printStackTrace();
                        });
                        //TODO : nav to update info
                        mNewUser.setValue(newUser);
                        mTaskCallBack.onSuccess();//nav here


                    } else {
                        mTaskCallBack.onError(task.getException());

                    }
                });
    }
    //endregion

    //region update info firebase user

    public void onUpdateInfoClick(TaskCallBack mTaskCallback)
    {
        UserInfo mNewInfo=mNewUser.getValue();
        if (mNewInfo.validateData()) {
            mUserInfoRef.child(mNewInfo.getUserID()).setValue(mNewInfo).addOnCompleteListener(task->{
                if(task.isSuccessful())
                {
                    updateFirebaseUser(mNewInfo);
                    mTaskCallback.onSuccess();


                }
                else if(!task.isSuccessful())
                {
                    mTaskCallback.onError(task.getException());
                }
            });
        }
    }
    private void updateFirebaseUser(UserInfo mNewInfo) {
        FirebaseUser mUser= FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest mRequest=new UserProfileChangeRequest.Builder().setDisplayName(mNewInfo.getDisplayName()).build();
        mUser.updateProfile(mRequest);
    }


    //endregion

}

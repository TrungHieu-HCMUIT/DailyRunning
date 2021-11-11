package com.example.dailyrunning.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.dailyrunning.R;
import com.example.dailyrunning.model.UserInfo;
import com.example.dailyrunning.record.RecordViewModel;
import com.example.dailyrunning.user.UserViewModel;
import com.example.dailyrunning.utils.ConfirmDialog;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.dailyrunning.record.RecordViewModel.bitmapToByteArray;
import static com.example.dailyrunning.user.UserViewModel.EMAIL_PROVIDER_ID;
import static com.example.dailyrunning.user.UserViewModel.FACEBOOK_PROVIDER_ID;
import static com.example.dailyrunning.user.UserViewModel.GOOGLE_PROVIDER_ID;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<UserInfo> mNewUser=new MutableLiveData<>();
    private DatabaseReference mUserInfoRef = FirebaseDatabase.getInstance().getReference().child("UserInfo");
    public MutableLiveData<UserInfo> mRegisterUser=new MutableLiveData<>();
    public MutableLiveData<String> forgotPasswordEmail=new MutableLiveData<>();
    public LoadingDialog loadingDialog;
    public UserViewModel.RunningSnackBar snackBar;
    public RecordViewModel.ShowConfirmDialog confirmDialog;

    {
        mRegisterUser.setValue(new UserInfo());
    }

    public void setLoadingDialog(LoadingDialog loadingDialog)
    {this.loadingDialog=loadingDialog;

    }

    public MutableLiveData<UserInfo> getNewUser() {

        return mNewUser;
    }

    public void onForgotPasswordClick(){
        mNavigator.navToForgotPassword();
    }
    public void onSendEmailClick() {
        if(forgotPasswordEmail.getValue()==null)
            return;
        String emailString=forgotPasswordEmail.getValue();
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            snackBar.showSnackBar("Email không hợp lệ",null);
            return;
        }
        FirebaseAuth.getInstance().sendPasswordResetEmail(emailString).addOnCompleteListener(task -> {
           if(task.isSuccessful())
           {
               snackBar.showSnackBar("Gửi mail thành công!",new Snackbar.Callback() {
                   @Override
                   public void onDismissed(Snackbar snackbar, int event) {
                       super.onDismissed(snackbar, event);
                       mNavigator.popBack();
                   }
               });

           }
           else
           {
                Exception ex=task.getException();
                snackBar.showSnackBar("Không tồn tại người dùng nào với email này!",null);
           }
        });
    }
    public CallbackManager mCallbackManager;
    private LoginNavigator mNavigator;

    public void setNavigator(LoginNavigator nav) {
        mNavigator = nav;
    }

    public void onNormalLoginClick(String email, String password, TaskCallBack mTaskCallBack) {
        if(email.isEmpty() || password.isEmpty()) {
            snackBar.showSnackBar("Vui lòng nhập email và mật khẩu",null);
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            snackBar.showSnackBar("Email không hợp lệ",null);
            return ;
        }
        loadingDialog.showDialog();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        loadingDialog.dismissDialog();

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
                        loadingDialog.dismissDialog();

                        Exception ex=task.getException();
                        Log.v("NormalLoginError", task.getException().toString());
                        mTaskCallBack.onError(task.getException());
                        if (ex instanceof FirebaseAuthInvalidCredentialsException) {
                            snackBar.showSnackBar("Sai mật khẩu",null);
                        } else if (ex instanceof FirebaseAuthInvalidUserException) {
                            snackBar.showSnackBar("Không tồn tại người dùng này",null);
                        }
                    }
                });
    }

    public interface TaskCallBack {
        void onSuccess();

        void onError(Exception exception);
    }
    public interface LoadingDialog
    {
        void showDialog();
        void dismissDialog();
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
            loadingDialog.dismissDialog();
            Log.w(getClass().getName(), "Google sign in failed", e);
        }
    }
    private void firebaseAuthWithGoogle(String idToken, Context mContext, TaskCallBack mGoogleTaskCallBack) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener((Activity) mContext, task -> {
                    if (task.isSuccessful()) {
                        loadingDialog.dismissDialog();
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

        loadingDialog.showDialog();
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

                        loadingDialog.dismissDialog();

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
                        mNewUser.setValue(newUser);
                        mTaskCallBack.onSuccess();//nav here


                    } else {
                        mTaskCallBack.onError(task.getException());

                    }
                });
    }
    //endregion

    //region update info firebase user

    public void onUpdateInfoClick(TaskCallBack mTaskCallback,Context context)
    {
        UserInfo mNewInfo=mNewUser.getValue();
        if (mNewInfo.validateData()) {
            mUserInfoRef.child(mNewInfo.getUserID()).setValue(mNewInfo).addOnCompleteListener(task->{
                if(task.isSuccessful())
                {
                    updateFirebaseUser(mNewInfo,context);
                    mTaskCallback.onSuccess();
                }
                else if(!task.isSuccessful())
                {
                    mTaskCallback.onError(task.getException());
                }
            });
        }
        else{
            Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        }
    }
    private void updateFirebaseUser(UserInfo mNewInfo, Context context) {
        FirebaseUser userInfo=FirebaseAuth.getInstance().getCurrentUser();
        switch (userInfo.getProviderData().get(1).getProviderId()) {
            case EMAIL_PROVIDER_ID:
            case GOOGLE_PROVIDER_ID:
                Uri avatarUrl=userInfo.getPhotoUrl();
                Glide.with(context).asBitmap().load(avatarUrl).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        putAvatarToFireStorage(resource,mNewInfo);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
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
                            "/" + fbUID + "/picture?redirect=0&type=normal",
                            response -> {
                                JSONObject res = response.getJSONObject();
                                try {
                                    Uri avatarUri = Uri.parse(res.getJSONObject("data").getString("url"));
                                    Glide.with(context).asBitmap().load(avatarUri).into(new CustomTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                            putAvatarToFireStorage(resource,mNewInfo);
                                        }

                                        @Override
                                        public void onLoadCleared(@Nullable Drawable placeholder) {
                                        }
                                    });

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });

                    request.executeAsync();
                }
                break;

        }

    }

    public void putAvatarToFireStorage(Bitmap image,UserInfo newInfo) {

        FirebaseUser userInfo = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference mAvatarStorageReference = FirebaseStorage.getInstance().getReference().child("avatar_photos");

        //tạo ref mới trong folder avatar_photos/
        StorageReference photoRef = mAvatarStorageReference.child(userInfo.getUid());
        //up hình lên
        photoRef.putBytes(bitmapToByteArray(image)).addOnSuccessListener(taskSnapshot -> photoRef.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Uri userAvatarUri = uri;
                    //update avatarURI trong UserInfo
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(userInfo.getUid());
                    userRef.child("avatarURI").setValue(userAvatarUri.toString());
                    //update profile của firebase user
                    UserProfileChangeRequest mRequest=new UserProfileChangeRequest.Builder()
                            .setPhotoUri(userAvatarUri)
                            .setDisplayName(newInfo.getDisplayName()).build();
                    userInfo.updateProfile(mRequest);
                }));
    }

    //endregion

}

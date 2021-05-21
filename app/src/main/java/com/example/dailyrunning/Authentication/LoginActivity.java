package com.example.dailyrunning.Authentication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.dailyrunning.R;
import com.example.dailyrunning.Model.UserInfo;
import com.example.dailyrunning.Utils.LoginViewModel;
import com.example.dailyrunning.Utils.UserViewModel;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/*
* Q7UH R5LF EJ6M NRXE YMG4 Y3Y3 ILF6 QDAN
*   100053689872926
Nxhung2
 * */

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel mLoginViewModel;
    public static final String DEFAULT_AVATAR_URL = "https://firebasestorage.googleapis.com/v0/b/dailyrunning-6e8e9.appspot.com/o/avatar_photos%2Fsbcf-default-avatar%5B1%5D.png?alt=media&token=ec7c1fcd-9fc8-415f-b2ec-51ffb03867a3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoginViewModel=new ViewModelProvider(this).get(LoginViewModel.class);


        //endregion
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //FB login
        mLoginViewModel.mCallbackManager.onActivityResult(requestCode, resultCode, data);
        //
        super.onActivityResult(requestCode, resultCode, data);
    }
    



}

/*
                                                    //region up avt len firebase
                                                    StorageReference avtRef = FirebaseStorage.getInstance().getReference().child("avatar_photos").child(fbUID);
                                                    avtRef.putFile(Uri.parse(avatarUrl)).addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            avtRef.getDownloadUrl()
                                                                    .addOnSuccessListener(uri -> {
                                                                        Uri userAvatarUri = uri;
                                                                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(user.getUid());
                                                                        userRef.child("avatarURI").setValue(userAvatarUri.toString());
                                                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(userAvatarUri).build();
                                                                        user.updateProfile(profileUpdates);
                                                                    });
                                                        }
                                                        else
                                                        {
                                                            task1.getException().printStackTrace();
                                                        }
                                                    });

                                                    //endregion
*/

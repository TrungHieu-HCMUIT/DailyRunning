package com.example.dailyrunning.authentication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dailyrunning.model.UserInfo;
import com.example.dailyrunning.R;
import com.example.dailyrunning.utils.LoginViewModel;
import com.example.dailyrunning.utils.UserViewModel;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.app.Activity.RESULT_OK;


public class LoginFragment extends Fragment {

    private View rootView;
    private static final int RC_REGISTER = 2;
    private static final int RC_SIGN_IN = 1;
    private static final int RC_SIGN_IN_GOOGLE = 101;
    private static final String TAG = "LoginActivity";
    private GoogleSignInClient mGoogleSignInClient;
    private UserViewModel mUserViewModel;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private UserInfo mCurrentUser;
    private View.OnClickListener mLoginOnClickListener;
    private View.OnClickListener mRegisterOnClickListener;
    private View.OnClickListener mLoginWithGoogleListener;
    private View.OnClickListener mLoginWithFacebookListener;
    private LoginViewModel mLoginViewModel;
    public static final String DEFAULT_AVATAR_URL = "https://firebasestorage.googleapis.com/v0/b/dailyrunning-6e8e9.appspot.com/o/avatar_photos%2Fsbcf-default-avatar%5B1%5D.png?alt=media&token=ec7c1fcd-9fc8-415f-b2ec-51ffb03867a3";

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button loginButton;
    private TextView registerButton;
    private Button loginWithGoogleButton;
    private Button loginWithFacebookButton;
    private com.facebook.login.widget.LoginButton realLoginWithFacebookButton;

    //firebase database
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserInfoRef;

    //facebook callback manager
    private CallbackManager mCallbackManager;

    //timeout var
    private AtomicBoolean loginTimeOut;
    private static final String EMAIL = "email";
    private NavController mNavController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
        loginTimeOut = new AtomicBoolean(false);

        mNavController = Navigation.findNavController(getActivity(), R.id.login_fragment_container);
        mLoginViewModel = new ViewModelProvider(getActivity()).get(LoginViewModel.class);
        //region init firebase database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserInfoRef = mFirebaseDatabase.getReference().child("UserInfo");
        //endregion
        setUpGoogleAuth();
        mEmailEditText = rootView.findViewById(R.id.email_editText);
        mPasswordEditText = rootView.findViewById(R.id.password_editText);

        setUpOnClickListener();

        //region firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        loginButton = rootView.findViewById(R.id.login_button);
        registerButton = rootView.findViewById(R.id.registerClickable_textView);
        registerButton.setOnClickListener(mRegisterOnClickListener);
        loginButton.setOnClickListener(mLoginOnClickListener);
        loginWithGoogleButton = rootView.findViewById(R.id.loginGmail);
        loginWithGoogleButton.setOnClickListener(mLoginWithGoogleListener);
        loginWithFacebookButton = rootView.findViewById(R.id.loginFacebook);
        loginWithFacebookButton.setOnClickListener(mLoginWithFacebookListener);
        realLoginWithFacebookButton = rootView.findViewById(R.id.loginFacebookReal);
        realLoginWithFacebookButton.setPermissions(Arrays.asList(EMAIL));
        //init callbackmanager
        setUpFacebookAuth();

        //endregion
        if (mLoginViewModel.isFromRegister) {
            mEmailEditText.setText(mLoginViewModel.tempUser.getEmail());
            mLoginViewModel.getNewUser().setValue(null);
            mLoginViewModel.isFromRegister = false;
        }
        mLoginViewModel.getNewUser().observe(getActivity(), newUser -> {
            if (newUser != null) {
                if (newUser.getDob() == null || newUser.getHeight() == 0 || newUser.getWeight() == 0 || newUser.getDisplayName() == null) {

                    if (mNavController.getCurrentDestination().getId() == R.id.loginFragment)
                        mNavController.navigate(R.id.action_loginFragment_to_registerAddInfoFragment2);

                } else {
                    if (!mLoginViewModel.isFromRegister) {

                        Intent data = new Intent();
                        data.putExtra("newUser", newUser);
                        getActivity().setResult(RESULT_OK, data);
                        getActivity().finish();
                    }
                }
            }
        });
    }


    private void setUpOnClickListener() {
        //region login
        mLoginOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailString = mEmailEditText.getText().toString();
                String passwordString = mPasswordEditText.getText().toString();
                signInWithEmailAndPassword(emailString, passwordString);
            }
        };

        mLoginWithFacebookListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realLoginWithFacebookButton.performClick();
            }
        };
        mLoginWithGoogleListener = new View.OnClickListener() {
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
                mNavController.navigate(R.id.action_loginFragment_to_registerFragment);
            }
        };
        //endregion

    }


    //region showDialog message

    private void showDialog(String title, String message) {
        new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)


                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    //endregion

    //region signInWithEmailAndPassword

    private void signInWithEmailAndPassword(String email, String password) {
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        loginTimeOut.set(true);

                        FirebaseUser user = task.getResult().getUser();
                        getUserInfo(user);



                    } else {
                        Log.v("Wrongpass", task.getException().toString());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            showDialog("Lỗi đăng nhập", "Sai mật khẩu");
                        } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            showDialog("Lỗi đăng nhập", "Không tồn tại người dùng này");
                        }
                    }
                });
    }
    //endregion

    //region google auth
    private void setUpGoogleAuth() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
    }

    private void signInGoogle() {
        mGoogleSignInClient.signOut();
        mGoogleSignInClient.revokeAccess();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);

    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        checkTimeOut();

        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        loginTimeOut.set(true);

                        FirebaseUser user = task.getResult().getUser();
                        mUserInfoRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.exists()) //user đăng nhập lần đầu
                                {
                                    UserInfo currentUser = new UserInfo(user.getDisplayName(), user.getEmail(), 0, 0,
                                            user.getUid(), null, 0, 0, user.getPhotoUrl().toString());
                                    mUserInfoRef.child(currentUser.getUserID()).setValue(currentUser)
                                            .addOnCompleteListener(putUserTask->{
                                                if(putUserTask.isSuccessful())
                                                    getUserInfo(user);
                                                else
                                                    putUserTask.getException().printStackTrace();

                                            });

                                } else //user đã đăng nhập trước đó
                                {
                                    getUserInfo(user);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });



                        // Sign in success, update UI with the signed-in user's information
                        //updateUI(user);
                    } else {
                        loginTimeOut.set(false);
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        //updateUI(null);
                    }
                });
    }
    //endregion

    //region facebook auth
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        checkTimeOut();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {

                        loginTimeOut.set(true);

                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");

                        FirebaseUser user = mFirebaseAuth.getCurrentUser();
                        mUserInfoRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.exists()) //user đăng nhập lần đầu
                                {
                                    UserInfo currentUser = new UserInfo(user.getDisplayName(), user.getEmail(), 0, 0,
                                            user.getUid(), null, 0, 0, null);
                                    mUserInfoRef.child(currentUser.getUserID()).setValue(currentUser)
                                            .addOnCompleteListener(putUserTask->{
                                                if(putUserTask.isSuccessful())
                                                    getUserInfo(user);
                                                else
                                                    putUserTask.getException().printStackTrace();

                                            });

                                } else //user đã đăng nhập trước đó
                                {
                                    getUserInfo(user);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    } else {
                        loginTimeOut.set(false);

                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(getActivity(), "Authentication failed, time out",
                                Toast.LENGTH_SHORT).show();
                        //updateUI(null);
                    }
                });
    }

    private void setUpFacebookAuth() {
        mCallbackManager = CallbackManager.Factory.create();
        mLoginViewModel.mCallbackManager = mCallbackManager;


        // Callback registration
        realLoginWithFacebookButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });


    }
    //endregion

    //region isNetworkAvailable
    private void checkTimeOut() {
        if (isNetworkAvailable()) {


            Handler handler = new Handler();
            Runnable timerTask = () -> {
                if (!loginTimeOut.get()) { //  Timeout

                    // Your timeout code goes here
                    Log.i(TAG, "Connection timed out");
                    new AlertDialog.Builder(getActivity()).setMessage("Connection timed out").create().show();

                }
            };
            // Setting timeout of 10 sec for the request
            handler.postDelayed(timerTask, 10000);
        } else {
            // Internet not available
            Log.i(TAG, "No internet available");
        }
    }

    private boolean isNetworkAvailable() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        return false;
    }

    //endregion

    private void getUserInfo(FirebaseUser user)
    {
        mUserInfoRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserInfo currentUser =snapshot.getValue(UserInfo.class);
                mLoginViewModel.tempUser=currentUser;
                mLoginViewModel.getNewUser().setValue(currentUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //
        super.onActivityResult(requestCode, resultCode, data);
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
package com.example.dailyrunning.authentication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.dailyrunning.R;
import com.example.dailyrunning.databinding.FragmentLoginBinding;
import com.example.dailyrunning.user.UserViewModel;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.app.Activity.RESULT_OK;


public class LoginFragment extends Fragment implements LoginNavigator{

    private View rootView;
    private static final int RC_REGISTER = 2;
    private static final int RC_SIGN_IN = 1;
    private static final int RC_SIGN_IN_GOOGLE = 101;
    private static final String TAG = "LoginActivity";
    private GoogleSignInClient mGoogleSignInClient;
    private UserViewModel mUserViewModel;
    private FirebaseAuth mFirebaseAuth;
    private View.OnClickListener mRegisterOnClickListener;

    private LoginViewModel mLoginViewModel;
    public static final String DEFAULT_AVATAR_URL = "https://firebasestorage.googleapis.com/v0/b/dailyrunning-6e8e9.appspot.com/o/avatar_photos%2Fsbcf-default-avatar%5B1%5D.png?alt=media&token=ec7c1fcd-9fc8-415f-b2ec-51ffb03867a3";



    //firebase database
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserInfoRef;

    //facebook callback manager
    private CallbackManager mCallbackManager;

    //timeout var
    private AtomicBoolean loginTimeOut;
    private static final String EMAIL = "email";
    private NavController mNavController;
    private FragmentLoginBinding binding;
    private LoginActivity mParent;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentLoginBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mParent= (LoginActivity) getActivity();
        rootView = view;
        loginTimeOut = new AtomicBoolean(false);

        mNavController = Navigation.findNavController(getActivity(), R.id.login_fragment_container);
        mLoginViewModel = new ViewModelProvider(getActivity()).get(LoginViewModel.class);
        binding.setLoginViewModel(mLoginViewModel);
        binding.setLifecycleOwner(getActivity());
        mLoginViewModel.setNavigator(this);
        //region init firebase database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserInfoRef = mFirebaseDatabase.getReference().child("UserInfo");
        //endregion
        mGoogleSignInClient=mLoginViewModel.setUpGoogleAuth(getActivity());


        setUpOnClickListener();


        //region firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        binding.loginFacebookReal.setPermissions(Arrays.asList(EMAIL));
        //init callbackmanager
        setUpFacebookAuth();

        //endregion

    }

    private void completeLogin()
    {
        getActivity().setResult(RESULT_OK);
        mParent.finish();
    }
    private void setUpOnClickListener() {
        //region login
        binding.loginButton.setOnClickListener( v -> mLoginViewModel.onNormalLoginClick(binding.emailEditText.getText().toString()
                , binding.passwordEditText.getText().toString(), new LoginViewModel.TaskCallBack() {
                    @Override
                    public void onSuccess() {
                        completeLogin();
                    }

                    @Override
                    public void onError(Exception exception) {
                    }
                })
        );



        //endregion


    }


    //region showDialog message

  /*  private void showDialog(String title, String message) {
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
    }*/
    //endregion




    //region facebook auth


    private void setUpFacebookAuth() {
        mCallbackManager = CallbackManager.Factory.create();
        mLoginViewModel.mCallbackManager = mCallbackManager;


        // Callback registration
        binding.loginFacebookReal.setOnClickListener(v->{
            mLoginViewModel.loadingDialog.showDialog();
        });
        binding.loginFacebookReal.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                mLoginViewModel.handleFacebookAccessToken(loginResult.getAccessToken(),getActivity() ,new LoginViewModel.TaskCallBack() {
                    @Override
                    public void onSuccess() {
                        completeLogin();
                    }

                    @Override
                    public void onError(Exception exception) {
                        Log.e(getClass().getName(),exception.toString());
                    }
                });

            }

            @Override
            public void onCancel() {
                // App code
                mLoginViewModel.loadingDialog.dismissDialog();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                mLoginViewModel.loadingDialog.dismissDialog();
            }
        });


    }
    //endregion

    //region isNetworkAvailable

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



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN_GOOGLE) {
           mLoginViewModel.handleGoogleLoginResult(data, getActivity(), new LoginViewModel.TaskCallBack() {
               @Override
               public void onSuccess() {
                   completeLogin();
               }

               @Override
               public void onError(Exception exception) {
                   Log.e(getClass().getName(),exception.toString());
               }
           });
        }
    }


    @Override
    public void onGoogleLoginClick() {
        mGoogleSignInClient.signOut();
        mGoogleSignInClient.revokeAccess();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
    }

    @Override
    public void navToRegister() {
        mNavController.navigate(R.id.action_loginFragment_to_registerFragment);
    }

    @Override
    public void navToUpdateInfo() {
        mNavController.navigate(R.id.action_loginFragment_to_registerAddInfoFragment2);
    }

    @Override
    public void navToForgotPassword() {
        mNavController.navigate(R.id.action_loginFragment_to_forgotPasswordFragment);

    }

    @Override
    public void popBack() {
        mNavController.popBackStack();
    }
}
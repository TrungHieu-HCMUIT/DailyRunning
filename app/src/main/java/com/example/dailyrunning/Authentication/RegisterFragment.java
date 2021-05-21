package com.example.dailyrunning.Authentication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dailyrunning.Model.UserInfo;
import com.example.dailyrunning.R;
import com.example.dailyrunning.Utils.LoginViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;


public class RegisterFragment extends Fragment {
    private TextInputEditText mEmailEditText;
    private TextInputLayout mEmailTextInputLayout;
    private TextInputEditText mPasswordRetypeTextInputEditText;
    private TextInputLayout mPasswordRetypeTextInputLayout;
    private static final int RC_REGISTER = 2 ;
    private static final String TAG=RegisterFragment.class.getName();
    private TextInputEditText mPasswordEditText;
    private TextInputLayout mPasswordTextInputLayout;
    private Button mRegisterButton;
    private View.OnClickListener mRegisterButtonOnClickListener;
    private FirebaseAuth mFirebaseAuth;
    private TextInputLayout mDisplayNameTextInputLayout;
    private TextView mLoginClickableTextView;
    private TextInputEditText mDisplayNameTextInputEditText;
    private View rootView;
    private LoginViewModel mLoginViewModel;
    private NavController mNavController;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull  View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView=view;
        mLoginViewModel=new ViewModelProvider(getActivity()).get(LoginViewModel.class);
        //init firebaseauth
        mFirebaseAuth=FirebaseAuth.getInstance();
        mNavController= Navigation.findNavController(getActivity(),R.id.login_fragment_container);
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
                String emailString=mEmailEditText.getText().toString().trim();
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailString).matches())
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
        mEmailTextInputLayout=rootView.findViewById(R.id.email_outlinedTextField);
        mEmailEditText=(TextInputEditText) mEmailTextInputLayout.getEditText();
        mPasswordTextInputLayout=rootView.findViewById(R.id.password_outlinedTextField);
        mPasswordEditText=(TextInputEditText) mPasswordTextInputLayout.getEditText();
        mPasswordRetypeTextInputLayout=rootView.findViewById(R.id.reTypePassword_outlinedTextField);
        mPasswordRetypeTextInputEditText=(TextInputEditText) mPasswordRetypeTextInputLayout.getEditText();
        mDisplayNameTextInputLayout=rootView.findViewById(R.id.displayName_outlinedTextField);
        mDisplayNameTextInputEditText=(TextInputEditText)mDisplayNameTextInputLayout.getEditText();
        mRegisterButton=rootView.findViewById(R.id.register_button);
        mLoginClickableTextView=rootView.findViewById(R.id.loginClickable_textView);
    }
    private boolean validateData(String retypePasswordString, String displayNameString, String emailString, String passwordString)
    {

        if(TextUtils.isEmpty(emailString)||TextUtils.isEmpty(passwordString) ||TextUtils.isEmpty(displayNameString)
                ||TextUtils.isEmpty(retypePasswordString) )
        {
            Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mDisplayNameTextInputLayout.getError()!=null || mEmailTextInputLayout.getError()!=null
                ||mPasswordRetypeTextInputLayout.getError()!=null ||mPasswordTextInputLayout.getError()!=null)
            return false;


        return true;
    }
    private void setupOnClickListener() {
        mRegisterButtonOnClickListener = v -> {
            String emailString= Objects.requireNonNull(mEmailEditText.getText()).toString().trim();
            String passwordString= Objects.requireNonNull(mPasswordEditText.getText()).toString().trim();
            String retypePasswordString= Objects.requireNonNull(mPasswordRetypeTextInputEditText.getText()).toString().trim();
            String displayNameString= Objects.requireNonNull(mDisplayNameTextInputEditText.getText()).toString().trim();
            if(!validateData(emailString,passwordString,retypePasswordString,displayNameString))
            {
                return;
            }

            mFirebaseAuth.createUserWithEmailAndPassword(emailString,passwordString)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(getContext(),"Sign up successfully",Toast.LENGTH_LONG);
                            AuthResult mAuthResult=task.getResult();
                            FirebaseUser firebaseUser=mAuthResult.getUser();
                            //TODO wait for complete ui then add gender,dob,...;
                            UserInfo newUser = new UserInfo(displayNameString, firebaseUser.getEmail(), 0, 0,
                                    firebaseUser.getUid(), null, 0, 0, LoginActivity.DEFAULT_AVATAR_URL);
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayNameString).setPhotoUri(Uri.parse(LoginActivity.DEFAULT_AVATAR_URL)).build();
                            firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(upTask->{
                                if (!upTask.isSuccessful())
                                    upTask.getException().printStackTrace();
                            });
                            mLoginViewModel.isFromRegister=true;
                            mLoginViewModel.tempUser=newUser;
                            mNavController.navigate(R.id.action_registerFragment_to_registerAddInfoFragment);
                        }
                        else
                        {
                            if(task.getException() instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                                Toast.makeText(getContext(), "Email này đã tồn tại", Toast.LENGTH_SHORT).show();
                            }
                            Log.v(TAG,"Error: "+task.getException());
                        }
                    });
        };

        mLoginClickableTextView.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });
    }
}
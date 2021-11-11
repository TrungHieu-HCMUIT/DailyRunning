package com.example.dailyrunning.authentication;

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
import android.widget.Toast;

import com.example.dailyrunning.databinding.FragmentRegisterBinding;
import com.example.dailyrunning.model.UserInfo;
import com.example.dailyrunning.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterFragment extends Fragment {

    private static final int RC_REGISTER = 2;
    private static final String TAG = RegisterFragment.class.getName();

    private View.OnClickListener mRegisterButtonOnClickListener;
    private FirebaseAuth mFirebaseAuth;

    private View rootView;
    private LoginViewModel mLoginViewModel;
    private NavController mNavController;
    private FragmentRegisterBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
        mLoginViewModel = new ViewModelProvider(getActivity()).get(LoginViewModel.class);
        binding.setLoginViewModel(mLoginViewModel);
        binding.setLifecycleOwner(getActivity());

        //init firebaseauth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mNavController = Navigation.findNavController(getActivity(), R.id.login_fragment_container);
        //getView
        textChangeCheck();

        setupOnClickListener();
        binding.registerButton.setOnClickListener(mRegisterButtonOnClickListener);


    }

    static public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        Log.v("Password Validate",String.valueOf(matcher.matches()));
        return matcher.matches();

    }
    private void textChangeCheck() {
        //region password
        //kiểm tra 2 password có giống nhau không
        TextWatcher passwordTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //TODO: check password==retype password
                String passwordString = Objects.requireNonNull(binding.passwordOutlinedTextField.getEditText().getText()).toString().trim();
                String retypePasswordString = Objects.requireNonNull(binding.reTypePasswordOutlinedTextField.getEditText().getText()).toString().trim();
                if (!passwordString.equals(retypePasswordString) && !retypePasswordString.isEmpty() && !passwordString.isEmpty()) {
                    binding.reTypePasswordOutlinedTextField.setError("Mật khẩu không trùng khớp");
                }
                else {
                    binding.reTypePasswordOutlinedTextField.setError(null);
                }

                if(passwordString.isEmpty())
                {
                    binding.passwordOutlinedTextField.setError(null);
                    return;
                }

                if(!isValidPassword(passwordString))
                {
                    binding.passwordOutlinedTextField.setError("Mật khẩu không hợp lệ");
                }
                else if(isValidPassword(passwordString))
                {
                    binding.passwordOutlinedTextField.setError(null);
                }
            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        binding.reTypePasswordOutlinedTextField.getEditText().addTextChangedListener(passwordTextWatcher);
        binding.passwordOutlinedTextField.getEditText().addTextChangedListener(passwordTextWatcher);
        //endregion
        //region email

        binding.emailOutlinedTextField.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String emailString = binding.emailOutlinedTextField.getEditText().getText().toString().trim();
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
                    // or
                    binding.emailOutlinedTextField.setError("Email không hợp lệ");
                } else {
                    binding.emailOutlinedTextField.setError(null);

                }
            }
        });
        //endregion
    }

    private boolean validateData(String retypePasswordString, String displayNameString, String emailString, String passwordString) {

        if (TextUtils.isEmpty(emailString) || TextUtils.isEmpty(passwordString) || TextUtils.isEmpty(displayNameString)
                || TextUtils.isEmpty(retypePasswordString)) {
            Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.emailOutlinedTextField.getError() != null
                || binding.passwordOutlinedTextField.getError() != null || binding.reTypePasswordOutlinedTextField.getError() != null)
            return false;


        return true;
    }

    private void setupOnClickListener() {
        mRegisterButtonOnClickListener = v -> {
            UserInfo mRegisterUser = mLoginViewModel.mRegisterUser.getValue();
            String passwordString = Objects.requireNonNull(binding.passwordOutlinedTextField.getEditText().getText()).toString().trim();
            String retypePasswordString = Objects.requireNonNull(binding.reTypePasswordOutlinedTextField.getEditText().getText()).toString().trim();
            if (!validateData(mRegisterUser.getEmail(), passwordString, retypePasswordString, mRegisterUser.getDisplayName())) {
                return;
            }
            mLoginViewModel.registerUser(passwordString, new LoginViewModel.TaskCallBack() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getContext(), "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                    mNavController.navigate(R.id.action_registerFragment_to_registerAddInfoFragment);
                }

                @Override
                public void onError(Exception exception) {
                    if (exception instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                        Toast.makeText(getContext(), "Email này đã tồn tại", Toast.LENGTH_SHORT).show();
                    }
                    Log.v(TAG, "Error: " + exception);
                }
            });


        };

        binding.loginClickableTextView.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });
    }
}
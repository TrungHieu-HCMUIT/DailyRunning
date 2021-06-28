package com.example.dailyrunningforadmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dailyrunningforadmin.databinding.FragmentLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import static android.app.Activity.RESULT_OK;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    private FragmentLoginBinding binding;

    private static final String adminEmail = "work.trunghieu.0107@gmail.com";

    private static final int EMPTY_EMAIL = 1;
    private static final int EMPTY_PASSWORD = 2;
    private static final int WRONG_EMAIL = 3;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpOnClickListener();
    }

    private void setUpOnClickListener() {
        binding.loginButton.setOnClickListener(v -> handleLogInEvent(binding.emailEditText.getText().toString(),
                binding.passwordEditText.getText().toString()));
    }

    private void handleLogInEvent(String email, String password) {

        switch (isValidInput(email, password)) {
            case EMPTY_EMAIL:
                Toast.makeText(getContext(), "Vui lòng nhập địa chỉ email", Toast.LENGTH_LONG).show();
                return;
            case EMPTY_PASSWORD:
                Toast.makeText(getContext(), "Vui lòng nhập mật khẩu", Toast.LENGTH_LONG).show();
                return;
            case WRONG_EMAIL:
                Toast.makeText(getContext(), "Tài khoản không dành cho admin", Toast.LENGTH_LONG).show();
                return;
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                completeLogIn();
            }
            else {
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                    Toast.makeText(getContext(), "Sai mật khẩu", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void completeLogIn() {
        getActivity().setResult(RESULT_OK);
        getActivity().finish();
    }

    private int isValidInput(String email, String password) {
        if (email.isEmpty())
            return EMPTY_EMAIL;
        else if (password.isEmpty())
            return EMPTY_PASSWORD;
        else if (!email.equals(adminEmail))
            return WRONG_EMAIL;
        return 0;
    }
}

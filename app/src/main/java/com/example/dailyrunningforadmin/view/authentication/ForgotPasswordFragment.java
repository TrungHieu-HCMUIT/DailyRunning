package com.example.dailyrunningforadmin.view.authentication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.example.dailyrunningforadmin.databinding.FragmentForgotPasswordBinding;
import com.example.dailyrunningforadmin.databinding.FragmentLoginBinding;
import com.example.dailyrunningforadmin.viewmodel.LoginViewModel;

public class ForgotPasswordFragment extends Fragment {

    private FragmentForgotPasswordBinding binding;
    private LoginViewModel mLoginViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentForgotPasswordBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLoginViewModel = new ViewModelProvider(getActivity()).get(LoginViewModel.class);
        mLoginViewModel.setContext(getContext());

        binding.setLoginViewModel(mLoginViewModel);
        binding.setLifecycleOwner((LifecycleOwner) getContext());
    }

}

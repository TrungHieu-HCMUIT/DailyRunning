package com.example.dailyrunning.authentication;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dailyrunning.R;
import com.example.dailyrunning.databinding.FragmentForgotPasswordBinding;

import org.jetbrains.annotations.NotNull;


public class ForgotPasswordFragment extends Fragment {


    FragmentForgotPasswordBinding binding;
    LoginViewModel mLoginViewModel;
    Context mContext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentForgotPasswordBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext=getContext();
        mLoginViewModel=new ViewModelProvider((ViewModelStoreOwner) mContext).get(LoginViewModel.class);
        binding.setLoginViewModel(mLoginViewModel);
        binding.setLifecycleOwner((LifecycleOwner) mContext);
    }
}
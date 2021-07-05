package com.example.dailyrunningforadmin.view.authentication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.example.dailyrunningforadmin.databinding.ActivityChangePasswordBinding;
import com.example.dailyrunningforadmin.utils.DataLoadListener;
import com.example.dailyrunningforadmin.viewmodel.HomeViewModel;

public class ChangePasswordFragment extends AppCompatActivity implements DataLoadListener {

    private ActivityChangePasswordBinding binding;
    private HomeViewModel mHomeViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mHomeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        mHomeViewModel.setContext(this);

        binding.setHomeViewModel(mHomeViewModel);
        binding.setLifecycleOwner(this);
    }

    @Override
    public void onGiftLoaded() {
        finish();
    }

    @Override
    public void onSuccess() {

    }
}

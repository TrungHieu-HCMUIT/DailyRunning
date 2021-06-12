package com.example.dailyrunning.home.find;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dailyrunning.R;
import com.example.dailyrunning.databinding.FragmentOtherUserStatisticalBinding;
import com.example.dailyrunning.databinding.FragmentStatisticalBinding;
import com.example.dailyrunning.user.StatisticalFragment;
import com.example.dailyrunning.user.UserViewModel;

import org.jetbrains.annotations.NotNull;


public class OtherUserStatisticalFragment extends Fragment {

    private FragmentOtherUserStatisticalBinding binding;
    private OtherUserProfileViewModel mOtherUserProfileViewModel;
    private int currentPage;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOtherUserStatisticalBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mOtherUserProfileViewModel = new ViewModelProvider(requireActivity()).get(OtherUserProfileViewModel.class);
        binding.setOtherUserViewModel(mOtherUserProfileViewModel);
        binding.setLifecycleOwner(requireActivity());
        currentPage = getArguments().getInt("position");
        binding.setCurrentPage(currentPage);
    }

    public static Fragment newInstance(int position) {
        OtherUserStatisticalFragment mCurrentFragment = new OtherUserStatisticalFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        mCurrentFragment.setArguments(args);
        return mCurrentFragment;
    }
}
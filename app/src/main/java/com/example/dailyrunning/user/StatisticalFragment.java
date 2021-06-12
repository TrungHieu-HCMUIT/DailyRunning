package com.example.dailyrunning.user;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.dailyrunning.R;
import com.example.dailyrunning.databinding.FragmentStatisticalBinding;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StatisticalFragment extends Fragment {


    private final int STATISTICAL_WEEK = 0;
    private final int STATISTICAL_MONTH = 1;
    private final int STATISTICAL_YEAR = 2;
    private FragmentStatisticalBinding binding;
    private StatisticalViewModel statisticalViewModel;
    private int currentPage;
    private String userID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //region get View
        binding = FragmentStatisticalBinding.inflate(inflater, container, false);
        //endregion

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        statisticalViewModel = new ViewModelProvider(requireActivity()).get(StatisticalViewModel.class);
        binding.setStatisticalViewModel(statisticalViewModel);
        binding.setLifecycleOwner(requireActivity());
        currentPage = getArguments().getInt("position");
        userID = getArguments().getString("uid");
        Log.d("StatisticalFragment", "onViewCreated: " + userID);
        binding.setCurrentPage(currentPage);
        setData();

    }

    public static Fragment newInstance(int position, String uid) {
        StatisticalFragment mCurrentFragment = new StatisticalFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("uid", uid);
        Log.d("StatisticalFragment", "newInstance: " + uid);
        mCurrentFragment.setArguments(args);
        return mCurrentFragment;
    }

    private void setData() {
        statisticalViewModel.fetchActivities(userID);
    }
}

package com.example.dailyrunning.Home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dailyrunning.R;
import com.flyco.tablayout.SegmentTabLayout;

public class HomeFollowingFragment extends Fragment {

    private SegmentTabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_following, container, false);

        tabLayout = (SegmentTabLayout) view.findViewById(R.id.tabLayout);

        return view;
    }
}
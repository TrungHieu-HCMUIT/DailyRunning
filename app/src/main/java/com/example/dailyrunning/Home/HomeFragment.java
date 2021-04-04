package com.example.dailyrunning.Home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dailyrunning.R;
import com.flyco.tablayout.SegmentTabLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;

public class HomeFragment extends Fragment {
    private AppBarLayout appBarLayout;
    private SegmentTabLayout tabLayout;

    private String[] mTitles = {"Đang theo dõi", "Bạn"};
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tabLayout = (SegmentTabLayout) view.findViewById(R.id.tabLayout);
        tabLayout.setTabData(mTitles);

        appBarLayout = (AppBarLayout) view.findViewById(R.id.topAppBar);

        exposeTabLayoutWhenCollapsed();

        return view;
    }

    private void exposeTabLayoutWhenCollapsed() {
        appBarLayout.addOnOffsetChangedListener(
                new AppBarStateChangeListener() {
                    @Override
                    public void onStateChanged(AppBarLayout appBarLayout, State state) {
                        switch (state) {
                            case COLLAPSED:
                                tabLayout.setElevation(20);
                                break;
                        }
                    }
                }
        );
    }
}

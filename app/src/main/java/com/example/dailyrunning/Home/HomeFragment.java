package com.example.dailyrunning.Home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.dailyrunning.R;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class HomeFragment extends Fragment {
    private View rootView;
    private AppBarLayout appBarLayout;
    private SegmentTabLayout tabLayout;
    private ViewPager2 viewPager2;

    private String[] mTitles = {"Đang theo dõi", "Bạn"};
    private HomeViewModel mHomeViewModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHomeViewModel=new ViewModelProvider(getActivity()).get(HomeViewModel.class);

        tabLayout = (SegmentTabLayout) rootView.findViewById(R.id.tabLayout);
        tabLayout.setTabData(mTitles);

        appBarLayout = (AppBarLayout) rootView.findViewById(R.id.topAppBar);
        viewPager2 = (ViewPager2) rootView.findViewById(R.id.homeScreenViewPager);

        exposeTabLayoutWhenCollapsed();

        setUpViewPager();
    }


    // Handle tap and swipe
    private void setUpViewPager() {
        // Attach adapter to viewPager

        HomeScreenAdapter homeScreenAdapter = new HomeScreenAdapter(this);
        viewPager2.setAdapter(homeScreenAdapter);

        //restore state
        if(mHomeViewModel.tabPosition!=null)
        {

            tabLayout.setCurrentTab(mHomeViewModel.tabPosition);
            int deohieukieugi=mHomeViewModel.tabPosition;
            viewPager2.postDelayed(new Runnable() {

                @Override
                public void run() {
                    viewPager2.setCurrentItem(deohieukieugi);
                }
            }, 200);
            Log.v("Restore state",String.valueOf(viewPager2.getCurrentItem())+" "+mHomeViewModel.tabPosition);
        }

        // Handle selecting on tab layout
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewPager2.setCurrentItem(position);
                mHomeViewModel.tabPosition=position;
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

        // Handle swipe gesture
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.setCurrentTab(position);
                mHomeViewModel.tabPosition=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    // Display tab layout when toolbar is collapsed
    private void exposeTabLayoutWhenCollapsed() {

        if(mHomeViewModel.isExpanded!=null)
            appBarLayout.setExpanded(mHomeViewModel.isExpanded);
        //
        appBarLayout.addOnOffsetChangedListener(
                new AppBarStateChangeListener() {
                    @Override
                    public void onStateChanged(AppBarLayout appBarLayout, State state) {
                        switch (state) {
                            case COLLAPSED:
                                tabLayout.setElevation(20);
                                mHomeViewModel.isExpanded=false;
                                break;
                            case EXPANDED:
                                mHomeViewModel.isExpanded=true;
                                break;
                        }
                    }
                }
        );
    }

    //region save state

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }


    //endregion


}

package com.example.dailyrunning.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.example.dailyrunning.R;
import com.example.dailyrunning.user.UserViewModel;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;

public class HomeFragment extends Fragment{
    private View rootView;
    private AppBarLayout appBarLayout;
    private SegmentTabLayout tabLayout;
    private ViewPager2 viewPager2;

    private String[] mTitles = {"Đang theo dõi", "Bạn"};
    private HomeViewModel mHomeViewModel;
    private MaterialToolbar mTopToolBar;
    private UserViewModel mUserViewModel;
    private NavController mNavController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mHomeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        mUserViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        mTopToolBar = rootView.findViewById(R.id.topToolBar);
        mTopToolBar.setTitle("");
        mNavController = Navigation.findNavController(getActivity(), R.id.home_fragment_container);


        tabLayout = (SegmentTabLayout) rootView.findViewById(R.id.tabLayout);
        tabLayout.setTabData(mTitles);

        appBarLayout = (AppBarLayout) rootView.findViewById(R.id.topAppBar);
        viewPager2 = (ViewPager2) rootView.findViewById(R.id.homeScreenViewPager);

        exposeTabLayoutWhenCollapsed();

        setUpViewPager();

        setupSearchIcon();

        updateUIWhenUserChanged();

        mHomeViewModel.mHomeActivity.getValue().showNavBar();

        getActivity().findViewById(R.id.bottom_nav_bar).setVisibility(View.VISIBLE);

        return rootView;
    }


    private void updateUIWhenUserChanged() {
        mUserViewModel.getCurrentUser().observe(getActivity(),
                userInfo -> {
                    if (userInfo == null)
                        return;
                    mTopToolBar.setTitle(getGreet() + userInfo.getDisplayName());
                    Log.v("Home Fragment", "user updated " + mTopToolBar.getTitle() + "\n" + userInfo.getDisplayName());

                    //TODO: update post for new user
                });
    }

    private String getGreet()
    {
        int now=DateTime.now().get(DateTimeFieldType.clockhourOfDay());
        if(now>=0 && now<12)

            return "Good morning, ";

        else if(now>=12 && now<=18)
            return "Good afternoon, ";
        else return "Good evening, ";

    }

    // Handle tap and swipe
    private void setUpViewPager() {
        // Attach adapter to viewPager

        HomeScreenAdapter homeScreenAdapter = new HomeScreenAdapter(getChildFragmentManager(), getLifecycle());

        if (viewPager2.getAdapter() == null)
            viewPager2.setAdapter(homeScreenAdapter);

        //restore state
        if (mHomeViewModel.tabPosition != null) {

            tabLayout.setCurrentTab(mHomeViewModel.tabPosition);
            int temp = mHomeViewModel.tabPosition;
            viewPager2.postDelayed(new Runnable() {

                @Override
                public void run() {
                    viewPager2.setCurrentItem(temp);
                }
            }, 10);
            Log.v("Restore state", String.valueOf(viewPager2.getCurrentItem()) + " " + mHomeViewModel.tabPosition);
        }

        // Handle selecting on tab layout
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewPager2.setCurrentItem(position);
                mHomeViewModel.tabPosition = position;
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

        // Handle swipe gesture
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(mHomeViewModel.tabPosition, positionOffset, positionOffsetPixels);

            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.setCurrentTab(position);
                mHomeViewModel.tabPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    // Display tab layout when toolbar is collapsed
    private void exposeTabLayoutWhenCollapsed() {

        if (mHomeViewModel.isExpanded != null)
            appBarLayout.setExpanded(mHomeViewModel.isExpanded);
        //
        appBarLayout.addOnOffsetChangedListener(
                new AppBarStateChangeListener() {
                    @Override
                    public void onStateChanged(AppBarLayout appBarLayout, State state) {
                        switch (state) {
                            case COLLAPSED:
                                tabLayout.setElevation(20);
                                mHomeViewModel.isExpanded = false;
                                break;
                            case EXPANDED:
                                mHomeViewModel.isExpanded = true;
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

    private void setupSearchIcon() {
        mTopToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_search:
                        getActivity().findViewById(R.id.bottom_nav_bar).setVisibility(View.GONE);
                        mNavController.navigate(R.id.action_homeFragment_to_findFragment);
                        return true;
                }
                return false;
            }
        });
    }
}

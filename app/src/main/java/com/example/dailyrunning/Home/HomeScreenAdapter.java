package com.example.dailyrunning.Home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class HomeScreenAdapter extends FragmentStateAdapter {
    public HomeScreenAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new HomeFollowingFragment();
        }
        else if (position == 1) {
            fragment = new HomeUserFragment();
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

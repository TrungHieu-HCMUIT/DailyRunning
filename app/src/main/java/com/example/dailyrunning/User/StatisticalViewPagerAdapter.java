package com.example.dailyrunning.User;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class StatisticalViewPagerAdapter extends FragmentStateAdapter {



    public StatisticalViewPagerAdapter(Fragment f) {//Pager constructor receives Activity instance
        super(f);
    }

    @Override
    public int getItemCount() {
        return 3;//Number of fragments displayed
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        return StatisticalFragment.newInstance(position);
    }
}


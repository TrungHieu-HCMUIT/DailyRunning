package com.example.dailyrunning.user;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class StatisticalViewPagerAdapter extends FragmentStateAdapter {



    public StatisticalViewPagerAdapter(Fragment f,String userID) {//Pager constructor receives Activity instance
        super(f);
        Log.d("StatisticalViewPagerAdapter", "constructor: " + userID);
        this.userID=userID;
    }
    String userID;
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
        Log.d("StatisticalViewPagerAdapter", "create Fragment: " + userID);
        return StatisticalFragment.newInstance(position,userID);
    }
}


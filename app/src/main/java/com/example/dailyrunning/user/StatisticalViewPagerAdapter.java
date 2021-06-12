package com.example.dailyrunning.user;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.dailyrunning.home.find.OtherUserStatisticalFragment;

public class StatisticalViewPagerAdapter extends FragmentStateAdapter {

    private boolean isMe;
    public StatisticalViewPagerAdapter(Fragment f,boolean isMe) {//Pager constructor receives Activity instance
        super(f);
        this.isMe=isMe;
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
        if(isMe)
        return StatisticalFragment.newInstance(position);
        else
            return OtherUserStatisticalFragment.newInstance(position);
    }
}


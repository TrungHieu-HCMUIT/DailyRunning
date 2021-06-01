package com.example.dailyrunning.record.spotify;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MusicFragmentAdapter extends FragmentStateAdapter {
    public MusicFragmentAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        if(position==0)
            return new MyMusicFragment();
        else if(position==1)
            return  new DiscoverFragment();
        return null;

    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
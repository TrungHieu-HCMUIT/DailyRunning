package com.example.dailyrunning.record.Spotify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.dailyrunning.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.spotify.protocol.types.Track;

public class MusicMainFragment extends Fragment {

    private SpotifyViewModel mSpotifyViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mSpotifyViewModel=new ViewModelProvider(getActivity()).get(SpotifyViewModel.class);
        return inflater.inflate(R.layout.fragment_music_main, container, false);
    }



    private void checkBottomPlayerState() {
        Track currentTrack=mSpotifyViewModel.mCurrentTrack.getValue();
        if(currentTrack!=null)
        {
            FragmentManager mFragmentManager=getChildFragmentManager();
            Fragment bottomPlayer=mFragmentManager.findFragmentById(R.id.bottom_player_fragment);
            mFragmentManager.beginTransaction().show(bottomPlayer).commit();
        }
        else
        {
            FragmentManager mFragmentManager=getChildFragmentManager();
            Fragment bottomPlayer=mFragmentManager.findFragmentById(R.id.bottom_player_fragment);
            mFragmentManager.beginTransaction().hide(bottomPlayer).commit();
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewPager2 viewPager2=view.findViewById(R.id.viewpager);
        MusicFragmentAdapter mMusicFragmentAdapter=new MusicFragmentAdapter(this);
        viewPager2.setAdapter(mMusicFragmentAdapter);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    if(position==0)
                        tab.setText("My Music");
                    else
                        tab.setText("Discover");
                }
        ).attach();
        //checkBottomPlayerState();

    }
}
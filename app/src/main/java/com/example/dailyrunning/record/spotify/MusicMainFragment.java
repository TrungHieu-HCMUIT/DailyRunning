package com.example.dailyrunning.record.spotify;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.example.dailyrunning.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.spotify.protocol.types.Track;

public class MusicMainFragment extends Fragment {

    private SpotifyViewModel mSpotifyViewModel;
    private Context mContext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
        mContext=getContext();
        mSpotifyViewModel=new ViewModelProvider((ViewModelStoreOwner) mContext).get(SpotifyViewModel.class);

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
        checkBottomPlayerState();


    }
}
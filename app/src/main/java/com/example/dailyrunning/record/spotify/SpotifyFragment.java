package com.example.dailyrunning.record.spotify;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dailyrunning.R;
import com.example.dailyrunning.record.MapsActivity;


public class SpotifyFragment extends Fragment {


    private View rootView;
    private SpotifyViewModel mSpotifyViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_spotify, container, false);


        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSpotifyViewModel = new ViewModelProvider((ViewModelStoreOwner) getContext()).get(SpotifyViewModel.class);
        if (mSpotifyViewModel.spotifyAppRemote.getValue() == null || !mSpotifyViewModel.spotifyAppRemote.getValue().isConnected()
                || mSpotifyViewModel.spotifyService.getValue() == null) {
            MapsActivity parent = (MapsActivity) getContext();
            parent.startSpotifyService();
        }
    }
}
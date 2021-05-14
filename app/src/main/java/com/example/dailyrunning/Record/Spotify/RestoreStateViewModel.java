package com.example.dailyrunning.Record.Spotify;

import android.graphics.drawable.Drawable;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RestoreStateViewModel extends ViewModel {
    MutableLiveData<PlaylistAdapter> mPlaylistAdapter=new MutableLiveData<>();
    MutableLiveData<TrackAdapter> mRecentlyPlayedTrackAdapter=new MutableLiveData<>();
    MutableLiveData<DiscoverPlaylistAdapter> mDiscoverPlaylistAdapter=new MutableLiveData<>();
    MutableLiveData<Drawable> mThumbnailImage=new MutableLiveData<>();



}

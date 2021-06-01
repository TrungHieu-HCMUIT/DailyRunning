package com.example.dailyrunning.record.spotify;

import android.graphics.drawable.Drawable;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import kaaes.spotify.webapi.android.models.PlaylistSimple;

public class RestoreStateViewModel extends ViewModel {
    MutableLiveData<PlaylistAdapter> mPlaylistAdapter=new MutableLiveData<>();
    MutableLiveData<TrackAdapter> mRecentlyPlayedTrackAdapter=new MutableLiveData<>();
    MutableLiveData<DiscoverPlaylistAdapter> mDiscoverPlaylistAdapter=new MutableLiveData<>();
    MutableLiveData<Drawable> mThumbnailImage=new MutableLiveData<>();
    MutableLiveData<List<PlaylistSimple>> featuredPlaylist=new MutableLiveData<>();



}

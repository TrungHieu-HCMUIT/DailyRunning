package com.example.dailyrunning.record.spotify;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dailyrunning.record.MapsActivity;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.UserPrivate;

public class SpotifyViewModel extends ViewModel {
    public MutableLiveData<String> accessToken = new MutableLiveData<String>();
    public MutableLiveData<SpotifyService> spotifyService = new MutableLiveData<>();
    public MutableLiveData<SpotifyAppRemote> spotifyAppRemote = new MutableLiveData<>();
    public MutableLiveData<Track> mCurrentTrack = new MutableLiveData<>();
    public MutableLiveData<PlayerState> mPlayerState = new MutableLiveData<>();
    public MutableLiveData<UserPrivate> mCurrentUser = new MutableLiveData<>();
    public MutableLiveData<MapsActivity> mMapsActivity = new MutableLiveData<>();

}

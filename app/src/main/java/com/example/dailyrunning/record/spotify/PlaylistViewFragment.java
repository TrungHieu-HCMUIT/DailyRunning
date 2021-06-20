package com.example.dailyrunning.record.spotify;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dailyrunning.R;

import java.util.List;
import java.util.stream.Collectors;

import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class PlaylistViewFragment extends Fragment {

    private View rootView;
    private ImageView mThumbnailImageView;
    private TextView mTitleTextView;
    private TextView mDescriptionTextView;
    private Button mPlayButton;
    private RecyclerView mRecyclerView;
    private PlaylistSimple mThisPlaylist;
    private TrackAdapter mTrackAdapter;
    private SpotifyViewModel mSpotifyViewModel;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_playlist_view, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext=getContext();
        findView();
        init();

    }

    private void checkBottomPlayerState() {
        com.spotify.protocol.types.Track currentTrack=mSpotifyViewModel.mCurrentTrack.getValue();
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
    private void init() {

        mSpotifyViewModel = new ViewModelProvider((ViewModelStoreOwner) mContext).get(SpotifyViewModel.class);
        mThisPlaylist = getArguments().getParcelable("playlist");
        checkBottomPlayerState();
        if (mThisPlaylist.images.size() != 0)
            Glide.with(mThumbnailImageView).load(mThisPlaylist.images.get(0).url).into(mThumbnailImageView);
        mTitleTextView.setText(mThisPlaylist.name);
        mDescriptionTextView.setText(mThisPlaylist.tracks.total + " songs • " + mThisPlaylist.owner.display_name);
        mPlayButton.setOnClickListener(v -> {
            mSpotifyViewModel.spotifyAppRemote.getValue().getPlayerApi().play(mThisPlaylist.uri);
        });

        mSpotifyViewModel.spotifyService.observe((LifecycleOwner) mContext, spotifyService -> {
            spotifyService.getPlaylistTracks(mSpotifyViewModel.mCurrentUser.getValue().id
                    , mThisPlaylist.uri.replace("spotify:playlist:",""), new Callback<Pager<PlaylistTrack>>() {
                @Override
                public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                    List<Track> tracks=playlistTrackPager.items.stream().map(item->item.track).collect(Collectors.toList());
                    workingWithRecyclerView(tracks);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("GET PLAYLIST TRACK", error.toString());

                }
            });
        });


    }

    private void workingWithRecyclerView(List<Track> data) {
        mTrackAdapter = new TrackAdapter(data,mThisPlaylist.uri, (Activity) mContext);
        mRecyclerView.setAdapter(mTrackAdapter);
    }

    private void findView() {
        mThumbnailImageView = rootView.findViewById(R.id.playlist_view_thumbnail_image_view);
        mTitleTextView = rootView.findViewById(R.id.playlist_view_title_text_view);
        mDescriptionTextView = rootView.findViewById(R.id.playlist_view_description_text_view);
        mPlayButton = rootView.findViewById(R.id.playlist_view_play_button);
        mRecyclerView = rootView.findViewById(R.id.playlist_view_recycler_view);
    }
}
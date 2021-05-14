package com.example.dailyrunning.Record.Spotify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyrunning.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import kaaes.spotify.webapi.android.models.FeaturedPlaylists;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class DiscoverFragment extends Fragment {

    private View rootView;
    private TextInputLayout mSearchTextInputLayout;
    private RecyclerView mRecyclerView;
    private DiscoverPlaylistAdapter mDiscoverPlaylistAdapter;
    public SpotifyViewModel mSpotifyViewModel;
    private List<PlaylistSimple> defaultPlaylist;
    private RestoreStateViewModel mRestoreStateViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_discover, container, false);
        findView();
        init();
        return rootView;
    }


    private void init() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRestoreStateViewModel = new ViewModelProvider(getActivity()).get(RestoreStateViewModel.class);
        mSpotifyViewModel = new ViewModelProvider(getActivity()).get(SpotifyViewModel.class);
        if (mRestoreStateViewModel.mDiscoverPlaylistAdapter.getValue() == null)
            mSpotifyViewModel.spotifyService.observe(getActivity(), spotifyService -> {
                spotifyService.getFeaturedPlaylists(new Callback<FeaturedPlaylists>() {
                    @Override
                    public void success(FeaturedPlaylists featuredPlaylists, Response response) {
                        defaultPlaylist = featuredPlaylists.playlists.items;
                        updateRecyclerView(defaultPlaylist);
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            });
        else
            mRecyclerView.setAdapter(mRestoreStateViewModel.mDiscoverPlaylistAdapter.getValue());
        searchForPlaylist();
    }

    private void searchForPlaylist() {
        mSearchTextInputLayout.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                mSpotifyViewModel.spotifyService.getValue().searchPlaylists(
                        mSearchTextInputLayout.getEditText().getText().toString().trim(), new Callback<PlaylistsPager>() {
                            @Override
                            public void success(PlaylistsPager playlistsPager, Response response) {
                                updateRecyclerView(playlistsPager.playlists.items);
                                mRestoreStateViewModel.mDiscoverPlaylistAdapter.setValue(mDiscoverPlaylistAdapter);
                            }

                            @Override
                            public void failure(RetrofitError error) {

                            }
                        });
                return true;
            }
            return false;
        });
        mSearchTextInputLayout.setEndIconOnClickListener(v -> {
            updateRecyclerView(defaultPlaylist);
            mRestoreStateViewModel.mDiscoverPlaylistAdapter.setValue(null);
            mSearchTextInputLayout.getEditText().setText("");
        });

    }

    private void updateRecyclerView(List<PlaylistSimple> data) {
        mDiscoverPlaylistAdapter = new DiscoverPlaylistAdapter(data, this);
        mRecyclerView.setAdapter(mDiscoverPlaylistAdapter);
    }

    private void findView() {
        mSearchTextInputLayout = rootView.findViewById(R.id.discover_search_text_input_layout);
        mRecyclerView = rootView.findViewById(R.id.discover_recycler_view);
    }
}
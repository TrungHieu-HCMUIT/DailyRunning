package com.example.dailyrunning.record.spotify;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyrunning.R;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

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
    private RestoreStateViewModel mRestoreStateViewModel;
    private Context mContext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_discover, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext=getContext();
        findView();
        init();
    }

    private void init() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        mRestoreStateViewModel = new ViewModelProvider((ViewModelStoreOwner) mContext).get(RestoreStateViewModel.class);
        mSpotifyViewModel = new ViewModelProvider((ViewModelStoreOwner) mContext).get(SpotifyViewModel.class);
        if (mRestoreStateViewModel.mDiscoverPlaylistAdapter.getValue() == null)
            if (mRestoreStateViewModel.featuredPlaylist.getValue() != null)
                updateRecyclerView(mRestoreStateViewModel.featuredPlaylist.getValue());
            else
                mSpotifyViewModel.spotifyService.observe((LifecycleOwner) mContext, spotifyService -> {
                    spotifyService.getFeaturedPlaylists(new Callback<FeaturedPlaylists>() {
                        @Override
                        public void success(FeaturedPlaylists featuredPlaylists, Response response) {
                            mRestoreStateViewModel.featuredPlaylist.setValue(featuredPlaylists.playlists.items);
                            updateRecyclerView(mRestoreStateViewModel.featuredPlaylist.getValue());
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
        mSearchTextInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0) {
                    updateRecyclerView(mRestoreStateViewModel.featuredPlaylist.getValue());
                    mRestoreStateViewModel.mDiscoverPlaylistAdapter.setValue(null);
                }
            }
        });
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
            updateRecyclerView(mRestoreStateViewModel.featuredPlaylist.getValue());
            mRestoreStateViewModel.mDiscoverPlaylistAdapter.setValue(null);
            mSearchTextInputLayout.getEditText().setText("");
        });

    }

    private void updateRecyclerView(List<PlaylistSimple> data) {
        mDiscoverPlaylistAdapter = new DiscoverPlaylistAdapter(data, mContext);
        mRecyclerView.setAdapter(mDiscoverPlaylistAdapter);
    }

    private void findView() {
        mSearchTextInputLayout = rootView.findViewById(R.id.discover_search_text_input_layout);
        mRecyclerView = rootView.findViewById(R.id.discover_recycler_view);
    }
}
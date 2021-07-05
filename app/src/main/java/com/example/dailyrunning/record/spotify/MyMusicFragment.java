package com.example.dailyrunning.record.spotify;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyrunning.R;
import com.example.dailyrunning.record.MapsActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MyMusicFragment extends Fragment {


    private SpotifyViewModel spotifyViewModel;
    private SpotifyAppRemote mSpotifyAppRemote;
    private RecyclerView mRecentlyPlayedRecyclerView;
    private RecyclerView mMyPlaylistRecyclerView;
    private TrackAdapter trackAdapter;
    private PlaylistAdapter playlistAdapter;
    private NavController mNavController;
    private static final String CLIENT_ID = "c10a34dbe6f14c9aa605c3fd682377f8";
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    private TextInputLayout mSearchTextInputLayout;
    private View root;
    private RestoreStateViewModel mRestoreStateViewModel;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_my_music, container, false);

        // Inflate the layout for this fragment
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext=getContext();
        spotifyViewModel = new ViewModelProvider((ViewModelStoreOwner) mContext).get(SpotifyViewModel.class);
        mRestoreStateViewModel= new ViewModelProvider((ViewModelStoreOwner) mContext).get(RestoreStateViewModel.class);

        mSearchTextInputLayout = root.findViewById(R.id.search_text_input_layout);
        mNavController = Navigation.findNavController((Activity) mContext, R.id.spotify_fragment_container);

        root.findViewById(R.id.recently_plays_text_view).setOnClickListener(vi -> {
            mNavController.navigate(R.id.action_musicMainFragment_to_playerFragment);
        });
        initRecyclerView();
        spotifyAPI();
    }

    private void initRecyclerView()
    {
        mMyPlaylistRecyclerView = root.findViewById(R.id.my_playlist_recycler_view);
        mMyPlaylistRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        mRecentlyPlayedRecyclerView = root.findViewById(R.id.recently_plays_recycler_view);
        mRecentlyPlayedRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
    }
    private void workWithPlaylistRecyclerView() {

        playlistAdapter = new PlaylistAdapter(playlistSimples,mContext);
        mRestoreStateViewModel.mPlaylistAdapter.setValue(playlistAdapter);
        mMyPlaylistRecyclerView.setAdapter(playlistAdapter);
    }

    private void workWithRecentlyPlaysRecyclerView(List<Track> tracks) {

        trackAdapter = new TrackAdapter(tracks,null, (Activity) mContext);
        mRestoreStateViewModel.mRecentlyPlayedTrackAdapter.setValue(trackAdapter);
        mRecentlyPlayedRecyclerView.setAdapter(trackAdapter);
    }

    List<PlaylistSimple> playlistSimples;
    private boolean restoreState()
    {
        if(mRestoreStateViewModel.mPlaylistAdapter.getValue()==null|| mRestoreStateViewModel.mRecentlyPlayedTrackAdapter.getValue()==null)
        {
            return false;
        }
        else
        {
            playlistAdapter=mRestoreStateViewModel.mPlaylistAdapter.getValue();
            trackAdapter=mRestoreStateViewModel.mRecentlyPlayedTrackAdapter.getValue();
            mMyPlaylistRecyclerView.setAdapter(playlistAdapter);
            mRecentlyPlayedRecyclerView.setAdapter(trackAdapter);
            playlistAdapter.filter("");
            trackAdapter.filter("");
            return true;
        }
    }
    private void spotifyAPI() {

        spotifyViewModel.spotifyService.observe((LifecycleOwner) mContext, spotifyService -> {
            spotifyService.getMe(new Callback<UserPrivate>() {
                @Override
                public void success(UserPrivate userPrivate, Response response) {
                    Log.v("USER IDENTIFY", userPrivate.product + "\n a");
                    if (userPrivate.product != null && userPrivate.product.equals("premium"))//premium user
                    {
                        //region Search
                        searchInMyMusic();
                        //endregion
                        if(restoreState())
                            return;
                        //region load
                        loadRecently();
                        loadMyPlaylist();
                        //endregion



                    } else {
                        Log.v("USER IDENTIFY", "free user");
                        //region Search
                        ((MapsActivity) mContext).showSnackBar("Vui lòng nâng cấp Spotify Premium để sử dụng tính năng này!",
                                new Snackbar.Callback(){
                                    @Override
                                    public void onDismissed(Snackbar transientBottomBar, int event) {
                                        super.onDismissed(transientBottomBar, event);
                                    }
                                });
                        //endregion
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        });
    }

    private void loadMyPlaylist() {
        spotifyViewModel.spotifyService.getValue().getMyPlaylists(new Callback<Pager<PlaylistSimple>>() {
            @Override
            public void success(Pager<PlaylistSimple> playlistSimplePager, Response response) {
                playlistSimples = playlistSimplePager.items;

                workWithPlaylistRecyclerView();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void searchInMyMusic() {
        mSearchTextInputLayout.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                trackAdapter.filter(mSearchTextInputLayout.getEditText().getText().toString());
                playlistAdapter.filter(mSearchTextInputLayout.getEditText().getText().toString());
                return true;
            }
            return false;
        });
        mSearchTextInputLayout.setEndIconOnClickListener(v -> {
            trackAdapter.filter("");
            playlistAdapter.filter("");
            mSearchTextInputLayout.getEditText().setText("");
        });
        mSearchTextInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length()==0)
                {
                    trackAdapter.filter("");
                    playlistAdapter.filter("");
                }
            }
        });


    }



    private void loadRecently() {
        HttpUrl.Builder builder = HttpUrl.parse("https://api.spotify.com/v1/me/player/recently-played").newBuilder();
        builder.addQueryParameter("limit", "5");

        Request request = new Request.Builder().header("Authorization", "Bearer " + spotifyViewModel.accessToken.getValue()).url(builder.build().toString()).build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new com.squareup.okhttp.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                String res = response.body().string();
                //Log.v("FUCKFUCKFUCK",response.body().string());
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    JSONArray tracksJSONArray = jsonObject.getJSONArray("items");
                    List<Track> tracks = new ArrayList<>();
                    for (int i = 0; i < tracksJSONArray.length(); i++) {
                        String trackID = tracksJSONArray.getJSONObject(i).getJSONObject("track").getString("id");
                        spotifyViewModel.spotifyService.getValue().getTrack(trackID, new Callback<Track>() {
                            @Override
                            public void success(Track track, Response response) {
                                tracks.add(track);
                                Log.v("TRACKNAME", track.name + "\n" + tracks.size() + " " + tracksJSONArray.length());
                                if (tracks.size() == tracksJSONArray.length()) {
                                    List<Track> data=  tracks.stream().filter(distinctByKey(item->item.name)).collect(Collectors.toList());


                                    workWithRecentlyPlaysRecyclerView(data);
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Log.v("TRACKNAME", error.getMessage());

                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }


}

package com.example.dailyrunning.record;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.dailyrunning.R;
import com.example.dailyrunning.record.spotify.SpotifyViewModel;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MapsActivity extends FragmentActivity {

    //region spotify auth
    // Request code will be used to verify if result comes from the login activity. Can be set to any integer.
    private static final int REQUEST_CODE = 1337;


    //endregion
    private Button playButton;
    private EditText uriEditText;
    private TextView textView;
    private static final String CLIENT_ID = "3933e6ca20464721b0b4e6c1cb623c9d";
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    private String textT;
    private SpotifyAppRemote mSpotifyAppRemote;
    private String accessToken;
    private SpotifyViewModel mSpotifyViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        mSpotifyViewModel = new ViewModelProvider(this).get(SpotifyViewModel.class);
        mSpotifyViewModel.mMapsActivity.setValue(this);
    }

    public void startSpotifyService() {
        spotifyAuth();
        setUpSpotifyAppRemote();
    }

    private void setUpSpotifyAppRemote() {
        // Set the connection parameters
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();
        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        mSpotifyViewModel.spotifyAppRemote.setValue(spotifyAppRemote);


                        Log.d("MainActivity", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        connected();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    public void callOnPostResume()
    {
        super.onPostResume();
    }
    private void spotifyAuth() {
        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming"
                , "playlist-read-private"
                , "user-read-playback-state"
                , "user-modify-playback-state"
                , "user-read-recently-played"
                , "user-read-currently-playing"
                , "user-read-private"});
        AuthorizationRequest request = builder.build();

        Intent intent = AuthorizationClient.createLoginActivityIntent(this, request);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    accessToken = response.getAccessToken();
                    mSpotifyViewModel.accessToken.setValue(accessToken);
                    SpotifyApi api = new SpotifyApi();
                    api.setAccessToken(accessToken);
                    SpotifyService spotifyService;
                    spotifyService = api.getService();

                    mSpotifyViewModel.spotifyService.setValue(spotifyService);
                    spotifyService.getMe(new Callback<UserPrivate>() {
                        @Override
                        public void success(UserPrivate userPrivate, Response response) {
                            mSpotifyViewModel.mCurrentUser.setValue(userPrivate);
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });


                    // Handle successful response
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }


    private void connected() {

        // Subscribe to PlayerState
        //mSpotifyAppRemote.getPlayerApi().play("spotify:track:6IDKbALVvJKL7krrO6n7tf");

        final Track[] prevTrack = {null, null};
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {

                    if (prevTrack[0] == null && playerState.track != null) {
                        prevTrack[0] = playerState.track;
                        mSpotifyViewModel.mCurrentTrack.setValue(playerState.track);
                    } else if (prevTrack[0] != null && playerState.track != null && !prevTrack[0].name.equals(playerState.track.name)) {

                        prevTrack[0] = playerState.track;
                        mSpotifyViewModel.mCurrentTrack.setValue(playerState.track);
                    }
                    mSpotifyViewModel.mPlayerState.setValue(playerState);

                });

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mSpotifyAppRemote!=null&&mSpotifyAppRemote.isConnected())
            SpotifyAppRemote.disconnect(mSpotifyAppRemote);

    }


}

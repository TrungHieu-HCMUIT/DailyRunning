package com.example.dailyrunning.record;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.dailyrunning.R;
import com.example.dailyrunning.authentication.LoginViewModel;
import com.example.dailyrunning.generated.callback.OnClickListener;
import com.example.dailyrunning.record.spotify.SpotifyViewModel;
import com.example.dailyrunning.user.UserViewModel;
import com.example.dailyrunning.utils.ConfirmDialog;
import com.example.dailyrunning.utils.RunningLoadingDialog;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.material.snackbar.Snackbar;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.jetbrains.annotations.NotNull;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MapsActivity extends FragmentActivity implements
        RecordViewModel.WorkingOnActivity , LoginViewModel.LoadingDialog,
        RecordViewModel.ShowConfirmDialog, UserViewModel.RunningSnackBar {

    //region spotify auth
    // Request code will be used to verify if result comes from the login activity. Can be set to any integer.
    private static final int REQUEST_CODE = 1337;
    public static final int RECORD_CODE = 5712;


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
    private RecordViewModel mRecordViewModel;
    final int CHECK_PERMISSION=3003;
    public static BitmapDescriptor startMarker;
    public static BitmapDescriptor endMarker;
    private RunningLoadingDialog loadingDialog;
    private ConfirmDialog confirmDialog;
    private LinearLayout mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        mRootView=findViewById(R.id.record_root_layout);
        mSpotifyViewModel = new ViewModelProvider(this).get(SpotifyViewModel.class);
        mSpotifyViewModel.mMapsActivity.setValue(this);
        mRecordViewModel=new ViewModelProvider(this).get(RecordViewModel.class);
        mRecordViewModel.workingOnActivity=this;
        loadingDialog=new RunningLoadingDialog();
        confirmDialog=new ConfirmDialog();
        mRecordViewModel.loadingDialog=this;
        mRecordViewModel.confirmDialog=this;
        MapsInitializer.initialize(this);
        startMarker=BitmapDescriptorFactory.fromResource(R.drawable.marker_start);
        endMarker=BitmapDescriptorFactory.fromResource(R.drawable.marker_end);

    }


    @Override
    public void showDialog() {
        loadingDialog.show(getSupportFragmentManager(),"tag");
    }

    @Override
    public void dismissDialog() {

        loadingDialog.dismiss();

    }

    public Fragment getForegroundFragment(){
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.record_fragment_container);
        return navHostFragment == null ? null : navHostFragment.getChildFragmentManager().getFragments().get(0);
    }
    @Override
    public void onBackPressed() {

        Fragment currentFrag=getForegroundFragment();
        if(currentFrag instanceof RecordFragment) {
            mRecordViewModel.confirmDialog.show("Hủy bỏ hoạt động", "Bạn có muốn hủy bỏ hoạt động hiện tại ?"
                    , v -> {
                    }, v -> {
                        super.onBackPressed();
                    });
        }
        else{
            super.onBackPressed();
        }
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
    @Override
    public boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},CHECK_PERMISSION);
            return false;
        }
        return  true;
    }

    @Override
    public void updateTimer(MutableLiveData<String> timeString) {
        runOnUiThread(() -> timeString.postValue(mRecordViewModel.getTimeWorkingString()));
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==CHECK_PERMISSION)
        {
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                mRecordViewModel.listenToLocationChange();
                if(checkPermission())
                mRecordViewModel.map.setMyLocationEnabled(true);
            }
            else
            {
                //TODO show dialog and finish record activity
            }
        }
    }


    @Override
    public void show(String title, String description, View.OnClickListener onCancel, View.OnClickListener onConfirm) {
        confirmDialog.show(getSupportFragmentManager(),title,description,onCancel,onConfirm);
    }

    @Override
    public void showSnackBar(String content, Snackbar.Callback callback) {
        Snackbar.make(mRootView, content, Snackbar.LENGTH_SHORT).setTextColor(ContextCompat.getColor(this, R.color.color_palette_3))
                .addCallback(callback).show();
    }
}

   
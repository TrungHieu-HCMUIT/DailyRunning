package com.example.dailyrunning.record;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.dailyrunning.R;
import com.example.dailyrunning.databinding.FragmentRecordBinding;
import com.example.dailyrunning.record.spotify.SpotifyViewModel;
import com.example.dailyrunning.user.UserViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.spotify.protocol.types.Track;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class RecordFragment extends Fragment implements UserViewModel.OnTaskComplete {

    private static final String TAG = "RecordFragment";
    private NavController mNavController;

    private SpotifyViewModel mSpotifyViewModel;
    private ImageButton mSpotifyImageButton;
    private RecordViewModel mRecordViewModel;
    private FragmentRecordBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentRecordBinding.inflate(inflater,container,false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.setLifecycleOwner(getActivity());
        mNavController = Navigation.findNavController(getActivity(), R.id.record_fragment_container);

        // creates the map leading to the onMapReady function being called
        mSpotifyViewModel=new ViewModelProvider(getActivity()).get(SpotifyViewModel.class);
        mRecordViewModel=new ViewModelProvider(getActivity()).get(RecordViewModel.class);
        binding.setRecordViewModel(mRecordViewModel);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mRecordViewModel.mLocationClient=getFusedLocationProviderClient(getActivity());
        mRecordViewModel.onTaskComplete=this;
        mRecordViewModel.isMapLoading=true;
        mRecordViewModel.loadingDialog.showDialog();
        mapFragment.getMapAsync(mRecordViewModel);
        binding.musicImageButton.setOnClickListener(v -> {
            mNavController.navigate(R.id.action_recordFragment_to_spotifyFragment);
        });
        checkBottomPlayerState();
      /*  requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                confirmCancelActivity();
            }
        });*/

    }
    void confirmCancelActivity()
    {
        mRecordViewModel.confirmDialog.show("Hủy bỏ hoạt động","Bạn có muốn hủy bỏ hoạt động hiện tại ?"
                ,v->{ },v->{
                    getActivity().finish();
                });
    }

    private void checkBottomPlayerState() {
        Track currentTrack = mSpotifyViewModel.mCurrentTrack.getValue();
        FragmentManager mFragmentManager = getChildFragmentManager();
        Fragment bottomPlayer = mFragmentManager.findFragmentById(R.id.bottom_player_fragment);
        if (currentTrack != null) {
            mFragmentManager.beginTransaction().show(bottomPlayer).commit();
        } else {
            mFragmentManager.beginTransaction().hide(bottomPlayer).commit();
        }
    }


    @Override
    public void onComplete(boolean result) {
        if(result)
            mNavController.navigate(R.id.action_recordFragment_to_finishFragment);
    }
}
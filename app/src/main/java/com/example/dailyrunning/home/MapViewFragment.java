package com.example.dailyrunning.home;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.dailyrunning.R;
import com.example.dailyrunning.databinding.FragmentMapViewBinding;
import com.example.dailyrunning.home.post.PostViewModel;
import com.example.dailyrunning.record.MapsActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class MapViewFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ArrayList<LatLng> list = new ArrayList<>();
    private Context context;
    private FragmentMapViewBinding binding;
    private PostViewModel mPostViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMapViewBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getActivity());
        mPostViewModel=new ViewModelProvider(getActivity()).get(PostViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapViewFragment);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        binding.backButton.setOnClickListener(v->{
            getActivity().onBackPressed();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setCompassEnabled(false);
        List<LatLng> locations=mPostViewModel.getSelectedPost().getValue().getActivity()
                .getLatLngArrayList().stream().map(item->new LatLng(item.getLatitude(),item.getLongitude())).collect(Collectors.toList());
        drawing(locations);
    }

    public void drawing(List<LatLng> listDraw) {
        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(ResourcesCompat.getColor(getResources(), R.color.color_palette_3, null)); //without theme
        polyOptions.width(10);
        polyOptions.addAll(listDraw);

        mMap.clear();
        mMap.addPolyline(polyOptions);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng LatLng : listDraw) {
            builder.include(LatLng);
        }

        final LatLngBounds bounds = builder.build();
        mMap.addMarker(new MarkerOptions()
                .icon(HomeActivity.startMarker)
                .position(listDraw.get(0))
                .zIndex(2)
                .draggable(false)
                .anchor(0.5f, 1));
        mMap.addMarker(new MarkerOptions()
                .icon(HomeActivity.endMarker)
                .position(listDraw.get(listDraw.size() - 1))
                .zIndex(2)
                .draggable(false)
                .anchor(0.5f, 1));
        //BOUND_PADDING is an int to specify padding of bound.. try 100.
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
        mMap.animateCamera(cu);
    }

}
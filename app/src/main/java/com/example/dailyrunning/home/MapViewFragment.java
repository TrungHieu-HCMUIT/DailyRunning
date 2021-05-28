package com.example.dailyrunning.home;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dailyrunning.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MapViewFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ArrayList<LatLng> list= new ArrayList<>();
    private Context context;
    private NavController mNavController;
    private String INTENT_DATECREATED="date";
    private String datecreated=null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);
        context = view.getContext();

        mNavController = Navigation.findNavController(getActivity(), R.id.home_fragment_container);

        
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference activityRef = database.getReference();

        Bundle bundle = getArguments();
        datecreated =(String) bundle.getString(INTENT_DATECREATED);

        SupportMapFragment mapFragment = (SupportMapFragment)  getChildFragmentManager().findFragmentById(R.id.mapViewFragment);
        //The function getMapAsync acquires a GoogleMap initializing the map system and the view.
        mapFragment.getMapAsync( this);

        Query query = activityRef.child("Activity").orderByChild("dateCreated").equalTo(datecreated);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot walkSnapShot : dataSnapshot.getChildren()) {
                        String key = walkSnapShot.child(walkSnapShot.getKey()).getKey();
                        List<Object> locations = (List<Object>) dataSnapshot.child(key).child("latLngArrayList").getValue();
                        for (Object locationObj : locations) {
                            Map<String, Object> location = (Map<String, Object>) locationObj;
                            LatLng latLng = new LatLng((Double) location.get("latitude"), (Double) location.get("longitude"));
                            list.add(latLng);
                        }
                    }
                }
                drawing(list);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // disenable zoom button because the zoom level is fixed.
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // disable this because after the POI marker popup this tool will be added automatically
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }
    public void drawing(List<LatLng> listDraw)
    {
        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(Color.RED);
        polyOptions.width(5);
        polyOptions.addAll(listDraw);

        mMap.clear();
        mMap.addPolyline(polyOptions);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : listDraw) {
            builder.include(latLng);
        }

        final LatLngBounds bounds = builder.build();

        //BOUND_PADDING is an int to specify padding of bound.. try 100.
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
        mMap.animateCamera(cu);
    }

}
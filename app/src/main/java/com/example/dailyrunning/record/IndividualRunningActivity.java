package com.example.dailyrunning.record;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dailyrunning.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IndividualRunningActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private String INTENT_DATETIMEKEY = "dateTime";
    private TextView textViewCaption=null;
    private TextView textViewPace= null;
    private TextView textViewDistance = null;
    private TextView textViewTime = null;
    private TextView textViewDateTime=null;
    private long duration;
    private double distance;
    private int pace;
    private String caption;
    private List<LatLng> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_post);

        Intent intent = getIntent();
        String dateTimeRecv = intent.getExtras().getString(INTENT_DATETIMEKEY);
        textViewDistance = (TextView) findViewById(R.id.tvDistance);
        textViewTime = (TextView) findViewById(R.id.tvDuration);
        textViewPace=(TextView) findViewById(R.id.tvPace) ;
        textViewCaption=(TextView) findViewById(R.id.tvContent);
        textViewDateTime=(TextView) findViewById(R.id.tvDateTime);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.ivMap);
        //The function getMapAsync acquires a GoogleMap initializing the map system and the view.
        mapFragment.getMapAsync( this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rt = database.getReference().child("UserInfo").child(user.getUid()).child("list of run");
        Query query = rt.child(user.getUid()).orderByChild("time").equalTo(dateTimeRecv);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot walkSnapShot : dataSnapshot.getChildren()) {
                        duration = (long) walkSnapShot.child("duration").getValue();
                        distance = (double) walkSnapShot.child("distance").getValue();
                        pace = Math.toIntExact((long) walkSnapShot.child("pace").getValue());
                        caption=(String) walkSnapShot.child("describe").getValue();
                        String key = walkSnapShot.child(walkSnapShot.getKey()).getKey();
                        List<Object> locations = (List<Object>) dataSnapshot.child(key).child("latLngArrayList").getValue();
                        for (Object locationObj : locations) {
                            Map<String, Object> location = (Map<String, Object>) locationObj;
                            LatLng latLng = new LatLng((Double) location.get("latitude"), (Double) location.get("longitude"));
                            list.add(latLng);
                        }
                        }
                    textViewDateTime.setText(dateTimeRecv);
                    textViewCaption.setText(caption);
                    textViewPace.setText(pace+"m/ph");
                    textViewDistance.setText(formatDistance(distance));
                    textViewTime.setText(formatDuration(duration));
                }
                drawing(list);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        MapsInitializer.initialize(this);

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

    public String formatDistance(double pDistance) {
        if (pDistance / 1000 >= 1) {
            @SuppressLint("DefaultLocale") String distanceStr = String.format("%.2f", (pDistance / 1000));
            return distanceStr + "km";
        } else {
            @SuppressLint("DefaultLocale") String distanceStr = String.format("%.0f", pDistance);
            return distanceStr + "m";
        }
    }

    public String formatDuration(long pDuration) {
        return DateUtils.formatElapsedTime(pDuration);

    }
}


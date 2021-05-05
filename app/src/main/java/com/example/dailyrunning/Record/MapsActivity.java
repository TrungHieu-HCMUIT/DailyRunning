package com.example.dailyrunning.Record;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.dailyrunning.Model.Activity;
import com.example.dailyrunning.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.SphericalUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private String INTENT_LATLNGARRLIST= "latlngarrlist";
    private String INTENT_DISTANCEKEY ="distance";
    private  String INTENT_TIMEKEY = "time";
    private String INTENT_DATECREATED= "datecreated";
    // variable for Google Map API
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private LocationListener locListener;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationManager locationManager;
    private Location mLastKnownLocation;
    private final LatLng mDefaultLocation = new LatLng(10.886113149181485, 106.78205916450588);
    private static final int DEFAULT_ZOOM = 20;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    ArrayList<LatLng> list = new ArrayList<LatLng>();

    private TextView textlength = null;
    private TextView textView=null;
    private TextView textCalories = null;
    FloatingActionButton startButton;
    ImageButton endButton;
    StopWatchService stopWatchService;
    ImageButton pauseButton;
    ImageButton countinueButton;
    boolean mBound = false;
    Intent startWatchIntent;
    Intent stopWatchIntent;
    LinearLayout layout,layout1,layout2;
    Calendar c;
    String formattedDate = "";
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        setTitle(getString(R.string.currentRunning));

        //create the view elements
        textView = (TextView) findViewById(R.id.data_time);
        startButton = (FloatingActionButton) findViewById(R.id.start);
        endButton = (ImageButton) findViewById(R.id.btnEnd);
        textlength = (TextView) findViewById(R.id.data_distance);
        layout =(LinearLayout) findViewById(R.id.staticPostLayout);
        layout1 =(LinearLayout) findViewById(R.id.staticPostLayout1);
        layout2 =(LinearLayout) findViewById(R.id.staticPostLayout2);
        pauseButton=(ImageButton) findViewById(R.id.btnPause) ;
        countinueButton = (ImageButton) findViewById(R.id.btnCountinue);
        startWatchIntent = new Intent(this, StopWatchService.class);
        stopWatchIntent = new Intent(this, StopWatchService.class);
        c = Calendar.getInstance();


        // creates the map leading to the onMapReady function being called
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mLocationRequest = new LocationRequest();
        mFusedLocationClient = getFusedLocationProviderClient(this);
        //set the interval for active location update to 0.3 second
        mLocationRequest.setInterval(300);
        // the fast interval request is 0.01 second
        mLocationRequest.setFastestInterval(100);
        // request High accuracy location based on the need of this app
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
         /* when the start button is pressed, start the stopwatch service
          * and bind to that service.
           * */
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startService(startWatchIntent);
                bindService(startWatchIntent, mConnection, Context.BIND_AUTO_CREATE);

                // when the walk has started, take note of the current time.

                formattedDate = df.format(c.getTime());
                startButton.setVisibility(View.INVISIBLE);
                layout.setVisibility(View.VISIBLE);
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
                requestLocationUpdates(list);

            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mBound = false;
                countinueButton.setVisibility(View.VISIBLE);
                endButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.INVISIBLE);

            }
        });

        countinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBound=true;
                countinueButton.setVisibility(View.INVISIBLE);
                endButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
            }
        });
        endButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                double computedDistance = getDistance();
                long elapsedTime = stopWatchService.getElapsedTime();
                stopService(stopWatchIntent);
                unbindService(mConnection);
                mBound = false;

                Intent intentToFinish = new Intent(getApplicationContext(), com.example.dailyrunning.Record.FinishActivity.class);
                intentToFinish.putExtra(INTENT_DISTANCEKEY,computedDistance);
                intentToFinish.putExtra(INTENT_TIMEKEY,elapsedTime);
                intentToFinish.putExtra(INTENT_LATLNGARRLIST,list);
                intentToFinish.putExtra(INTENT_DATECREATED,formattedDate);
                startActivity(intentToFinish);


            }
        });


        /**
         * Every one second: display the time that has passed since the walk has started.
         */
        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                if (mBound) {
                                    long elapsedTime = stopWatchService.getElapsedTime();
                                    String formattedTime = DateUtils.formatElapsedTime(elapsedTime);
                                    textView.setText(formattedTime);
                                    @SuppressLint("DefaultLocale") String Sum = String.format("%.2f", getDistance()/100.0);
                                    textlength.setText(Sum);
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates(final ArrayList<LatLng> tList) {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation(),tList);
            }
        }, null);
    }
    private void onLocationChanged(Location location,ArrayList<LatLng> tList) {
        tList.add(new LatLng(location.getLatitude(), location.getLongitude()));

        Polyline line = mMap.addPolyline(new PolylineOptions()
                .addAll(tList)
                .width(5)
                .color(Color.RED));
    }


    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            StopWatchService.LocalBinder binder = (StopWatchService.LocalBinder) service;
            stopWatchService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        MapsInitializer.initialize(this);
        // ask for the permission of requesting location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1340);
        } else {
            mMap.setMyLocationEnabled(true);
        }


        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // disenable zoom button because the zoom level is fixed.
        //mMap.getUiSettings().setZoomControlsEnabled(true);
        //enable positioning button
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        //getCurrentLocation
        mMap.setMyLocationEnabled(true);
        if (mMap != null) {
            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
                }
            });}
            // disable this because after the POI marker popup this tool will be added automatically
            mMap.getUiSettings().setMapToolbarEnabled(false);
    }




    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1340:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                } else {
                    Toast.makeText(this, "Location cannot be obtained due to " + "missing permission.", Toast.LENGTH_LONG).show();
                }
                break;
        }

    }
    /**
     *Loop through the arrayList of latlng
     * and compute the distance between each latlng
     *
     * @return  total distance covered in meters
     */
    private double getDistance() {

        double totalDistance = 0;

        for (int i = 0; i < list.size() - 1; i++) {
            totalDistance = totalDistance + SphericalUtil.computeDistanceBetween(list.get(i), list.get(i + 1));
        }

        return totalDistance;

    }
}

package com.example.dailyrunning.record;


import android.graphics.Color;
import android.location.Location;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.jetbrains.annotations.NotNull;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class RecordViewModel extends ViewModel implements OnMapReadyCallback{
    public GoogleMap map;
    public final int MIN_TIME = 1000;
    public final int MIN_DISTANCE = 1;
    public final float BEARING = 192.833f;
    public final float ZOOM = 48;
    public WorkingOnActivity workingOnActivity;
    ArrayList<LatLng> locations=new ArrayList<>();
    public FusedLocationProviderClient mLocationClient;
    public MutableLiveData<Boolean> isTracking=new MutableLiveData<>();
    public MutableLiveData<Boolean> topControllerExpanded=new MutableLiveData<>();
    public MutableLiveData<Boolean> isPaused=new MutableLiveData<>();
    public MutableLiveData<String> timeString=new MutableLiveData<>();
    public MutableLiveData<String> distanceString=new MutableLiveData<>();
    public MutableLiveData<String> paceString=new MutableLiveData<>();
    public LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private TimerTask mTimerTask;
    private Timer mTimer;
    private float distance;
    private long timeWorkingInSec;

    {
        mLocationRequest=LocationRequest.create();
        //set the interval for active location update to 0.3 second
        mLocationRequest.setInterval(300);
        // the fast interval request is 0.01 second
        mLocationRequest.setFastestInterval(100);
        // request High accuracy location based on the need of this app
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        isTracking.setValue(false);
        mLocationCallback= new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onLocationChanged(locationResult.getLastLocation());
            }
        };
        timeString.setValue("00:00:00");
        distanceString.setValue("0.00 Km");
        paceString.setValue("0.00 m/s");
        timeWorkingInSec=0;
        distance=0;
        mTimer=new Timer();
    }
    void newRecord()
    {
        timeString.setValue("00:00:00");
        distanceString.setValue("0.00 Km");
        timeWorkingInSec=0;
        distance=0;
        lastPaceUpdateTime=0;
        paceString.setValue("0.00 m/s");
        topControllerExpanded.setValue(true);
        isPaused.setValue(false);
        mTimer=new Timer();
        listenToLocationChange();
    }
    void initRecord()
    {
        if(workingOnActivity.checkPermission())
            map.setMyLocationEnabled(true);
        map.getUiSettings().setScrollGesturesEnabled(false);
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMinZoomPreference(ZOOM);

        if(!isTracking.getValue())
        newRecord();

    }

    void onLocationChanged(Location location)
    {

        LatLng newLocation=new LatLng(location.getLatitude(),location.getLongitude());

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(newLocation)
                .bearing(BEARING)
                .zoom(ZOOM)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        updateDistance(newLocation);
        if(isTracking.getValue())
        locations.add(newLocation);
        updatePolyline();

    }
    void updatePace(double newDistance)
    {
        if(timeWorkingInSec-lastPaceUpdateTime>=1)
        {
            lastPaceUpdateTime=timeWorkingInSec;
            paceString.setValue(String.format("%.2f", newDistance)+ " m/s");
        }
    }
    private long lastPaceUpdateTime=0;
    void updateDistance(LatLng newLocation)
    {

        if(isPaused.getValue())
            return;
        if(locations.size()>0) {
            LatLng lastLocation=locations.get(locations.size()-1);
            double newDistance=distance(newLocation.latitude, newLocation.longitude,lastLocation.latitude,lastLocation.longitude);
            distance +=newDistance;
            distanceString.setValue(String.format("%.2f", distance/1000)+ " Km");
            updatePace(newDistance);
        }
    }
    void updatePolyline()
    {

            map.addPolyline(new PolylineOptions()
                    .addAll(locations)
                    .width(10)
                    .color(Color.parseColor("#00A896")));

    }

    void listenToLocationChange() {
        if(workingOnActivity.checkPermission())
        mLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback,null);

    }

    public void onTrackingClick()
    {
        if(isTracking.getValue())
        {
            mLocationClient.removeLocationUpdates(mLocationCallback);
        }
        else
        {
            startTimer();

        }
        isTracking.setValue(!isTracking.getValue());
    }


    void startTimer()
    {
        mTimerTask =new TimerTask() {
            @Override
            public void run() {
                timeWorkingInSec++;
                workingOnActivity.updateTimer(timeString);

            }
        };
        mTimer.scheduleAtFixedRate(mTimerTask, 0 ,1000);
    }
    String getTimeWorkingString()
    {
        Period period = new Period(timeWorkingInSec * 1000L);
        String time =String.format("%02d:%02d:%02d", period.getHours(), period.getMinutes(), period.getSeconds());
        return time;
    }
    public void onTopControllerArrowClick()
    {
        topControllerExpanded.setValue(!topControllerExpanded.getValue());
    }
    public void togglePause()
    {
        if(isPaused.getValue())
        {
            startTimer();

        }
        else
        {
            mTimerTask.cancel();
        }
        isPaused.setValue(!isPaused.getValue());
    }
    public void finishRecord()
    {

    }



    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;

        initRecord();
    }
    public interface WorkingOnActivity
    {
        boolean checkPermission();
        void updateTimer(MutableLiveData<String> timeString);
    }

    public double distance (double lat_a, double lng_a, double lat_b, double lng_b )
    {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return distance * meterConversion;
    }
}

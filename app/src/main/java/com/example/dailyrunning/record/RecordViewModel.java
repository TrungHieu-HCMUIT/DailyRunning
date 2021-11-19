package com.example.dailyrunning.record;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dailyrunning.authentication.LoginViewModel;
import com.example.dailyrunning.model.Activity;
import com.example.dailyrunning.model.Comment;
import com.example.dailyrunning.model.Post;
import com.example.dailyrunning.user.UserViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class RecordViewModel extends ViewModel implements OnMapReadyCallback {
    public GoogleMap map;
    public final int MIN_TIME = 1000;
    public final int MIN_DISTANCE = 1;
    public final float BEARING = 192.833f;
    public final float ZOOM = 48;
    public WorkingOnActivity workingOnActivity;
    ArrayList<LatLng> locations = new ArrayList<>();
    public FusedLocationProviderClient mLocationClient;
    public MutableLiveData<Boolean> isTracking = new MutableLiveData<>();
    public MutableLiveData<Boolean> topControllerExpanded = new MutableLiveData<>();
    public MutableLiveData<Boolean> isPaused = new MutableLiveData<>();
    public MutableLiveData<String> timeString = new MutableLiveData<>();
    public MutableLiveData<String> distanceString = new MutableLiveData<>();
    public MutableLiveData<String> paceString = new MutableLiveData<>();
    public MutableLiveData<String> averagePaceString = new MutableLiveData<>();
    public MutableLiveData<String> activityDescribe = new MutableLiveData<>();
    public MutableLiveData<Integer> runningPointAcquired = new MutableLiveData<>();
    private double averagePace = 0;
    private DatabaseReference activityRef = FirebaseDatabase.getInstance().getReference()
            .child("Activity").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    private DatabaseReference postRef = FirebaseDatabase.getInstance().getReference()
            .child("Post").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    private StorageReference activityImageRef = FirebaseStorage.getInstance().getReference().child("activity_images");
    public LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private TimerTask mTimerTask;
    private Timer mTimer;
    private float distance;
    public long timeWorkingInSec;
    private int mMapBoundsPadding = 25;
    public LoginViewModel.LoadingDialog loadingDialog;
    public Bitmap activityImage;
    public UserViewModel.OnTaskComplete onTaskComplete;
    public ShowConfirmDialog confirmDialog;
    public static SimpleDateFormat activityDateFormat = new SimpleDateFormat("dd-MM-yyy HH:mm");
    public boolean isMapLoading=true;

    public RecordViewModel()
    {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(300);
        mLocationRequest.setFastestInterval(100);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        isTracking.setValue(false);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onLocationChanged(locationResult.getLastLocation());
            }
        };
        timeString.setValue("00:00:00");
        distanceString.setValue("0.00 Km");
        paceString.setValue("0.00 m/s");
        timeWorkingInSec = 0;
        distance = 0;
        mTimer = new Timer();
        averagePaceString.setValue("0:00 m/s");
        activityDescribe.setValue("");
        averagePace = 0;
        isMapLoading=true;
    }

    void newRecord() {
        timeString.setValue("00:00:00");
        distanceString.setValue("0.00 Km");
        timeWorkingInSec = 0;
        distance = 0;
        lastPaceUpdateTime = 0;
        paceString.setValue("0.00 m/s");
        topControllerExpanded.setValue(true);
        isPaused.setValue(false);
        mTimer = new Timer();
        activityDescribe.setValue("");
        activityImage = null;
        averagePace = 0;
        listenToLocationChange();

    }

    void initRecord() {
        if (workingOnActivity.checkPermission())
            map.setMyLocationEnabled(true);
        map.getUiSettings().setScrollGesturesEnabled(false);
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.getUiSettings().setCompassEnabled(false);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMinZoomPreference(ZOOM);
        if (!isTracking.getValue())
            newRecord();

    }

    void onLocationChanged(Location location) {

        LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(newLocation)
                .bearing(BEARING)
                .zoom(ZOOM)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        if(isMapLoading)
        {
            isMapLoading=false;
            loadingDialog.dismissDialog();
        }
        updateDistance(newLocation);
        if (isTracking.getValue())
            locations.add(newLocation);
        updatePolyline();

    }

    void updatePace(double newDistance) {
        if (timeWorkingInSec - lastPaceUpdateTime >= 1) {
            lastPaceUpdateTime = timeWorkingInSec;
            paceString.setValue(String.format("%.2f", newDistance) + " m/s");
        }
    }

    private long lastPaceUpdateTime = 0;

    void updateDistance(LatLng newLocation) {

        if (isPaused.getValue())
            return;
        if (locations.size() > 0) {
            LatLng lastLocation = locations.get(locations.size() - 1);
            double newDistance = distance(newLocation.latitude, newLocation.longitude, lastLocation.latitude, lastLocation.longitude);
            distance += newDistance;
            distanceString.setValue(String.format("%.2f", distance / 1000) + " Km");
            updatePace(newDistance);
        }
    }

    void updatePolyline() {
        if(!isPaused.getValue())
        map.addPolyline(new PolylineOptions()
                .addAll(locations)
                .width(10)
                .color(Color.parseColor("#00A896")));

    }

    void listenToLocationChange() {
        if (workingOnActivity.checkPermission())
            mLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);

    }

    public void onTrackingClick() {
        if (isTracking.getValue()) {
            mLocationClient.removeLocationUpdates(mLocationCallback);
        } else {
            startTimer();
        }
        isTracking.setValue(!isTracking.getValue());
    }


    void startTimer() {
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                timeWorkingInSec++;
                workingOnActivity.updateTimer(timeString);

            }
        };
        mTimer.scheduleAtFixedRate(mTimerTask, 0, 1000);
    }

    String getTimeWorkingString() {
        if(timeWorkingInSec<0)
            return "Invalid input";
        Period period = new Period(timeWorkingInSec * 1000L);
        String time = String.format("%02d:%02d:%02d", period.getHours(), period.getMinutes(), period.getSeconds());
        return time;
    }

    public void onTopControllerArrowClick() {
        topControllerExpanded.setValue(!topControllerExpanded.getValue());
    }

    public void togglePause() {
        if (isPaused.getValue()) {
            startTimer();

        } else {
            mTimerTask.cancel();
        }
        isPaused.setValue(!isPaused.getValue());
    }

    public void finishRecord() {
        confirmDialog.show("Kết thúc hoạt động", "Bạn có muốn kết thúc hoạt động ?", v -> {
        }, v -> {
            mLocationClient.removeLocationUpdates(mLocationCallback);
            map.resetMinMaxZoomPreference();
            if (locations.size() > 0) {
                loadingDialog.showDialog();
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng loc : locations) {
                    builder.include(loc);
                }
                LatLngBounds bounds = builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
                map.moveCamera(cu);
                averagePace = distance / timeWorkingInSec;
                averagePaceString.setValue(String.format("%.2f", averagePace) + " m/s");
                runningPointAcquired.setValue((int) (distance / 1000));
                map.addMarker(new MarkerOptions()
                        .icon(MapsActivity.startMarker)
                        .position(locations.get(0))
                        .zIndex(2)
                        .draggable(false)
                        .anchor(0.5f, 1));
                map.addMarker(new MarkerOptions()
                        .icon(MapsActivity.endMarker)
                        .position(locations.get(locations.size() - 1))
                        .zIndex(2)
                        .draggable(false)
                        .anchor(0.5f, 1));

                new Handler().postDelayed(() -> map.snapshot(bitmap -> {
                    activityImage = bitmap;
                    loadingDialog.dismissDialog();

                    onTaskComplete.onComplete(true);
                }), 1000);
            }
        });

    }

    static public byte[] bitmapToByteArray(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        return bytes.toByteArray();
    }

    public void onSaveClick(UserViewModel.OnTaskComplete onTaskComplete) {
        try {
            loadingDialog.showDialog();
            String key = activityRef.push().getKey();
            ArrayList<com.example.dailyrunning.model.LatLng> latLngArrayList = locations.stream()
                    .map(location ->
                            new com.example.dailyrunning.model.LatLng(location.latitude, location.longitude))
                    .collect(Collectors.toCollection(ArrayList::new));
            activityImageRef.child(key).putBytes(bitmapToByteArray(activityImage)).
                    addOnSuccessListener(taskSnapshot -> activityImageRef.child(key).
                            getDownloadUrl().
                            addOnSuccessListener(
                    uri -> {
                        Activity newActivity = new Activity(
                                key,
                                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                activityDateFormat.format(Calendar.getInstance().getTime()),
                                distance,
                                timeWorkingInSec,
                                uri.toString(),
                                averagePace,
                                activityDescribe.getValue(),
                                latLngArrayList
                        );
                        activityRef.child(key).setValue(newActivity);
                        createNewPost(newActivity);
                        loadingDialog.dismissDialog();
                        onTaskComplete.onComplete(true);
                    }
            ));
        }
        catch (Exception e)
        {
            Log.e("Save Activity Err",e.getMessage());
            onTaskComplete.onComplete(false);
            loadingDialog.dismissDialog();
        }



    }

    void createNewPost(Activity activity) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String key = postRef.push().getKey();
        Post newPost = new Post(key,
                new ArrayList<Comment>(),
                new ArrayList<String>(),
                activity,
                user.getUid(),
                user.getPhotoUrl().toString(),
                user.getDisplayName()
        );
        postRef.child(key).setValue(newPost);

    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        initRecord();
    }

    public interface WorkingOnActivity {
        boolean checkPermission();

        void updateTimer(MutableLiveData<String> timeString);
    }

    public static double distance(double lat_a, double lng_a, double lat_b, double lng_b) {
        if(lat_a < -90 || lat_a>90 ||
                lat_b < -90 || lat_b>90 ||
                lng_a < -180 || lng_a>180 ||
                lng_b < -180 || lng_b>180
        )
            return -1000;
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b - lat_a);
        double lngDiff = Math.toRadians(lng_b - lng_a);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return distance * meterConversion;
    }

    public interface ShowConfirmDialog {
        void show(String title, String description, View.OnClickListener onCancel, View.OnClickListener onConfirm);
    }

}

package com.example.dailyrunning.Record;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dailyrunning.R;
import com.example.dailyrunning.Record.Spotify.SpotifyViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.SphericalUtil;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.Track;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class RecordFragment extends Fragment implements OnMapReadyCallback {

    private String INTENT_LATLNGARRLIST = "latlngarrlist";
    private String INTENT_DISTANCEKEY = "distance";
    private String INTENT_TIMEKEY = "time";
    private String INTENT_DATECREATED = "datecreated";
    private String INTENT_IMAGE = "pictureURL";
    private static final int UPDATE_TEXTVIEW = 0;
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
    private static int count = 0;
    private static double s = 0;
    private static double d = 0;
    private static double sum = 0;
    private static int delay = 1000; //1s
    private static int period = 1000; //1s
    private static double EARTH_RADIUS = 6378.137;//radius of earth
    private boolean isDraw=false;
    private static View viewMap=null;
    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }
    private boolean isPause = false;
    private boolean isStop = true;
    private MaterialCardView mBottomControlCardView;
    private Context mContext;
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    private Handler mHandler = null;
    private TextView textlength = null;
    private TextView textView = null;
    private TextView textCalories = null;
    private byte[] byteArray;
    FloatingActionButton startButton;
    ImageButton endButton;
    ImageButton pauseButton;
    ImageButton countinueButton;
    String formattedDate;
    Calendar c;
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");
    private View rootView;
    private NavController mNavController;
    private ImageButton mFoldButton;
    private LinearLayout mBottomControlCentreLinearLayout;
    private SpotifyViewModel mSpotifyViewModel;
    private ImageButton mSpotifyImageButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_record, container, false);
        mNavController = Navigation.findNavController(getActivity(), R.id.record_fragment_container);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        checkBottomPlayerState();
        c = Calendar.getInstance();

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE_TEXTVIEW:
                        updateTextView();
                        break;
                    default:
                        break;
                }
            }
        };


        // creates the map leading to the onMapReady function being called
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mLocationRequest = new LocationRequest();
        mFusedLocationClient = getFusedLocationProviderClient(getActivity());
        //set the interval for active location update to 0.3 second
        mLocationRequest.setInterval(300);
        // the fast interval request is 0.01 second
        mLocationRequest.setFastestInterval(100);
        // request High accuracy location based on the need of this app
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        setUpOnClick();



    }

    private void checkBottomPlayerState() {
        Track currentTrack=mSpotifyViewModel.mCurrentTrack.getValue();
        if(currentTrack!=null)
        {
            FragmentManager mFragmentManager=getChildFragmentManager();
            Fragment bottomPlayer=mFragmentManager.findFragmentById(R.id.bottom_player_fragment);
            mFragmentManager.beginTransaction().show(bottomPlayer).commit();
        }
        else
        {
            FragmentManager mFragmentManager=getChildFragmentManager();
            Fragment bottomPlayer=mFragmentManager.findFragmentById(R.id.bottom_player_fragment);
            mFragmentManager.beginTransaction().hide(bottomPlayer).commit();
        }
    }

    private void setUpOnClick() {
        /* when the start button is pressed, start the stopwatch service
         * and bind to that service.
         * */
        viewMap=getView().findViewById(R.id.map);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // when the walk has started, take note of the current time.
                formattedDate = df.format(c.getTime());
                startButton.setVisibility(View.GONE);
                mBottomControlCardView.setVisibility(View.VISIBLE);
                requestLocationUpdates(list);
                sum=0;
                count=0;
                startTimer();
                updateTextView();
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                countinueButton.setVisibility(View.VISIBLE);
                endButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.GONE);
                pauseTimer();
            }
        });

        countinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countinueButton.setVisibility(View.GONE);
                endButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);
                pauseTimer();
                startTimer();
            }
        });
        endButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopTimer();
                long time = count;
                Bundle resultForFinishFragment = new Bundle();
                resultForFinishFragment.putDouble(INTENT_DISTANCEKEY, getDistance());
                resultForFinishFragment.putLong(INTENT_TIMEKEY, time);
                resultForFinishFragment.putParcelableArrayList(INTENT_LATLNGARRLIST, list);
                resultForFinishFragment.putString(INTENT_DATECREATED, formattedDate);
                mMap.snapshot(bitmap -> {

                    ImageView img=rootView.findViewById(R.id.map_image);
                    img.setImageBitmap(bitmap);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byteArray = stream.toByteArray();
                    resultForFinishFragment.putByteArray(INTENT_IMAGE,byteArray);

                    mNavController.navigate(R.id.action_recordFragment_to_finishFragment, resultForFinishFragment);

                });
            }
        });
        mFoldButton.setOnClickListener(v->{
            if(mFoldButton.getTag().toString().equals("up"))
            {

                mBottomControlCentreLinearLayout.setVisibility(View.VISIBLE);

                mFoldButton.setTag("down");
                mFoldButton.setImageResource(R.drawable.ic_up_arrow);
            }
            else if(mFoldButton.getTag().toString().equals("down"))
            {

                mBottomControlCentreLinearLayout.setVisibility(View.GONE);




                mFoldButton.setTag("up");
                mFoldButton.setImageResource(R.drawable.ic_down_arrow);
            }

        });
        mSpotifyImageButton.setOnClickListener(v->{
            mNavController.navigate(R.id.action_recordFragment_to_spotifyFragment);
        });
    }

    private void initView() {
        //create the view elements
        textView = rootView.findViewById(R.id.data_time);
        startButton = rootView.findViewById(R.id.start);
        endButton = rootView.findViewById(R.id.btnEnd);
        textlength = rootView.findViewById(R.id.data_distance);
        mFoldButton=rootView.findViewById(R.id.fold_image_button);
        pauseButton = rootView.findViewById(R.id.btnPause);
        countinueButton = rootView.findViewById(R.id.btnCountinue);
        mBottomControlCardView = rootView.findViewById(R.id.bottom_control_centre_card_view);
        mBottomControlCentreLinearLayout=rootView.findViewById(R.id.bottom_control_centre_linear_layout);
        mSpotifyViewModel=new ViewModelProvider(getActivity()).get(SpotifyViewModel.class);
        mSpotifyImageButton=rootView.findViewById(R.id.music_image_button);
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates(final ArrayList<LatLng> tList) {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation(), tList);
            }
        }, null);
    }

    private void onLocationChanged(Location location, ArrayList<LatLng> tList) {
        if (isDraw) {
            tList.add(new LatLng(location.getLatitude(), location.getLongitude()));

            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .addAll(tList)
                    .width(5)
                    .color(Color.RED));
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        MapsInitializer.initialize(getActivity());
        // ask for the permission of requesting location
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1340);
        } else {
            mMap.setMyLocationEnabled(true);
        }


        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // disenable zoom button because the zoom level is fixed.
        //mMap.getUiSettings().setZoomControlsEnabled(true);
        //enable positioning button
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (mMap != null) {
            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
                }
            });
        }
        // disable this because after the POI marker popup this tool will be added automatically
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }
    abstract class TaskState  {
        public abstract void run();
        public abstract TaskState next();
    }
    class InitialState extends TaskState {
        public void run() {
            sendMessage(UPDATE_TEXTVIEW);
            do {
                try {

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            } while (isPause);
            count++;/*update time*/
        }
        public TaskState next() {
            return new FinalState();
        }
    }
    class FinalState extends TaskState  {
        public void run() {
            System.out.println("Finishing...");
        }
        public TaskState next(){
            return new InitialState();
        }
    }
    private void startTimer() {
        isDraw = true;
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    sendMessage(UPDATE_TEXTVIEW);
                    do {
                        try {

                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    } while (isPause);
                    count++;/*update time*/
//                    sum = sumDistance(latitude,longitude);/*update distance*/
                }
            };
        }

        if (mTimer != null)
            mTimer.schedule(new TimerTask() {
                private TaskState state = new InitialState();
                public void run() {
                this.state.run();
            }
            }, delay, period);

    }

    private void pauseTimer(){
        isPause = !isPause;
        isDraw = !isDraw;
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;

        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;

        }
        count = 0;
        isDraw = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1340:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity()
                        , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                } else {
                    Toast.makeText(getActivity(), "Location cannot be obtained due to " + "missing permission.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
    /**
     * Loop through the arrayList of latlng
     * and compute the distance between each latlng
     *
     * @return total distance covered in meters
     */
    private double getDistance() {

        double totalDistance = 0;

        for (int i = 0; i < list.size() - 1; i++) {
            totalDistance = totalDistance + SphericalUtil.computeDistanceBetween(list.get(i), list.get(i + 1));
        }
        return totalDistance;

    }
    public static String getTime(int second) {
        if (second < 10) {
            return "00:00:0" + second;
        }
        if (second < 60) {
            return "00:00:" + second;
        }
        if (second < 3600) {
            int minute = second / 60;
            second = second - minute * 60;
            if (minute < 10) {
                if (second < 10) {
                    return "00:" + "0" + minute + ":0" + second;
                }
                return "00:" + "0" + minute + ":" + second;
            }
            if (second < 10) {
                return "00:" + minute + ":0" + second;
            }
            return "00:" + minute + ":" + second;
        }
        int hour = second / 3600;
        int minute = (second - hour * 3600) / 60;
        second = second - hour * 3600 - minute * 60;
        if (hour < 10) {
            if (minute < 10) {
                if (second < 10) {
                    return "0" + hour + ":0" + minute + ":0" + second;
                }
                return "0" + hour + ":0" + minute + ":" + second;
            }
            if (second < 10) {
                return "0" + hour + ":" + minute + ":0" + second;
            }
            return "0" + hour + ":" + minute + ":" + second;
        }
        if (minute < 10) {
            if (second < 10) {
                return hour + ":0" + minute + ":0" + second;
            }
            return hour + ":0" + minute + ":" + second;
        }
        if (second < 10) {
            return hour + ":" + minute + ":0" + second;
        }
        return hour + ":" + minute + ":" + second;
    }
    public void updateTextView() {
        textView.setText(getTime(count));
        String Sum = String .format("%.2f",getDistance());
        textlength.setText(Sum);
    }
    public void sendMessage(int id) {
        if (mHandler != null) {
            Message message = Message.obtain(mHandler, id);
            mHandler.sendMessage(message);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
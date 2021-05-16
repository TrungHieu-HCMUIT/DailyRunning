package com.example.dailyrunning.Record;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.example.dailyrunning.R;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MapsActivity extends FragmentActivity  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

    }


}

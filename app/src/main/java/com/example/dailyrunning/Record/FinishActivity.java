package com.example.dailyrunning.Record;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dailyrunning.Model.Activity;
import com.example.dailyrunning.R;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FinishActivity extends AppCompatActivity {
    private final String INTENT_DISTANCEKEY = "distance";
    private final String INTENT_TIMEKEY = "time";
    private  String INTENT_CALORIESKEY = "calories";
    private String INTENT_DATECREATED= "datecreated";
    private String INTENT_LATLNGARRLIST= "latlngarrlist";
    EditText describeText;
    ArrayList<LatLng> list = new ArrayList<LatLng>();
    DatabaseReference exampleRun;
    Button buttonSave;
    Button buttonBack;
    TextView distanceTextView;
    TextView timeTextView;
    TextView paceTextView;
    int pace;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        setTitle(R.string.runCompleted);

        Intent intent = getIntent();
        double completedDist = intent.getExtras().getDouble(INTENT_DISTANCEKEY);
        long completedTime = intent.getExtras().getLong(INTENT_TIMEKEY);
        int calories = intent.getExtras().getInt(INTENT_CALORIESKEY);
        list=intent.getExtras().getParcelableArrayList(INTENT_LATLNGARRLIST);
        String formattedDate=intent.getExtras().getString(INTENT_DATECREATED);
        String paceString=getPace(completedDist,completedTime);

        describeText = (EditText) findViewById(R.id.describe_editText);
        buttonSave = (Button) findViewById(R.id.btnSave);
        buttonBack=(Button) findViewById(R.id.btnBack) ;
        distanceTextView= (TextView) findViewById(R.id.km);
        timeTextView = (TextView) findViewById(R.id.time);
        paceTextView= (TextView) findViewById(R.id.pace);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rt = database.getReference().child("UserInfo").child(user.getUid()).child("list of run");
        exampleRun = rt.push();


        distanceTextView.setText(formatDistance(completedDist));
        timeTextView.setText(formatDuration(completedTime));
        paceTextView.setText(paceString);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intentToMap = new Intent(getApplicationContext(),MapsActivity.class);
                startActivity(intentToMap);
                String id = rt.push().getKey();
                Activity activity = new Activity(id,
                        formattedDate,
                        completedDist,
                        completedTime,
                        list,
                        calories,
                        describeText.getText().toString(),
                        pace
                );
                exampleRun.setValue(activity);
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FinishActivity.super.onBackPressed();
            }
        });

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
        return DateUtils.formatElapsedTime(pDuration)+"s";

    }

    public String getPace(double length, long t) {
        pace = 0;
        long time=t;
        pace = (int) (time / length);
        return pace+"km/h";
    }

}

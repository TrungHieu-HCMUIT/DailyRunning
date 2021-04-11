package com.example.dailyrunning.Record;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dailyrunning.R;

public class FinishActivity extends AppCompatActivity {
    private final String INTENT_DISTANCEKEY = "distance";
    private final String INTENT_TIMEKEY = "time";
    private  String INTENT_CALORIESKEY = "calories";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        setTitle(R.string.runCompleted);

        Intent intent = getIntent();
        double completedDist = intent.getExtras().getDouble(INTENT_DISTANCEKEY);
        long completedTime = intent.getExtras().getLong(INTENT_TIMEKEY);
        int calories = intent.getExtras().getInt(INTENT_CALORIESKEY);

        TextView textViewDurationF = (TextView) findViewById(R.id.textViewTimeF);
        TextView textViewDistanceF = (TextView) findViewById(R.id.textViewDistanceF);
        TextView textViewCaloriesF = (TextView) findViewById(R.id.textViewCaloriesF);
        Button buttonBack = (Button) findViewById(R.id.buttonBack);

        textViewDistanceF.setText(formatDistance(completedDist));
        textViewDurationF.setText(formatDuration(completedTime));
        textViewCaloriesF.setText(calories+"");

        buttonBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intentToMap = new Intent(getApplicationContext(),MapsActivity.class);
                startActivity(intentToMap);

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
        return DateUtils.formatElapsedTime(pDuration);

    }
}

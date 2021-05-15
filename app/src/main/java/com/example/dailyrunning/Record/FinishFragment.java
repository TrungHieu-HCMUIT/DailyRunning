package com.example.dailyrunning.Record;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dailyrunning.Model.Activity;
import com.example.dailyrunning.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class FinishFragment extends Fragment {

    private final String INTENT_DISTANCEKEY = "distance";
    private final String INTENT_TIMEKEY = "time";
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
    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_finish, container, false);
        //setTitle(R.string.runCompleted);

        Bundle resultFromRecordFragment = getArguments();
        double completedDist = resultFromRecordFragment.getDouble(INTENT_DISTANCEKEY);
        long completedTime = resultFromRecordFragment.getLong(INTENT_TIMEKEY);
        list=resultFromRecordFragment.getParcelableArrayList(INTENT_LATLNGARRLIST);
        String formattedDate=resultFromRecordFragment.getString(INTENT_DATECREATED);
        String paceString=getPace(completedDist,completedTime);

        describeText = rootView.findViewById(R.id.describe_editText);
        buttonSave = rootView.findViewById(R.id.btnSave);
        buttonBack= rootView.findViewById(R.id.btnBack);
        distanceTextView= rootView.findViewById(R.id.km);
        timeTextView = rootView.findViewById(R.id.time);
        paceTextView= rootView.findViewById(R.id.pace);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rt = database.getReference().child("UserInfo").child(user.getUid()).child("list of run");
        exampleRun = rt.push();


        distanceTextView.setText(formatDistance(completedDist));
        timeTextView.setText(formatDuration(completedTime));
        paceTextView.setText(paceString);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String id = rt.push().getKey();
                Activity activity = new Activity(id,
                        formattedDate,
                        completedDist,
                        completedTime,
                        list,
                        describeText.getText().toString(),
                        pace
                );
                exampleRun.setValue(activity);
                getActivity().finish();
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        return rootView;
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
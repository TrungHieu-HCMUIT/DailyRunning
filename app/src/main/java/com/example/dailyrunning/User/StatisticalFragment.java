package com.example.dailyrunning.User;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.dailyrunning.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StatisticalFragment extends Fragment {
    private float distance;
    private Date timeWorking;
    private int workingCount;
    private ViewGroup rootView;
    private TextView distanceTextView;
    private TextView timeWorkingTextView;
    private TextView workingCountTextView;
    private final int STATISTICAL_WEEK=0;
    private final int STATISTICAL_MONTH=1;
    private final int STATISTICAL_YEAR=2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView= (ViewGroup) inflater.inflate(
                R.layout.fragment_statistical, container, false);
        //region get View
        distanceTextView=rootView.findViewById(R.id.distance_textView);
        timeWorkingTextView=rootView.findViewById(R.id.time_working_textView);
        workingCountTextView=rootView.findViewById(R.id.working_count_textView);
        //endregion

        setData(getArguments().getInt("position"));
        return rootView;
    }
    public static Fragment newInstance(int position) {
        StatisticalFragment mCurrentFragment = new StatisticalFragment();
        Bundle args = new Bundle();
        args.putInt("position",position);
        mCurrentFragment.setArguments(args);
        return mCurrentFragment;
    }
    private void setData(int position)
    {
        //TODO: get current user, get statistical info

        switch (position)
        {
            case STATISTICAL_WEEK:
                distance=12;
                timeWorking=Calendar.getInstance().getTime();
                workingCount=3;
                break;
            case STATISTICAL_MONTH:
                distance=20;
                timeWorking=Calendar.getInstance().getTime();
                workingCount=10;
                break;
            case STATISTICAL_YEAR:
                distance=50;
                timeWorking=Calendar.getInstance().getTime();
                workingCount=30;
                break;
            default:
                break;
        }
        distanceTextView.setText(String.valueOf(distance)+" Km");
        SimpleDateFormat dateFormat=new SimpleDateFormat("hh:mm:ss");
        timeWorkingTextView.setText(dateFormat.format(timeWorking));
        workingCountTextView.setText(String.valueOf(workingCount));
    }
}

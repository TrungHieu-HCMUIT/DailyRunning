package com.example.dailyrunning.user;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dailyrunning.model.Activity;
import com.example.dailyrunning.model.mLatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class StatisticalViewModel extends ViewModel {
    //0=day,1=week,2=month
    public MutableLiveData<ArrayList<Double>> distance = new MutableLiveData<>();
    public MutableLiveData<ArrayList<String>> timeWorking = new MutableLiveData<>();
    public MutableLiveData<ArrayList<Integer>> workingCount = new MutableLiveData<>();
    public int currentPage = 0;
    private Calendar c = Calendar.getInstance();
    private LocalDate now = new LocalDate();
    List<Activity> activities = new ArrayList<>();
    private DatabaseReference activityRef = FirebaseDatabase.getInstance().getReference().child("Activity");
    private UserInfo mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    DecimalFormat df = new DecimalFormat("#.##");


    public void fetchActivities() {

        activities.clear();
        activityRef.orderByChild("userID").equalTo(mCurrentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {

                    HashMap map = (HashMap) task.getResult().getValue();
                    if(map ==null)
                        return;
                    for (Object o : map.values().toArray()) {
                        activities.add(toActivity((HashMap) o));
                    }
                    new Runnable() {
                        @Override
                        public void run() {
                            getMonthStatistic();
                            getWeekStatistic();
                            getYearStatistic();
                        }
                    }.run();
                }
            }
        });
    }

    Activity toActivity(HashMap map) {
        double distance = Double.parseDouble(Objects.requireNonNull(map.get("distance")).toString());
        double pace = Double.parseDouble(Objects.requireNonNull(map.get("pace")).toString());
        return new Activity(
                Objects.requireNonNull(map.get("activityID")).toString(),
                Objects.requireNonNull(map.get("userID")).toString(),
                Objects.requireNonNull(map.get("dateCreated")).toString(),
                distance,
                (long) map.get("duration"),
                map.get("pictureURI").toString(),
                pace,
                map.get("describe").toString(),
                (ArrayList<mLatLng>) map.get("latLngArrayList")
        );
    }


    private String timeConvert(long sec) {
        Date d = new Date(sec * 1000L);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss"); // HH for 0-23
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        String time = df.format(d);
        return time;
    }

    public void getWeekStatistic()
    {
        LocalDate monday = now.withDayOfWeek(DateTimeConstants.MONDAY).minusDays(1);
        LocalDate sunday = now.withDayOfWeek(DateTimeConstants.SUNDAY).plusDays(1);
        List<Activity> weekActivity = activities.stream().filter(activity -> {
            int endIndex = activity.getDateCreated().indexOf(" ");
            String activityDate = activity.getDateCreated().substring(0, endIndex);
            try {
                java.util.Date tempDate=mSimpleDateFormat.parse(activityDate);
                LocalDate actDate=new LocalDate(tempDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            return now.isBefore(sunday)&&now.isAfter(monday);
        }).collect(Collectors.toList());

        int weekWorkingCount = weekActivity.size();
        double weekDistance = 0;
        long secWorking = 0;
        String _timeWorking;
        for (Activity act : weekActivity
        ) {
            weekDistance+=act.getDistance();
            secWorking+=act.getDuration();
        }
        _timeWorking=timeConvert(secWorking);
        weekDistance/=1000;
        String distanceFormat=df.format(weekDistance);
        weekDistance=Double.parseDouble(distanceFormat);
        ArrayList<String> workingTime=timeWorking.getValue();
        if (workingTime==null)
            workingTime=new ArrayList<>(Arrays.asList("","",""));
        workingTime.set(0, _timeWorking);
        timeWorking.setValue(workingTime);

        ArrayList<Double> _distance=distance.getValue();
        if (_distance==null)
            _distance=new ArrayList<>(Arrays.asList(0.0,0.0,0.0));
        _distance.set(0,weekDistance);
        distance.setValue(_distance);

        ArrayList<Integer> _workingCount=workingCount.getValue();
        if (_workingCount==null)
            _workingCount=new ArrayList<>(Arrays.asList(0,0,0));
        _workingCount.set(0,weekWorkingCount);
        workingCount.setValue(_workingCount);


    }
    public void getMonthStatistic() {
        LocalDate lastDay = now.dayOfMonth().withMaximumValue().plusDays(1);
        LocalDate firstDay = now.dayOfMonth().withMinimumValue().minusDays(1);
        List<Activity> monthActivity = activities.stream().filter(activity -> {
            int endIndex = activity.getDateCreated().indexOf(" ");
            String activityDate = activity.getDateCreated().substring(0, endIndex);
            try {
                java.util.Date tempDate =mSimpleDateFormat.parse(activityDate);
                LocalDate actDate=new LocalDate(tempDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return now.isBefore(lastDay)&&now.isAfter(firstDay);
        }).collect(Collectors.toList());
        int monthWorkingCount = monthActivity.size();
        double monthDistance = 0;
        long secWorking = 0;
        String _timeWorking;
        for (Activity act : monthActivity
        ) {
            monthDistance +=act.getDistance();
            secWorking+=act.getDuration();
        }
        _timeWorking=timeConvert(secWorking);
        monthDistance /=1000;
        String distanceFormat=df.format(monthDistance);
        monthDistance =Double.parseDouble(distanceFormat);
        ArrayList<String> workingTime=timeWorking.getValue();
        if (workingTime==null)
            workingTime=new ArrayList<>(Arrays.asList("","",""));
        workingTime.set(1, _timeWorking);
        timeWorking.setValue(workingTime);

        ArrayList<Double> _distance=distance.getValue();
        if (_distance==null)
            _distance=new ArrayList<>(Arrays.asList(0.0,0.0,0.0));
        _distance.set(1, monthDistance);
        distance.setValue(_distance);

        ArrayList<Integer> _workingCount=workingCount.getValue();
        if (_workingCount==null)
            _workingCount=new ArrayList<>(Arrays.asList(0,0,0));
        _workingCount.set(1,monthWorkingCount);
        workingCount.setValue(_workingCount);
    }
    public void getYearStatistic() {
        LocalDate lastDay = now.dayOfYear().withMaximumValue().plusDays(1);
        LocalDate firstDay = now.dayOfYear().withMinimumValue().minusDays(1);
        List<Activity> yearActivity = activities.stream().filter(activity -> {
            int endIndex = activity.getDateCreated().indexOf(" ");
            String activityDate = activity.getDateCreated().substring(0, endIndex);
            try {
                java.util.Date tempDate =mSimpleDateFormat.parse(activityDate);
                LocalDate actDate=new LocalDate(tempDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return now.isBefore(lastDay)&&now.isAfter(firstDay);
        }).collect(Collectors.toList());
        int yearWorkingCount = yearActivity.size();
        double yearDistance = 0;
        long secWorking = 0;
        String _timeWorking;
        for (Activity act : yearActivity
        ) {
            yearDistance +=act.getDistance();
            secWorking+=act.getDuration();
        }
        _timeWorking=timeConvert(secWorking);
        yearDistance /=1000;
        String distanceFormat=df.format(yearDistance);
        yearDistance =Double.parseDouble(distanceFormat);
        ArrayList<String> workingTime=timeWorking.getValue();
        if (workingTime==null)
            workingTime=new ArrayList<>(Arrays.asList("","",""));
        workingTime.set(2, _timeWorking);
        timeWorking.setValue(workingTime);

        ArrayList<Double> _distance=distance.getValue();
        if (_distance==null)
            _distance=new ArrayList<>(Arrays.asList(0.0,0.0,0.0));
        _distance.set(2, yearDistance);
        distance.setValue(_distance);

        ArrayList<Integer> _workingCount=workingCount.getValue();
        if (_workingCount==null)
            _workingCount=new ArrayList<>(Arrays.asList(0,0,0));
        _workingCount.set(2, yearWorkingCount);
        workingCount.setValue(_workingCount);
    }

}

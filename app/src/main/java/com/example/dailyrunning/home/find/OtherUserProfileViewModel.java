package com.example.dailyrunning.home.find;

import android.util.Log;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.example.dailyrunning.R;
import com.example.dailyrunning.authentication.LoginActivity;
import com.example.dailyrunning.model.Activity;
import com.example.dailyrunning.model.LatLng;
import com.example.dailyrunning.model.UserInfo;
import com.example.dailyrunning.utils.MedalAdapter;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;


public class OtherUserProfileViewModel extends ViewModel {
    private String userID;
    private MutableLiveData<String> avatarUrl=new MutableLiveData<>();
    private MutableLiveData<String> userName=new MutableLiveData<>();
    private MutableLiveData<Integer> followerCount=new MutableLiveData<>();
    private MutableLiveData<Integer> followingCount=new MutableLiveData<>();
    private MutableLiveData<Integer> runningPoint=new MutableLiveData<>();
    //Huy hiệu cập nhật sau
    private MutableLiveData<UserInfo> user=new MutableLiveData<>();

    public void init(String userID) {
        this.userID = userID;
        FirebaseDatabase.getInstance().getReference()
                .child("UserInfo")
                .child(userID)
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        // Statistic
                        user.setValue(task.getResult().getValue(UserInfo.class));
                        fetchActivities();
                        setFollowCount();
                    }
                }) ;
    }

    @BindingAdapter({"avatarUrl"})
    public static void setProfilePicture(ImageView imageView, String url) {
        if (url == null) {
            Glide.with(imageView.getContext()).load(LoginActivity.DEFAULT_AVATAR_URL).into(imageView);
        }
        else
            Glide.with(imageView.getContext()).load(url).into(imageView);
    }

    public LiveData<String> getAvatarUrl() {
        return avatarUrl;
    }

    public LiveData<String> getUserName() {
        return userName;
    }

    public LiveData<Integer> getFollowerCount() {
        return followerCount;
    }

    public LiveData<Integer> getFollowingCount() {
        return followingCount;
    }

    public LiveData<Integer> getRunningPoint() {
        return runningPoint;
    }

    public void setOtherUserInfo() {
        FirebaseDatabase.getInstance().getReference()
                .child("UserInfo")
                .child(userID)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        avatarUrl.setValue((String) task.getResult().child("avatarURI").getValue());
                        userName.setValue((String) task.getResult().child("displayName").getValue());
            }
        });
    }

    public void setFollowCount() {
        FirebaseDatabase.getInstance().getReference()
                .child("Follow")
                .child(userID)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int follower = (int) task.getResult().child("followed").getChildrenCount();
                followerCount.setValue(follower);
                int following = (int) task.getResult().child("following").getChildrenCount();
                followingCount.setValue(following);
            }
        });
    }


    //region statistic
    //statistic
    public MutableLiveData<ArrayList<Double>> distance = new MutableLiveData<>();
    public MutableLiveData<ArrayList<String>> timeWorking = new MutableLiveData<>();
    public MutableLiveData<ArrayList<Integer>> workingCount = new MutableLiveData<>();
    public int currentPage = 0;
    private Calendar c = Calendar.getInstance();
    private LocalDate now = new LocalDate();
    List<Activity> activities = new ArrayList<>();
    private DatabaseReference activityRef = FirebaseDatabase.getInstance().getReference().child("Activity");
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    DecimalFormat df = new DecimalFormat("#.##");

    public void resetStatisticData() {
        distance = new MutableLiveData<>();
        timeWorking = new MutableLiveData<>();
        workingCount = new MutableLiveData<>();
        now = new LocalDate();
        activities = new ArrayList<>();
    }

    public void fetchActivities() {

        activities.clear();
        activityRef.child(user.getValue().getUserID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                HashMap map = (HashMap) task.getResult().getValue();
                if (map == null) {
                    timeWorking.setValue(new ArrayList<>(Arrays.asList("00:00:00", "00:00:00", "00:00:00")));
                    distance.setValue(new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0)));
                    workingCount.setValue(new ArrayList<>(Arrays.asList(0, 0, 0)));

                    return;
                }
                for (Object o : map.values().toArray()) {
                    activities.add(toActivity((HashMap) o));
                }

                new Runnable() {
                    @Override
                    public void run() {
                        getWeekStatistic();
                        getMonthStatistic();
                        getYearStatistic();
                    }
                }.run();
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
                (ArrayList<LatLng>) map.get("latLngArrayList")
        );
    }


    private String timeConvert(long sec) {
        java.sql.Date d = new java.sql.Date(sec * 1000L);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss"); // HH for 0-23
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        String time = df.format(d);
        return time;
    }

    public void getWeekStatistic() {
        LocalDate monday = now.withDayOfWeek(DateTimeConstants.MONDAY).minusDays(1);
        LocalDate sunday = now.withDayOfWeek(DateTimeConstants.SUNDAY).plusDays(1);
        List<Activity> weekActivity = activities.stream().filter(activity -> {
            int endIndex = activity.getDateCreated().indexOf(" ");
            String activityDate = activity.getDateCreated().substring(0, endIndex);
            LocalDate actDate = null;
            try {
                Date tempDate = mSimpleDateFormat.parse(activityDate);
                actDate = new LocalDate(tempDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            return actDate.isBefore(sunday) && actDate.isAfter(monday);
        }).collect(Collectors.toList());

        int weekWorkingCount = weekActivity.size();
        double weekDistance = 0;
        long secWorking = 0;
        String _timeWorking;
        for (Activity act : weekActivity
        ) {
            weekDistance += act.getDistance();
            secWorking += act.getDuration();
        }
        _timeWorking = timeConvert(secWorking);
        weekDistance /= 1000;
        String distanceFormat = df.format(weekDistance);
        weekDistance = Double.parseDouble(distanceFormat);
        ArrayList<String> workingTime = timeWorking.getValue();
        if (workingTime == null)
            workingTime = new ArrayList<>(Arrays.asList("00:00:00", "00:00:00", "00:00:00"));
        workingTime.set(0, _timeWorking);
        timeWorking.setValue(workingTime);

        ArrayList<Double> _distance = distance.getValue();
        if (_distance == null)
            _distance = new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0));
        _distance.set(0, weekDistance);
        distance.setValue(_distance);

        ArrayList<Integer> _workingCount = workingCount.getValue();
        if (_workingCount == null)
            _workingCount = new ArrayList<>(Arrays.asList(0, 0, 0));
        _workingCount.set(0, weekWorkingCount);
        workingCount.setValue(_workingCount);

    }

    public void getMonthStatistic() {
        LocalDate lastDay = now.dayOfMonth().withMaximumValue().plusDays(1);
        LocalDate firstDay = now.dayOfMonth().withMinimumValue().minusDays(1);
        List<Activity> monthActivity = activities.stream().filter(activity -> {
            int endIndex = activity.getDateCreated().indexOf(" ");
            String activityDate = activity.getDateCreated().substring(0, endIndex);
            LocalDate actDate = null;
            try {
                Date tempDate = mSimpleDateFormat.parse(activityDate);
                actDate = new LocalDate(tempDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return actDate.isBefore(lastDay) && actDate.isAfter(firstDay);
        }).collect(Collectors.toList());
        int monthWorkingCount = monthActivity.size();
        double monthDistance = 0;
        long secWorking = 0;
        String _timeWorking;
        for (Activity act : monthActivity
        ) {
            monthDistance += act.getDistance();
            secWorking += act.getDuration();
        }
        _timeWorking = timeConvert(secWorking);
        monthDistance /= 1000;
        String distanceFormat = df.format(monthDistance);
        monthDistance = Double.parseDouble(distanceFormat);
        ArrayList<String> workingTime = timeWorking.getValue();
        if (workingTime == null)
            workingTime = new ArrayList<>(Arrays.asList("00:00:00", "00:00:00", "00:00:00"));
        workingTime.set(1, _timeWorking);
        timeWorking.setValue(workingTime);

        ArrayList<Double> _distance = distance.getValue();
        if (_distance == null)
            _distance = new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0));
        _distance.set(1, monthDistance);
        distance.setValue(_distance);

        ArrayList<Integer> _workingCount = workingCount.getValue();
        if (_workingCount == null)
            _workingCount = new ArrayList<>(Arrays.asList(0, 0, 0));
        _workingCount.set(1, monthWorkingCount);
        workingCount.setValue(_workingCount);
    }

    public void getYearStatistic() {
        LocalDate lastDay = now.dayOfYear().withMaximumValue().plusDays(1);
        LocalDate firstDay = now.dayOfYear().withMinimumValue().minusDays(1);
        List<Activity> yearActivity = activities.stream().filter(activity -> {
            int endIndex = activity.getDateCreated().indexOf(" ");
            String activityDate = activity.getDateCreated().substring(0, endIndex);
            LocalDate actDate = null;
            try {
                Date tempDate = mSimpleDateFormat.parse(activityDate);
                actDate = new LocalDate(tempDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return actDate.isBefore(lastDay) && actDate.isAfter(firstDay);
        }).collect(Collectors.toList());
        int yearWorkingCount = yearActivity.size();
        double yearDistance = 0;
        long secWorking = 0;
        String _timeWorking;
        for (Activity act : yearActivity
        ) {
            yearDistance += act.getDistance();
            secWorking += act.getDuration();
        }
        _timeWorking = timeConvert(secWorking);
        yearDistance /= 1000;
        String distanceFormat = df.format(yearDistance);
        yearDistance = Double.parseDouble(distanceFormat);
        ArrayList<String> workingTime = timeWorking.getValue();
        if (workingTime == null)
            workingTime = new ArrayList<>(Arrays.asList("00:00:00", "00:00:00", "00:00:00"));
        workingTime.set(2, _timeWorking);
        timeWorking.setValue(workingTime);

        ArrayList<Double> _distance = distance.getValue();
        if (_distance == null)
            _distance = new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0));
        _distance.set(2, yearDistance);
        distance.setValue(_distance);

        ArrayList<Integer> _workingCount = workingCount.getValue();
        if (_workingCount == null)
            _workingCount = new ArrayList<>(Arrays.asList(0, 0, 0));
        _workingCount.set(2, yearWorkingCount);
        workingCount.setValue(_workingCount);
    }

    //endregion

    //region activity

    //endregion
}

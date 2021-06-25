package com.example.dailyrunning.home.find;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.example.dailyrunning.authentication.LoginActivity;
import com.example.dailyrunning.model.Activity;
import com.example.dailyrunning.model.MedalInfo;
import com.example.dailyrunning.model.UserInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static com.example.dailyrunning.user.UserViewModel.getMedal;
import static com.example.dailyrunning.user.UserViewModel.parseDistance;


public class OtherUserProfileViewModel extends ViewModel {
    public String userID;
    private MutableLiveData<String> avatarUrl=new MutableLiveData<>();
    private MutableLiveData<String> userName=new MutableLiveData<>();
    private MutableLiveData<Integer> runningPoint=new MutableLiveData<>();
    private MutableLiveData<UserInfo> selectedUser =new MutableLiveData<>();
    public MutableLiveData<ArrayList<String>> followerUid =new MutableLiveData<>();
    public MutableLiveData<ArrayList<String>> followingUid =new MutableLiveData<>();
    public MutableLiveData<String> title =new MutableLiveData<>();
    private ChildEventListener mChildEventListener;
    /*public void init(String userID) {
        this.userID = userID;
        if(mChildEventListener!=null)
        activityRef.child(selectedUser.getValue().getUserID()).removeEventListener(mChildEventListener);
        FirebaseDatabase.getInstance().getReference()
                .child("UserInfo")
                .child(userID)
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        // Statistic
                        selectedUser.setValue(task.getResult().getValue(UserInfo.class));
                        fetchActivities();
                        //setFollowCount();
                    }
                }) ;
    }*/

    public void getFollowInfo()
    {

        FirebaseDatabase.getInstance().getReference()
                .child("Follow")
                .child(userID).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                    return;
                ArrayList<String> followerID=new ArrayList<>();
                ArrayList<String> followingID=new ArrayList<>();
                ((Runnable) () -> {
                    dataSnapshot.child("followed").getChildren().forEach(follower->{
                    followerID.add(follower.getValue().toString());
                    });
                    followerUid.postValue(followerID);
                }).run();
                ((Runnable) () -> {
                    dataSnapshot.child("following").getChildren().forEach(follower->{
                        followingID.add(follower.getValue().toString());
                    });
                    followingUid.postValue(followingID);
                }).run();

            }
        });

    }


    public void onUserSelected(UserInfo userInfo)
    {
        this.userID=userInfo.getUserID();
        selectedUser.setValue(userInfo);
        fetchActivities();
        avatarUrl.setValue(userInfo.getAvatarURI());
        userName.setValue(userInfo.getDisplayName());
        //setFollowCount();
        getFollowInfo();
    }
    public void onUserSelected(String uid)
    {
        this.userID = uid;
        if(mChildEventListener!=null)
            activityRef.child(selectedUser.getValue().getUserID()).removeEventListener(mChildEventListener);
        FirebaseDatabase.getInstance().getReference()
                .child("UserInfo")
                .child(uid)
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                // Statistic
                selectedUser.setValue(task.getResult().getValue(UserInfo.class));
                this.userID=selectedUser.getValue().getUserID();
                fetchActivities();
                avatarUrl.setValue(selectedUser.getValue().getAvatarURI());
                userName.setValue(selectedUser.getValue().getDisplayName());
                getFollowInfo();
            }
        });


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


    public LiveData<Integer> getRunningPoint() {
        return runningPoint;
    }

/*    public void setOtherUserInfo() {
        FirebaseDatabase.getInstance().getReference()
                .child("UserInfo")
                .child(userID)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        avatarUrl.postValue((String) task.getResult().child("avatarURI").getValue());
                        userName.postValue((String) task.getResult().child("displayName").getValue());
            }
        });
    }*/




    //region statistic
    //statistic
    public MutableLiveData<ArrayList<Double>> distance = new MutableLiveData<>();
    public MutableLiveData<ArrayList<String>> timeWorking = new MutableLiveData<>();
    public MutableLiveData<ArrayList<Integer>> workingCount = new MutableLiveData<>();
    public MutableLiveData<ArrayList<MedalInfo>> medals = new MutableLiveData<>();

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
    private void loadMedal(double yearDistance) {
        ArrayList<MedalInfo> medalInfos = new ArrayList<>();
        if (yearDistance >= 1000) {
            for (int i = 1; i <= 5; i++) {
                medalInfos.add(getMedal(i, true));
            }
        } else if (yearDistance >= 500) {
            for (int i = 1; i <= 5; i++) {
                if (i == 5)
                    medalInfos.add(getMedal(i, false));
                else
                    medalInfos.add(getMedal(i, true));
            }
        } else if (yearDistance >= 200) {
            for (int i = 1; i <= 5; i++) {
                if (i >= 4)
                    medalInfos.add(getMedal(i, false));
                else
                    medalInfos.add(getMedal(i, true));
            }
        } else if (yearDistance >= 100) {
            for (int i = 1; i <= 5; i++) {
                if (i >= 3)
                    medalInfos.add(getMedal(i, false));
                else
                    medalInfos.add(getMedal(i, true));
            }
        } else if (yearDistance >= 50) {
            for (int i = 1; i <= 5; i++) {
                if (i >= 2)
                    medalInfos.add(getMedal(i, false));
                else
                    medalInfos.add(getMedal(i, true));
            }
        } else {
            for (int i = 1; i <= 5; i++) {
                medalInfos.add(getMedal(i, false));
            }
        }
        medals.postValue(medalInfos);
    }
    public void fetchActivities() {

        timeWorking.postValue(new ArrayList<>(Arrays.asList("00:00:00", "00:00:00", "00:00:00")));
        distance.postValue(new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0)));
        workingCount.postValue(new ArrayList<>(Arrays.asList(0, 0, 0)));
        activities.clear();
        mChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                activities.add(snapshot.getValue(Activity.class));
                new Runnable() {
                    @Override
                    public void run() {
                        getWeekStatistic();
                        getMonthStatistic();
                        getYearStatistic();
                    }
                }.run();
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };
        activityRef.child(selectedUser.getValue().getUserID()).addChildEventListener(mChildEventListener);
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

        weekDistance/=1000;
        weekDistance = parseDistance(weekDistance);

        ArrayList<String> workingTime = timeWorking.getValue();
        if (workingTime == null)
            workingTime = new ArrayList<>(Arrays.asList("00:00:00", "00:00:00", "00:00:00"));
        workingTime.set(0, _timeWorking);
        timeWorking.postValue(workingTime);

        ArrayList<Double> _distance = distance.getValue();
        if (_distance == null)
            _distance = new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0));
        _distance.set(0, weekDistance);
        distance.postValue(_distance);

        ArrayList<Integer> _workingCount = workingCount.getValue();
        if (_workingCount == null)
            _workingCount = new ArrayList<>(Arrays.asList(0, 0, 0));
        _workingCount.set(0, weekWorkingCount);
        workingCount.postValue(_workingCount);

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

        monthDistance/=1000;
        monthDistance = parseDistance(monthDistance);

        ArrayList<String> workingTime = timeWorking.getValue();
        if (workingTime == null)
            workingTime = new ArrayList<>(Arrays.asList("00:00:00", "00:00:00", "00:00:00"));
        workingTime.set(1, _timeWorking);
        timeWorking.postValue(workingTime);

        ArrayList<Double> _distance = distance.getValue();
        if (_distance == null)
            _distance = new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0));
        _distance.set(1, monthDistance);
        distance.postValue(_distance);

        ArrayList<Integer> _workingCount = workingCount.getValue();
        if (_workingCount == null)
            _workingCount = new ArrayList<>(Arrays.asList(0, 0, 0));
        _workingCount.set(1, monthWorkingCount);
        workingCount.postValue(_workingCount);
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
        yearDistance/=1000;
        yearDistance = parseDistance(yearDistance);

        ArrayList<String> workingTime = timeWorking.getValue();
        if (workingTime == null)
            workingTime = new ArrayList<>(Arrays.asList("00:00:00", "00:00:00", "00:00:00"));
        workingTime.set(2, _timeWorking);
        timeWorking.postValue(workingTime);

        ArrayList<Double> _distance = distance.getValue();
        if (_distance == null)
            _distance = new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0));
        _distance.set(2, yearDistance);
        distance.postValue(_distance);

        ArrayList<Integer> _workingCount = workingCount.getValue();
        if (_workingCount == null)
            _workingCount = new ArrayList<>(Arrays.asList(0, 0, 0));
        _workingCount.set(2, yearWorkingCount);
        workingCount.postValue(_workingCount);
        loadMedal(yearDistance);
    }

    //endregion

    //region activity

    //endregion
}

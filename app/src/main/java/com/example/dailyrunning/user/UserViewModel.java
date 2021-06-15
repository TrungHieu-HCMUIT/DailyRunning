package com.example.dailyrunning.user;


import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dailyrunning.authentication.LoginViewModel;
import com.example.dailyrunning.model.Activity;
import com.example.dailyrunning.model.GiftInfo;
import com.example.dailyrunning.user.UserViewModel;
import com.example.dailyrunning.home.HomeViewModel;
import com.example.dailyrunning.model.LatLng;
import com.example.dailyrunning.model.UserInfo;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class UserViewModel extends ViewModel {

    private MutableLiveData<UserInfo> currentUser;
    public Parcelable mMedalRecyclerViewState;
    public Parcelable mGiftRecyclerViewState;
    public Integer mScrollViewPosition;
    private UserNavigator mUserNavigator;
    private static final String EMAIL_PROVIDER_ID = "password";
    private static final String GOOGLE_PROVIDER_ID = "google.com";
    private static final String FACEBOOK_PROVIDER_ID = "facebook.com";
    private  MutableLiveData<List<GiftInfo>> gifts;
    private final DatabaseReference mUserInfoRef= FirebaseDatabase.getInstance().getReference().child("UserInfo");
    public MutableLiveData<String> avatarUri;

    public LiveData<UserInfo> getCurrentUser()
    {
        if(currentUser==null)
        {
            currentUser=new MutableLiveData<>();
        }
        return currentUser;
    }

    public LiveData<String> getAvatarUri()
    {
        if(avatarUri==null)
        {
            avatarUri=new MutableLiveData<>();
            avatarUri.setValue(currentUser.getValue().getAvatarURI());
        }
        return avatarUri;
    }

    public void getUserInfo(LoginViewModel.TaskCallBack taskCallBack)
    {
        DatabaseReference mCurrentUserRef = mUserInfoRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        AtomicBoolean result= new AtomicBoolean(true);
        mCurrentUserRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(this.getClass().getName(), task.getException().toString());
                taskCallBack.onError(task.getException());
                return;
            }
            DataSnapshot taskRes = task.getResult();
            if (taskRes.getValue()==null)
            {
                Log.e(this.getClass().getName(), "current user is nulll");
                taskCallBack.onError(new Exception("Current user is null"));
                return;
            }
            currentUser.setValue(taskRes.getValue(UserInfo.class));
            taskCallBack.onSuccess();
        });
    }
    public void putAvatarToFireStorage(Intent data)
    {
        Uri selectedImageUri = data.getData();
        FirebaseUser userInfo = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference mAvatarStorageReference = FirebaseStorage.getInstance().getReference().child("avatar_photos");

        //tạo ref mới trong folder avatar_photos/
        StorageReference photoRef = mAvatarStorageReference.child(currentUser.getValue().getUserID());
        //up hình lên
        photoRef.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot -> photoRef.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Uri userAvatarUri = uri;
                    //update avatarURI trong UserInfo
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(currentUser.getValue().getUserID());
                    userRef.child("avatarURI").setValue(userAvatarUri.toString());
                    //update profile của firebase user
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(userAvatarUri).build();
                    userInfo.updateProfile(profileUpdates).addOnCompleteListener(task ->avatarUri.setValue(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString()));
                }));
    }
    public void onChangeAvatarClick()
    {
        mUserNavigator.updateAvatarClick();
    }
    public LiveData<List<GiftInfo>> getGifts()
    {
        if(gifts==null)
        {
            gifts=new MutableLiveData<>();
            getGiftData();
        }

        return gifts;
    }
    private void getGiftData()
    {
        List<GiftInfo> giftData=new ArrayList<>();
        giftData.add(new GiftInfo(Uri.parse("Temp_uri"), "Provider 1", "Gift detail 1", (int) (Math.random() * 100), "temp_id"));
        giftData.add(new GiftInfo(Uri.parse("Temp_uri"), "Provider 1", "Gift detail 1", (int) (Math.random() * 100), "temp_id"));
        giftData.add(new GiftInfo(Uri.parse("Temp_uri"), "Provider 1", "Gift detail 1", (int) (Math.random() * 100), "temp_id"));
        giftData.add(new GiftInfo(Uri.parse("Temp_uri"), "Provider 1", "Gift detail 1", (int) (Math.random() * 100), "temp_id"));
        giftData.add(new GiftInfo(Uri.parse("Temp_uri"), "Provider 1", "Gift detail 1", (int) (Math.random() * 100), "temp_id"));
        giftData.add(new GiftInfo(Uri.parse("Temp_uri"), "Provider 1", "Gift detail 1", (int) (Math.random() * 100), "temp_id"));
        giftData.add(new GiftInfo(Uri.parse("Temp_uri"), "Provider 1", "Gift detail 1", (int) (Math.random() * 100), "temp_id"));
        giftData.add(new GiftInfo(Uri.parse("Temp_uri"), "Provider 1", "Gift detail 1", (int) (Math.random() * 100), "temp_id"));
        giftData.add(new GiftInfo(Uri.parse("Temp_uri"), "Provider 1", "Gift detail 1", (int) (Math.random() * 100), "temp_id"));
        gifts.setValue(giftData);
    }

    @BindingAdapter({"dobText"})
    public static void getText(TextInputEditText view, Date date)
    {
        if(date==null)
            return;
        SimpleDateFormat mDateFormat=new SimpleDateFormat("dd/MM/yyyy");

        String res=mDateFormat.format(date);
        if (view.getText().toString().equals(date))
            return;
        view.setText(res);
    }
    @BindingAdapter({"dobTextAttrChanged"})
    public static void setListener(TextInputEditText view, InverseBindingListener listener)
    {
       if (listener!=null)
           view.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence s, int start, int count, int after) {

               }

               @Override
               public void onTextChanged(CharSequence s, int start, int before, int count) {

               }

               @Override
               public void afterTextChanged(Editable s) {
                    listener.onChange();
               }
           });
    }
    @InverseBindingAdapter(attribute = "dobText")
    public static Date getText(View view)
    {
        SimpleDateFormat mDateFormat=new SimpleDateFormat("dd/MM/yyyy");
        EditText editText=(EditText)view;
        Date res= null;
        try {
            res = mDateFormat.parse(editText.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }


    @BindingAdapter({"pickerMin"})
    public static void setMinValueForNumberPicker(View view, int value)
    {
        NumberPicker numberPicker= (NumberPicker) view;
        numberPicker.setMinValue(value);
    }
    @BindingAdapter({"pickerMax"})
    public static void setMaxValueForNumberPicker(View view, int value)
    {
        NumberPicker numberPicker= (NumberPicker) view;
        numberPicker.setMaxValue(value);
    }
    @BindingAdapter({"pickerValue"})
    public static void setValueForNumberPicker(View view, int value)
    {
        NumberPicker numberPicker= (NumberPicker) view;
        numberPicker.setValue(value);
    }
    @BindingAdapter({"pickerValueAttrChanged"})
    public static void setNumberPickerListener(NumberPicker view, InverseBindingListener listener)
    {
        if (listener!=null)
           view.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
               @Override
               public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                   listener.onChange();
               }
           });
    }
    @InverseBindingAdapter(attribute = "pickerValue")
    public static  int getPickerValue(NumberPicker view)
    {
        return view.getValue();
    }
    @BindingAdapter({"user"})
    public static void setProfilePicture(ImageView imageView, FirebaseUser userInfo) {
        switch (userInfo.getProviderData().get(1).getProviderId()) {
            case EMAIL_PROVIDER_ID:
            case GOOGLE_PROVIDER_ID:
                Glide.with(imageView.getContext()).load(userInfo.getPhotoUrl()).into(imageView);
                break;
            case FACEBOOK_PROVIDER_ID:
                //user chưa update avt thì lấy của fb
                if (userInfo.getPhotoUrl().toString().contains("graph.facebook.com")) {
                    //https://graph.facebook.com/2511714412307915/picture
                    String fbUID = userInfo.getPhotoUrl().toString().
                            replace("https://graph.facebook.com/", "")
                            .replace("/picture", "");
                    GraphRequest request = GraphRequest.newGraphPathRequest(
                            AccessToken.getCurrentAccessToken(),
                            "/" +fbUID + "/picture?redirect=0&type=normal",
                            response -> {
                                JSONObject res = response.getJSONObject();
                                try {
                                    String avatarUrl = res.getJSONObject("data").getString("url");
                                    Glide.with(imageView.getContext()).load(avatarUrl).into(imageView);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });

                    request.executeAsync();
                }
                else
                {
                    Glide.with(imageView.getContext()).load(userInfo.getPhotoUrl()).into(imageView);
                }
                break;

        }
    }
    public void setNavigator(UserNavigator mUserNavigator)
    {
        this.mUserNavigator=mUserNavigator;
    }
    public void settingOnClick()
    {
        mUserNavigator.settingOnClick();
    }
    public void allGiftOnClick()
    {
        mUserNavigator.allGiftOnClick();
    }
    public void onLogOutClick()
    {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
    }
    public void updateInfo(UserInfo mNewInfo, onUpdateCallback callBack)
    {
        mUserInfoRef.child(mNewInfo.getUserID()).setValue(mNewInfo).addOnCompleteListener(task->{
            if(task.isSuccessful())
            {
                currentUser.setValue(mNewInfo);
                updateFirebaseUser(mNewInfo);
                callBack.onComplete(true);

            }
            else if(!task.isSuccessful())
            {
                callBack.onComplete(false);
            }
        });
    }
    private void updateFirebaseUser(UserInfo mNewInfo) {
        FirebaseUser mUser= FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest mRequest=new UserProfileChangeRequest.Builder().setDisplayName(mNewInfo.getDisplayName()).build();
        mUser.updateProfile(mRequest);

    }
    public void onBackPress()
    {
        mUserNavigator.pop();
    }

    public void addPoint(int pointAcquired) {
       UserInfo tempU= currentUser.getValue();
       tempU.addPoint(pointAcquired);
       currentUser.setValue(tempU);
       mUserInfoRef.child(tempU.getUserID()).child("point").setValue(tempU.getPoint());
    }

    public interface onUpdateCallback{
        void onComplete(boolean result);

    }


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
        activityRef.child(currentUser.getValue().getUserID()).get().addOnCompleteListener(task -> {
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
                // TODO: chỗ này tự sửa nha
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


}

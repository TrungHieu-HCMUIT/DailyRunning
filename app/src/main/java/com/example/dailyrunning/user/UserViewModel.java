package com.example.dailyrunning.user;


import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.example.dailyrunning.R;
import com.example.dailyrunning.authentication.LoginViewModel;
import com.example.dailyrunning.model.Activity;
import com.example.dailyrunning.model.GiftInfo;
import com.example.dailyrunning.model.MedalInfo;
import com.example.dailyrunning.model.UserInfo;
import com.example.dailyrunning.utils.RunningLoadingDialog;
import com.example.dailyrunning.utils.SetStepTargetDialogFragment;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
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
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.example.dailyrunning.authentication.RegisterFragment.isValidPassword;

public class UserViewModel extends ViewModel {

    private MutableLiveData<UserInfo> currentUser;
    public Parcelable mMedalRecyclerViewState;
    public Parcelable mGiftRecyclerViewState;
    public Integer mScrollViewPosition;
    private UserNavigator mUserNavigator;
    public static final String EMAIL_PROVIDER_ID = "password";
    public static final String GOOGLE_PROVIDER_ID = "google.com";
    public static final String FACEBOOK_PROVIDER_ID = "facebook.com";
    private MutableLiveData<ArrayList<GiftInfo>> gifts=new MutableLiveData<>();
    private final DatabaseReference mUserInfoRef = FirebaseDatabase.getInstance().getReference().child("UserInfo");
    public MutableLiveData<String> avatarUri;
    public MutableLiveData<ArrayList<String>> followerUid =new MutableLiveData<>();
    public MutableLiveData<ArrayList<String>> followingUid =new MutableLiveData<>();
    public MutableLiveData<String> currentPassword =new MutableLiveData<>();
    public MutableLiveData<String> newPassword =new MutableLiveData<>();
    public MutableLiveData<String> newPasswordRetype =new MutableLiveData<>();
    public MutableLiveData<Integer> targetStep=new MutableLiveData<>();
    public SetStepDialog setStepDialog;
    Calendar c = Calendar.getInstance();;
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyy");
    public RunningSnackBar snackBar;
    public MutableLiveData<Boolean> canChangePassword=new MutableLiveData<>();
    public LoginViewModel.LoadingDialog loadingDialog;
    String formattedDate = df.format(c.getTime());

    public MutableLiveData<Integer> step=new MutableLiveData<>();

    {
        step.setValue(0);
        gifts.setValue(new ArrayList<>());
        targetStep.setValue(2000);
        canChangePassword.setValue(false);
    }



    public void onChangePasswordClick()
    {
        loadingDialog.showDialog();
        new Runnable() {
            @Override
            public void run() {
                String crPass=currentPassword.getValue(),newPass=newPassword.getValue(),newPassRetype=newPasswordRetype.getValue();
                if(TextUtils.isEmpty(crPass)||TextUtils.isEmpty(newPass)||TextUtils.isEmpty(newPassRetype)) {
                    (new Handler()).postDelayed(() -> {
                        loadingDialog.dismissDialog();
                    },100);
                    snackBar.showSnackBar("Vui lòng nhập đầy đủ thông tin",null);
                    return;
                }

                if(!isValidPassword(newPass))
                {
                    (new Handler()).postDelayed(() -> {
                        loadingDialog.dismissDialog();
                    },100);
                    snackBar.showSnackBar("Mật khẩu mới không hợp lệ",null);
                    return;
                }
                if(!newPass.equals(newPassRetype)) {
                    (new Handler()).postDelayed(() -> {
                        loadingDialog.dismissDialog();
                    },100);
                    snackBar.showSnackBar("Mật khẩu mới không trùng khớp",null);
                    return;
                }
                AuthCredential credential = EmailAuthProvider
                        .getCredential(currentUser.getValue().getEmail(),crPass);

                FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credential)  .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        FirebaseAuth.getInstance().getCurrentUser().updatePassword(newPass).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                loadingDialog.dismissDialog();
                                snackBar.showSnackBar("Đổi mật khẩu thành công!",new Snackbar.Callback() {
                                    @Override
                                    public void onDismissed(Snackbar snackbar, int event) {
                                        super.onDismissed(snackbar, event);
                                        FirebaseAuth.getInstance().signOut();
                                    }
                                });

                            }
                        });
                    } else {
                        // Password is incorrect
                        loadingDialog.dismissDialog();
                        snackBar.showSnackBar("Sai mật khẩu",null
                        );
                    }
                });
            }
        }.run();

    }
    private void setStep()
    {
        FirebaseDatabase.getInstance().getReference()
                .child("Step")
                .child(currentUser.getValue().getUserID())
                .child(formattedDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    long data = snapshot.getValue(Long.class);
                    step.setValue(Integer.parseInt(String.valueOf(data)));
                }
            }
            @Override
            public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError error) {

            }
        });

    }
    public void setStepTarget()
    {
        if(targetStep.getValue()==null)
            return;
        setStepDialog.showDialog(new SetStepTargetDialogFragment.ResultCallBack() {
            @Override
            public void onResult(int res) {
                targetStep.setValue(res);
                if(targetStep.getValue()!=null&& targetStep.getValue()>=2000)
                    FirebaseDatabase.getInstance().getReference().child("Step").child(currentUser.getValue().getUserID()).child("target")
                            .setValue(targetStep.getValue());
            }
        },targetStep.getValue());

    }
    public void getFollowInfo()
    {
        followerUid.postValue(new ArrayList<>());
        followingUid.postValue(new ArrayList<>());
        FirebaseDatabase.getInstance().getReference()
                .child("Follow")
                .child(currentUser.getValue().getUserID()).get().addOnSuccessListener(dataSnapshot -> {
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

                });

    }
    public LiveData<UserInfo> getCurrentUser()
    {
        if(currentUser==null)
        {
            currentUser=new MutableLiveData<>();
        }
        return currentUser;
    }

    public LiveData<String> getAvatarUri() {
        if (avatarUri == null) {
            avatarUri = new MutableLiveData<>();
            avatarUri.postValue(currentUser.getValue().getAvatarURI());
        }
        return avatarUri;
    }

    public void getUserInfo(LoginViewModel.TaskCallBack taskCallBack) {
        DatabaseReference mCurrentUserRef = mUserInfoRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        AtomicBoolean result = new AtomicBoolean(true);
        mCurrentUserRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(this.getClass().getName(), task.getException().toString());
                taskCallBack.onError(task.getException());
                return;
            }
            DataSnapshot taskRes = task.getResult();
            if (taskRes.getValue() == null) {
                Log.e(this.getClass().getName(), "current user is nulll");
                taskCallBack.onError(new Exception("Current user is null"));
                return;
            }
            currentUser.setValue(taskRes.getValue(UserInfo.class));
            getStepTarget();
            setStep();
            canChangePassword.setValue(getChangePasswordCapability());
            getFollowInfo();
            taskCallBack.onSuccess();
        });
    }

    private void getStepTarget() {
        FirebaseDatabase.getInstance().getReference()
                .child("Step").child(currentUser.getValue().getUserID()).child("target")
                .get().addOnSuccessListener(dataSnapshot -> {
                if(dataSnapshot.exists())
                    targetStep.setValue(dataSnapshot.getValue(Integer.class));
        });
    }

    public void putAvatarToFireStorage(Intent data) {
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
                    assert userInfo != null;
                    userInfo.updateProfile(profileUpdates).addOnCompleteListener(task -> avatarUri.postValue(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString()));
                    UserInfo newInfo=currentUser.getValue();
                    newInfo.setAvatarURI(userAvatarUri.toString());
                    currentUser.setValue(newInfo);
                    new Runnable() {
                        @Override
                        public void run() {
                            updatePostData();
                        }
                    }.run();
                }));

    }
    public void updatePostData()
    {
        DatabaseReference postRef=FirebaseDatabase.getInstance().getReference()
                .child("Post").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        postRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                dataSnapshot.getChildren().forEach(snapshot->{
                    Map<String,Object> newData=new HashMap<>();
                    newData.put("ownerAvatarUrl",currentUser.getValue().getAvatarURI());
                    newData.put("ownerName",currentUser.getValue().getDisplayName());
                    snapshot.getRef().updateChildren(newData);
                });
            }
        });
    }

    public void onChangeAvatarClick() {
        mUserNavigator.updateAvatarClick();
    }

    public LiveData<ArrayList<GiftInfo>> getGifts() {
        if (gifts == null) {
            gifts = new MutableLiveData<>();
        }

        return gifts;
    }

    public boolean exchangeGift(GiftInfo giftInfo)
    {
        UserInfo user=currentUser.getValue();
        if( user.exchangeGift(giftInfo.getPoint()))
        {
            HashMap<String,Object> updatePointMap=new HashMap<>();
            updatePointMap.put("point",user.getPoint());
            FirebaseDatabase.getInstance().getReference().child("UserInfo").child(user.getUserID()).updateChildren(updatePointMap);
            return true;
        }
        else
        {
            return false;
        }
    }
    public void getGiftData() {
        if(!gifts.getValue().isEmpty())
            return;
        FirebaseDatabase.getInstance().getReference().child("Gift").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                ArrayList<GiftInfo> giftData =gifts.getValue();
                giftData.add(snapshot.getValue(GiftInfo.class));
                gifts.setValue(giftData);
            }

            @Override
            public void onChildChanged(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

                ArrayList<GiftInfo> giftData =gifts.getValue();
                GiftInfo changedItem=snapshot.getValue(GiftInfo.class);
                int index=-1;
                for(int i=0;i<giftData.size();i++)
                {
                    if(giftData.get(i).getID().equals(changedItem.getID())) {
                        index = i;
                        break;
                    }
                }
                if(index!=-1)
                {
                    giftData.set(index,changedItem);
                    gifts.setValue(giftData);
                }
            }

            @Override
            public void onChildRemoved(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot snapshot) {
                ArrayList<GiftInfo> giftData =gifts.getValue();
                GiftInfo deletedItem =snapshot.getValue(GiftInfo.class);
                int index=-1;
                for(int i=0;i<giftData.size();i++)
                {
                    if(giftData.get(i).getID().equals(deletedItem.getID())) {
                        index = i;
                        break;
                    }
                }
                if(index!=-1)
                {
                    giftData.remove(index);
                    gifts.setValue(giftData);
                }
            }

            @Override
            public void onChildMoved(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError error) {

            }
        });
    }

    @BindingAdapter({"dobText"})
    public static void getText(TextInputEditText view, Date date) {
        if (date == null)
            return;
        SimpleDateFormat mDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        String res = mDateFormat.format(date);
        if (view.getText().toString().equals(date))
            return;
        view.setText(res);
    }

    @BindingAdapter({"dobTextAttrChanged"})
    public static void setListener(TextInputEditText view, InverseBindingListener listener) {
        if (listener != null)
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
    public static Date getText(View view) {
        SimpleDateFormat mDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        EditText editText = (EditText) view;
        Date res = null;
        try {
            res = mDateFormat.parse(editText.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }


    @BindingAdapter({"pickerMin"})
    public static void setMinValueForNumberPicker(View view, int value) {
        NumberPicker numberPicker = (NumberPicker) view;
        numberPicker.setMinValue(value);
    }

    @BindingAdapter({"pickerMax"})
    public static void setMaxValueForNumberPicker(View view, int value) {
        NumberPicker numberPicker = (NumberPicker) view;
        numberPicker.setMaxValue(value);
    }

    @BindingAdapter({"pickerValue"})
    public static void postValueForNumberPicker(View view, int value) {
        NumberPicker numberPicker = (NumberPicker) view;
        numberPicker.setValue(value);
    }

    @BindingAdapter({"pickerValueAttrChanged"})
    public static void setNumberPickerListener(NumberPicker view, InverseBindingListener listener) {
        if (listener != null)
            view.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    listener.onChange();
                }
            });
    }

    @InverseBindingAdapter(attribute = "pickerValue")
    public static int getPickerValue(NumberPicker view) {
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
                            "/" + fbUID + "/picture?redirect=0&type=normal",
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
                } else {
                    Glide.with(imageView.getContext()).load(userInfo.getPhotoUrl()).into(imageView);
                }
                break;

        }
    }

    public void setNavigator(UserNavigator mUserNavigator) {
        this.mUserNavigator = mUserNavigator;
    }

    public void settingOnClick() {
        mUserNavigator.settingOnClick();
    }

    public void allGiftOnClick() {
        mUserNavigator.allGiftOnClick();
    }

    public void onLogOutClick() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
    }

    public void updateInfo(UserInfo mNewInfo, OnTaskComplete callBack) {
        mUserInfoRef.child(mNewInfo.getUserID()).setValue(mNewInfo).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                currentUser.postValue(mNewInfo);
                updateFirebaseUser(mNewInfo);
                callBack.onComplete(true);

            } else if (!task.isSuccessful()) {
                callBack.onComplete(false);
            }
        });
    }

    private void updateFirebaseUser(UserInfo mNewInfo) {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest mRequest = new UserProfileChangeRequest.Builder().setDisplayName(mNewInfo.getDisplayName()).build();
        mUser.updateProfile(mRequest);
        updatePostData();
    }

    public void onBackPress() {
        mUserNavigator.pop();
    }

    public void addPoint(int pointAcquired) {
        UserInfo tempU = currentUser.getValue();
        tempU.addPoint(pointAcquired);
        currentUser.postValue(tempU);
        mUserInfoRef.child(tempU.getUserID()).child("point").setValue(tempU.getPoint());
    }

    public interface OnTaskComplete {
        void onComplete(boolean result);

    }


    //statistic
    public MutableLiveData<ArrayList<Double>> distance = new MutableLiveData<>();
    public MutableLiveData<ArrayList<String>> timeWorking = new MutableLiveData<>();
    public MutableLiveData<ArrayList<Integer>> workingCount = new MutableLiveData<>();
    public MutableLiveData<ArrayList<MedalInfo>> medals = new MutableLiveData<>();
    private LocalDate now = new LocalDate();
    List<Activity> activities = new ArrayList<>();
    private DatabaseReference activityRef = FirebaseDatabase.getInstance().getReference().child("Activity");
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public void resetStatisticData() {
        distance = new MutableLiveData<>();
        timeWorking = new MutableLiveData<>();
        workingCount = new MutableLiveData<>();
        now = new LocalDate();
        activities = new ArrayList<>();
    }

    public void fetchActivities() {
        timeWorking.postValue(new ArrayList<>(Arrays.asList("00:00:00", "00:00:00", "00:00:00")));
        distance.postValue(new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0)));
        workingCount.postValue(new ArrayList<>(Arrays.asList(0, 0, 0)));
        activities.clear();
        activityRef.child(currentUser.getValue().getUserID()).addChildEventListener(new ChildEventListener() {
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
        });
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
        monthDistance /= 1000;
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

    public static MedalInfo getMedal(int rank, boolean acquired) {
        if (acquired)
            switch (rank) {
                case 1:
                    return new MedalInfo(R.drawable.medal_1, "Huy chương đồng", "Bạn là một người tập chạy bộ để rèn luyện sức khỏe. Hoàn thành 50km trong một năm để nhận được huy chương này.");
                case 2:
                    return new MedalInfo(R.drawable.medal_2, "Huy chương bạc", "Bạn là một người có hứng thú chạy bộ. Hoàn thành 100km trong một năm để nhận được huy chương này.");
                case 3:
                    return new MedalInfo(R.drawable.medal_3, "Huy chương vàng", "Bạn là một người có sức khỏe và thể lực rất tốt. Hoàn thành 200km trong một năm để nhận được huy chương này.");
                case 4:
                    return new MedalInfo(R.drawable.medal_4, "Huy chương đam mê", "Bạn là một người thực sự có niềm đam mê chạy bộ. Hoàn thành 500km trong một năm để nhận được huy chương này.");
                case 5:
                    return new MedalInfo(R.drawable.medal_5, "Huy chương vận động viên", "Bạn là một người có tình yêu mãnh liệt với chạy bộ. Hoàn thành 1000km trong một năm để nhận được huy chương này.");
                default:
                    return null;
            }
        else
            switch (rank) {
                case 1:
                    return new MedalInfo(R.drawable.medal_1_greyscale
                            , "Huy chương đồng", "Bạn là một người tập chạy bộ để rèn luyện sức khỏe. Hoàn thành 50km trong một năm để nhận được huy chương này.");
                case 2:
                    return new MedalInfo(R.drawable.medal_2_greyscale, "Huy chương bạc", "Bạn là một người có hứng thú chạy bộ. Hoàn thành 100km trong một năm để nhận được huy chương này.");
                case 3:
                    return new MedalInfo(R.drawable.medal_3_greyscale, "Huy chương vàng", "Bạn là một người có sức khỏe và thể lực rất tốt. Hoàn thành 200km trong một năm để nhận được huy chương này.");
                case 4:
                    return new MedalInfo(R.drawable.medal_4_greyscale, "Huy chương đam mê", "Bạn là một người thực sự có niềm đam mê chạy bộ. Hoàn thành 500km trong một năm để nhận được huy chương này.");
                case 5:
                    return new MedalInfo(R.drawable.medal_5_greyscale, "Huy chương vận động viên", "Bạn là một người có tình yêu mãnh liệt với chạy bộ. Hoàn thành 1000km trong một năm để nhận được huy chương này.");
                default:
                    return null;
            }

    }

    static public double parseDistance(double distance)
    {

        String distanceFormat = String.format("%.2f",distance);
        distanceFormat=distanceFormat.replace(",",".");
        return Double.parseDouble(distanceFormat);
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

    boolean getChangePasswordCapability()
    {
        switch (FirebaseAuth.getInstance().getCurrentUser().getProviderData().get(1).getProviderId()) {
            case EMAIL_PROVIDER_ID:
                return true;
            case GOOGLE_PROVIDER_ID:
            case FACEBOOK_PROVIDER_ID:
              return false;

        }
        return false;
    }

    public interface SetStepDialog{
        void showDialog(SetStepTargetDialogFragment.ResultCallBack callBack,int initValue);
    }

    public interface RunningSnackBar{
        void showSnackBar(String content, Snackbar.Callback callback);
    }

}

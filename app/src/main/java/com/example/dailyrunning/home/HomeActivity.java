package com.example.dailyrunning.home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.dailyrunning.authentication.LoginActivity;
import com.example.dailyrunning.R;
import com.example.dailyrunning.authentication.LoginViewModel;
import com.example.dailyrunning.home.find.OtherUserProfileViewModel;
import com.example.dailyrunning.home.post.PostViewAdapter;
import com.example.dailyrunning.home.post.PostViewModel;
import com.example.dailyrunning.model.Post;
import com.example.dailyrunning.record.MapsActivity;
import com.example.dailyrunning.model.UserInfo;
import com.example.dailyrunning.user.ListUserViewModel;
import com.example.dailyrunning.user.UserViewModel;
import com.example.dailyrunning.user.stepcounter.MyPeriodicWork;
import com.example.dailyrunning.utils.RunningLoadingDialog;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity implements PostViewAdapter.PostUtils, LoginViewModel.LoadingDialog, UserViewModel.RunningSnackBar {

    private static final int RC_SIGN_IN = 1;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserInfoRef;
    private DatabaseReference mCurrentUserRef;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public static BitmapDescriptor startMarker;
    public static BitmapDescriptor endMarker;
    //Viewmodel to exchange data between fragment or activity
    private UserViewModel mUserViewModel;
    private Context mContext = HomeActivity.this;
    private static final String TAG = HomeActivity.class.getSimpleName();
    private NavController mNavController;
    private ImageView image;
    private HomeViewModel mHomeViewModel;
    private BottomNavigationViewEx bottomNavigationViewEx;
    public OtherUserProfileViewModel mOtherUserProfileViewModel;

    private PeriodicWorkRequest mPeriodicWorkRequest;

    public PostViewModel mPostViewModel;
    private RunningLoadingDialog mLoadingDialog;
    private RelativeLayout rootLayout;
    public ListUserViewModel mListUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //init firebaseAuth

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mLoadingDialog = new RunningLoadingDialog();
        (new Handler()).postDelayed(this::showDialog, 100);
        //init firebase database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserInfoRef = mFirebaseDatabase.getReference().child("UserInfo");

        //init viewmodel
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mHomeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        mPostViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        mListUserViewModel = new ViewModelProvider(this).get(ListUserViewModel.class);
        mOtherUserProfileViewModel = new ViewModelProvider(this).get(OtherUserProfileViewModel.class);

        //
        mHomeViewModel.mHomeActivity.setValue(this);

        mUserViewModel.loadingDialog = this;
        mUserViewModel.snackBar = this;
        mFirebaseAuth = FirebaseAuth.getInstance();
        setUpAuthStateListener();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        // Binding views by its id
        initWidgets();
        MapsInitializer.initialize(this);

        startMarker = BitmapDescriptorFactory.fromResource(R.drawable.marker_start);
        endMarker = BitmapDescriptorFactory.fromResource(R.drawable.marker_end);

        // Enable BottomNavigationViewEx
        setupBottomNavView();
        rootLayout = findViewById(R.id.home_activity_root);


        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(
                s -> {
                    Log.v("Firebase Token",s );

                }
        );
        FirebaseMessaging.getInstance().subscribeToTopic("broadcast");
    }


    public void hideNavBar() {
        findViewById(R.id.bottom_nav_bar).setVisibility(View.GONE);
    }

    public void showNavBar() {
        findViewById(R.id.bottom_nav_bar).setVisibility(View.VISIBLE);
    }

    //region firebaseAuth


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                mUserViewModel.getUserInfo(new LoginViewModel.TaskCallBack() {
                    @Override
                    public void onSuccess() {
                        onLoadData();
                    }

                    @Override
                    public void onError(Exception exception) {
                        if (mHomeViewModel.isActivityShow)
                            mUserViewModel.onLogOutClick();
                    }
                });
                bottomNavigationViewEx.setSelectedItemId(R.id.homeFragment);

            } else if (resultCode == RESULT_CANCELED) {
                //Toast.makeText(this, "Signed in canceled!", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == MapsActivity.RECORD_CODE) {
            if (resultCode == RESULT_OK) {
                int pointAcquired = data.getIntExtra("point", 0);
                mUserViewModel.addPoint(pointAcquired);
            } else if (resultCode == RESULT_CANCELED) {
                //Toast.makeText(this, "Canceled Record", Toast.LENGTH_SHORT).show();

            }

        }
    }

    private void setUpAuthStateListener() {
        mAuthStateListener = firebaseAuth -> checkAuthenticationState();
    }

    private void startLoginSession() {
        startActivityForResult(new Intent(this, LoginActivity.class), RC_SIGN_IN);

    }

    private void onLoadData() {
        mPostViewModel.getMyPosts();
        mPostViewModel.getFollowingUser();
        // This is PeriodicWorkRequest it repeats every 5 seconds.
        mPeriodicWorkRequest = new PeriodicWorkRequest.Builder(MyPeriodicWork.class,
                5, TimeUnit.SECONDS)
                .addTag("periodicWorkRequest")
                .build();
        WorkManager.getInstance().enqueue(mPeriodicWorkRequest);
    }

    private void checkAuthenticationState() {
        FirebaseUser mCurrentUser = mFirebaseAuth.getCurrentUser();
        if (mCurrentUser == null) {
            startLoginSession();
        } else {

            mUserViewModel.getUserInfo(new LoginViewModel.TaskCallBack() {
                @Override
                public void onSuccess() {
                    onLoadData();
                }

                @Override
                public void onError(Exception exception) {
                    if (mHomeViewModel.isActivityShow)
                        mUserViewModel.onLogOutClick();
                }
            });

        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        mHomeViewModel.isActivityShow = false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        mHomeViewModel.isActivityShow = true;

    }

    //endregion


    //region Bottom widget and Fragment
    private void initWidgets() {
        bottomNavigationViewEx = findViewById(R.id.bottomNavViewEx);
        mNavController = Navigation.findNavController(this, R.id.home_fragment_container);
    }

    private void setupBottomNavView() {
        //region Setup with navigationUI
        NavigationUI.setupWithNavController(bottomNavigationViewEx, mNavController);
        //endregion


        // Set Bottom Navigation View styles
        bottomNavigationViewEx.setBackground(null);
        bottomNavigationViewEx.getMenu().getItem(1).setEnabled(false);

        // Set Bottom Navigation View animations
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);

        FloatingActionButton newrecord = findViewById(R.id.newRecord);
        newrecord.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MapsActivity.class);
            startActivityForResult(intent, MapsActivity.RECORD_CODE);
        });
    }


    @Override
    public void onPostSelected(Post post, boolean isMap) {
        mPostViewModel.selectPost(post);
        if (isMap) {
            Log.i("OnMapSelected", post.getPostID());
            mNavController.navigate(R.id.action_homeFragment_to_mapViewFragment);
        } else {
            mNavController.navigate(R.id.action_homeFragment_to_postDetailFragment);
        }
    }

    @Override
    public void showDialog() {
        mLoadingDialog.show(getSupportFragmentManager(), "HomeActivityLoading");
    }

    @Override
    public void dismissDialog() {
        mLoadingDialog.dismiss();
    }

    @Override
    public void showSnackBar(String content, Snackbar.Callback callback) {
        Snackbar.make(rootLayout, content, Snackbar.LENGTH_SHORT).setTextColor(ContextCompat.getColor(this, R.color.color_palette_3))
                .addCallback(callback).show();
    }
}





/*  // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.dailyrunning",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/
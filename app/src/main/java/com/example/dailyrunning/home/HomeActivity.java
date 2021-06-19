package com.example.dailyrunning.home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dailyrunning.authentication.LoginActivity;
import com.example.dailyrunning.R;
import com.example.dailyrunning.authentication.LoginViewModel;
import com.example.dailyrunning.home.post.PostViewAdapter;
import com.example.dailyrunning.home.post.PostViewModel;
import com.example.dailyrunning.model.Post;
import com.example.dailyrunning.record.MapsActivity;
import com.example.dailyrunning.user.UserViewModel;
import com.example.dailyrunning.utils.RunningLoadingDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class HomeActivity extends AppCompatActivity implements PostViewAdapter.PostUtils, LoginViewModel.LoadingDialog {

    private static final int RC_SIGN_IN = 1;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserInfoRef;
    private DatabaseReference mCurrentUserRef;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //Viewmodel to exchange data between fragment or activity
    private UserViewModel mUserViewModel;
    private Context mContext = HomeActivity.this;
    private static final String TAG = HomeActivity.class.getSimpleName();
    private NavController mNavController;
    private ImageView image;
    private HomeViewModel mHomeViewModel;
    private BottomNavigationViewEx bottomNavigationViewEx;
    private PostViewModel mPostViewModel;
    private RunningLoadingDialog mLoadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //init firebaseAuth

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mLoadingDialog=new RunningLoadingDialog();
        showDialog();
        //init firebase database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserInfoRef = mFirebaseDatabase.getReference().child("UserInfo");


        //init viewmodel
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mHomeViewModel=new ViewModelProvider(this).get(HomeViewModel.class);
        mPostViewModel=new ViewModelProvider(this).get(PostViewModel.class);
        //
        mHomeViewModel.mHomeActivity.setValue(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        setUpAuthStateListener();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        // Binding views by its id
        initWidgets();


        // Enable BottomNavigationViewEx
        setupBottomNavView();


    }

    public void hideNavBar()
    {
        findViewById(R.id.bottom_nav_bar).setVisibility(View.GONE);
    }
    public void showNavBar()
    {
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

                    }

                    @Override
                    public void onError(Exception exception) {
                        if (mHomeViewModel.isActivityShow)
                            mUserViewModel.onLogOutClick();
                    }
                });
                bottomNavigationViewEx.setSelectedItemId(R.id.homeFragment);

                } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Signed in canceled!", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == MapsActivity.RECORD_CODE) {
            if (resultCode == RESULT_OK) {
                int pointAcquired=data.getIntExtra("point",0);
                mUserViewModel.addPoint(pointAcquired);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Signed in canceled!", Toast.LENGTH_SHORT).show();

            }

        }
    }

    private void setUpAuthStateListener() {
        mAuthStateListener = firebaseAuth -> checkAuthenticationState();
    }

    private void startLoginSession()
    {
        startActivityForResult(new Intent(this, LoginActivity.class), RC_SIGN_IN);

    }
    private void checkAuthenticationState() {
        FirebaseUser mCurrentUser = mFirebaseAuth.getCurrentUser();
        if (mCurrentUser == null) {
            startLoginSession();
        } else {

            mUserViewModel.getUserInfo(new LoginViewModel.TaskCallBack() {
                @Override
                public void onSuccess() {

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
        mHomeViewModel.isActivityShow=false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        mHomeViewModel.isActivityShow=true;

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
    public void onPostSelected(Post post,boolean isMap) {
        mPostViewModel.selectPost(post);
        if(isMap) {
            Log.i("OnMapSelected", post.getPostID());
            mNavController.navigate(Uri.parse("android-app://com.example.dailyrunning/map_view"));
        }
        else
        {
            mNavController.navigate(Uri.parse("android-app://com.example.dailyrunning/comment_view"));
        }

    }

    @Override
    public void showDialog() {
        mLoadingDialog.show(getSupportFragmentManager(),"HomeActivityLoading");
    }

    @Override
    public void dismissDialog() {
        mLoadingDialog.dismiss();
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
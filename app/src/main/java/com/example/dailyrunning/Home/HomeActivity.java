package com.example.dailyrunning.Home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dailyrunning.Authentication.LoginActivity;
import com.example.dailyrunning.R;
import com.example.dailyrunning.Record.MapsActivity;
import com.example.dailyrunning.User.UserFragment;
import com.example.dailyrunning.Model.UserInfo;
import com.example.dailyrunning.Utils.UserViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class HomeActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private UserInfo mCurrentUser;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserInfoRef;
    private DatabaseReference mCurrentUserRef;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //Viewmodel to exchange data between fragment or activity
    private UserViewModel mUserViewModel;
    private Context mContext = HomeActivity.this;

    private static final String TAG = HomeActivity.class.getSimpleName();

    private ImageView image;

    private BottomNavigationViewEx bottomNavigationViewEx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //init firebaseAuth
        mFirebaseAuth=FirebaseAuth.getInstance();
        setUpAuthStateListener();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        //init firebase database
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mUserInfoRef=mFirebaseDatabase.getReference().child("UserInfo");
        setUpDatabase();


        // Binding views by its id
        initWidgets();

        // Loading the default fragment (Post Fragment)
        loadFragment(new HomeFragment());

        // Enable BottomNavigationViewEx
        setupBottomNavView();

        FloatingActionButton newrecord =(FloatingActionButton) findViewById(R.id.newRecord);
        newrecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MapsActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    //region firebaseAuth
    private void showEmailVerificationDialog() {
        new AlertDialog.Builder(mContext)
                .setTitle("Verify your email")
                .setMessage("Please verify your email to continue using our app")


                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mFirebaseAuth.getCurrentUser().sendEmailVerification();
                    }
                })


                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mFirebaseAuth.signOut();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RC_SIGN_IN)
        {
            if(resultCode==RESULT_OK)
            {
                mCurrentUser=(UserInfo) data.getExtras().getSerializable("newUser");

                //update ui

                Toast.makeText(this, "Welcome "+mCurrentUser.getEmail(), Toast.LENGTH_SHORT).show();
            }
            else if(resultCode==RESULT_CANCELED)
            {
                Toast.makeText(this, "Signed in canceled!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }
    private void setUpAuthStateListener()
    {
        mAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                checkAuthenticationState();
            }
        };
    }
    private void checkAuthenticationState()
    {
        FirebaseUser mCurrentUser =mFirebaseAuth.getCurrentUser();
        if(mCurrentUser ==null)
        {
            startActivityForResult(new Intent(this, LoginActivity.class),RC_SIGN_IN);
        }
        else
        {
            mCurrentUserRef=mFirebaseDatabase.getReference().child("UserInfo").child(mCurrentUser.getUid());

            mCurrentUserRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(!task.isSuccessful())
                        return;
                    DataSnapshot taskRes=task.getResult();
                    HomeActivity.this.mCurrentUser =taskRes.getValue(UserInfo.class);
                    if(HomeActivity.this.mCurrentUser ==null)
                        return;
                    Log.v("UserGreet","Email: "+ HomeActivity.this.mCurrentUser.getEmail()+"\nID= "+ HomeActivity.this.mCurrentUser.getUserID());
                }
            });
            if(!mCurrentUser.isEmailVerified())
            {
                showEmailVerificationDialog();
            }
        }



    }



    //endregion

    //region firebase database
    private void setUpDatabase()
    {

    }
    //endregion



    //region Bottom widget and Fragment
    private void initWidgets() {
        bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewEx);
    }

    private void setupBottomNavView() {
        // Set Bottom Navigation View styles
        bottomNavigationViewEx.setBackground(null);
        bottomNavigationViewEx.getMenu().getItem(1).setEnabled(false);

        // Set Bottom Navigation View animations
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);

        // Register OnNavigationItemSelectedListener to bottomNavigationViewEx
        bottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                switch (item.getItemId()) {
                    case R.id.ic_home:
                        fragment = new HomeFragment();
                        break;

                    case R.id.ic_user:
                        fragment = new UserFragment();
                        break;
                }

                return loadFragment(fragment);
            }
        });
    }

    private boolean loadFragment(Fragment fragment) {
        // Switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }

        return false;
    }
    //endregion
}
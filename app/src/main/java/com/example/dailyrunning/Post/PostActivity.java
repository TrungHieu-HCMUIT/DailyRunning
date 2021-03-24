package com.example.dailyrunning.Post;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.dailyrunning.Find.FindFragment;
import com.example.dailyrunning.R;
import com.example.dailyrunning.Record.RecordActivity;
import com.example.dailyrunning.User.UserFragment;
import com.example.dailyrunning.data.UserInfo;
import com.example.dailyrunning.helper.UserViewModel;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.Arrays;

public class PostActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private UserInfo mCurrentUser;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //Viewmodel to exchange data between fragment or activity
    private UserViewModel mUserViewModel;
    private Context mContext = PostActivity.this;

    private static final String TAG = PostActivity.class.getSimpleName();

    private BottomNavigationViewEx bottomNavigationViewEx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //init firebase auth
        //init userViewModel
        mUserViewModel=new ViewModelProvider(this).get(UserViewModel.class);
        mFirebaseAuth=FirebaseAuth.getInstance();
        mAuthStateListener= firebaseAuth -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser == null) // signed out
            {
               Intent signinIntent= AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build(),
                            new AuthUI.IdpConfig.GoogleBuilder().build()
                        )).build();
               startActivityForResult(signinIntent,RC_SIGN_IN);
            }
            else
            {
                mCurrentUser=new UserInfo(currentUser.getDisplayName(),currentUser.getEmail(),0,1,null,160,50,null);
                mUserViewModel.select(mCurrentUser);


                Toast.makeText(mContext, "Welcome "+ currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
            }
        };
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        //end firebase auth


        // Binding views by its id
        initWidgets();

        // Loading the default fragment (Post Fragment)
        loadFragment(new PostFragment());

        // Enable BottomNavigationViewEx
        setupBottomNavView();



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RC_SIGN_IN)
        {
            if(resultCode==RESULT_OK)
            {
                Toast.makeText(this, "Singed In", Toast.LENGTH_SHORT).show();
            }
            else if(resultCode==RESULT_CANCELED)
            {
                Toast.makeText(this, "Signed in canceled!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void initWidgets() {
        bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottom_nav_view_ex);
    }

    private void setupBottomNavView() {
        // Set Bottom Navigation View  styles, animations
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(true);

        // Register OnNavigationItemSelectedListener to bottomNavigationViewEx
        bottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                switch (item.getItemId()) {
                    case R.id.ic_post:
                        fragment = new PostFragment();
                        break;

                    case R.id.ic_record:
                        Intent record_activity_intent = new Intent(mContext, RecordActivity.class);
                        mContext.startActivity(record_activity_intent);
                        break;

                    case R.id.ic_find:
                        fragment = new FindFragment();
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
}
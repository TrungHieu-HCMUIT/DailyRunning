package com.example.dailyrunning.Post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.dailyrunning.Find.FindFragment;
import com.example.dailyrunning.R;
import com.example.dailyrunning.Record.RecordActivity;
import com.example.dailyrunning.User.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class PostActivity extends AppCompatActivity {

    private Context mContext = PostActivity.this;

    private static final String TAG = PostActivity.class.getSimpleName();

    private BottomNavigationViewEx bottomNavigationViewEx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // Binding views by its id
        initWidgets();

        // Loading the default fragment (Post Fragment)
        loadFragment(new PostFragment());

        // Enable BottomNavigationViewEx
        setupBottomNavView();
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
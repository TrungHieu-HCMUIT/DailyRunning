package com.example.dailyrunningforadmin.home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.dailyrunningforadmin.DataLoadListener;
import com.example.dailyrunningforadmin.GiftAdapter;
import com.example.dailyrunningforadmin.R;
import com.example.dailyrunningforadmin.authentication.LoginActivity;

import com.example.dailyrunningforadmin.databinding.ActivityHomeBinding;
import com.example.dailyrunningforadmin.model.GiftInfo;
import com.example.dailyrunningforadmin.viewmodel.HomeViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements DataLoadListener {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private static final int RC_SIGN_IN = 1;

    private Context mContext;

    private ActivityHomeBinding binding;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private HomeViewModel homeViewModel;

    private RecyclerView recyclerView;
    private GiftAdapter giftAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mContext = this;

        initFirebaseAuth();

        homeViewModel = new ViewModelProvider((ViewModelStoreOwner) mContext).get(HomeViewModel.class);
        homeViewModel.init(mContext);

        initRecycleView();

        initWidget();
    }

    private void initFirebaseAuth() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        setUpAuthStateListener();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private void initRecycleView() {
        recyclerView = findViewById(R.id.gift_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        giftAdapter = new GiftAdapter(homeViewModel.getGiftList().getValue());

        recyclerView.setAdapter(giftAdapter);
    }

    private void initWidget() {
        // region logoutButton
        binding.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
            }
        });
        // endregion

        // region addButton
        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        HomeActivity.this, R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.bottom_sheet_layout, (ConstraintLayout)findViewById(R.id.bottom_sheet_container));
                bottomSheetDialog.setContentView(bottomSheetView);

                BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
                mBehavior.setPeekHeight(1500);

                bottomSheetDialog.show();

                bottomSheetView.findViewById(R.id.exit_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.hide();
                    }
                });
            }
        });
        // endregion
    }

    @Override
    public void onGiftLoaded() {
        homeViewModel.getGiftList().observe(this, new Observer<ArrayList<GiftInfo>>() {
            @Override
            public void onChanged(ArrayList<GiftInfo> giftInfos) {
                giftAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {}
            else if (requestCode == RESULT_CANCELED) {
                Toast.makeText(this, R.string.sign_in_cancel, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void setUpAuthStateListener() {
        mAuthStateListener = firebaseAuth -> checkAuthenticationState();
    }

    private void checkAuthenticationState() {
        FirebaseUser mCurrentUser = mFirebaseAuth.getCurrentUser();
        if (mCurrentUser == null) {
            startLoginSession();
        }
    }

    private void startLoginSession() {
        startActivityForResult(new Intent(this, LoginActivity.class), RC_SIGN_IN);
    }
}
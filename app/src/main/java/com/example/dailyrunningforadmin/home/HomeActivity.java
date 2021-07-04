package com.example.dailyrunningforadmin.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
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

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements DataLoadListener, PickImageListener {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private static final int PERMISSION_CODE = 0;

    private static final int RC_SIGN_IN = 1;
    private static final int RC_PICK_IMAGE = 2;

    private Uri selectedImageUri;
    GiftBottomSheetDialog bottomSheetDialog;

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
        homeViewModel.init(this);

        bottomSheetDialog = GiftBottomSheetDialog.getInstance(HomeActivity.this, R.style.BottomSheetDialogTheme, null);

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

        homeViewModel.getGiftList().observe((LifecycleOwner) this, giftInfos -> {
            giftAdapter.notifyDataSetChanged();
        });
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
                bottomSheetDialog = GiftBottomSheetDialog.getInstance(HomeActivity.this, R.style.BottomSheetDialogTheme, null);
                bottomSheetDialog.initView();
                bottomSheetDialog.show();
            }
        });
        // endregion
    }

    @Override
    public void onGiftLoaded() {
        giftAdapter.notifyDataSetChanged();
    }

    @Override
    public void pickImageFromGallery() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permissions, PERMISSION_CODE);
        }
        else {
            pickImage();
        }
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
        else if (requestCode == RC_PICK_IMAGE) {
            if (data != null) {
                Log.d(TAG, "onActivityResult");
                selectedImageUri = data.getData();
                ImageView imageView = (ImageView) bottomSheetDialog.getView().findViewById(R.id.gift_imageView);
                imageView.setImageURI(selectedImageUri);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImage();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Quyền truy cập bị từ chối", Toast.LENGTH_LONG);
                }
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, RC_PICK_IMAGE);
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
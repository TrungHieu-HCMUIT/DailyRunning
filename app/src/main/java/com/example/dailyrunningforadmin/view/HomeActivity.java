package com.example.dailyrunningforadmin.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dailyrunningforadmin.utils.DataLoadListener;
import com.example.dailyrunningforadmin.utils.GiftAdapter;
import com.example.dailyrunningforadmin.R;
import com.example.dailyrunningforadmin.utils.LoginNavigator;
import com.example.dailyrunningforadmin.view.authentication.ChangePasswordFragment;
import com.example.dailyrunningforadmin.view.authentication.LoginActivity;

import com.example.dailyrunningforadmin.databinding.ActivityHomeBinding;
import com.example.dailyrunningforadmin.model.GiftInfo;
import com.example.dailyrunningforadmin.utils.HomeActivityCallBack;
import com.example.dailyrunningforadmin.viewmodel.HomeViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity implements DataLoadListener, HomeActivityCallBack{

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
        homeViewModel.setContext(this);

        bottomSheetDialog = GiftBottomSheetDialog.getInstance(HomeActivity.this, R.style.BottomSheetDialogTheme, null);

        initRecycleView();

        initWidget();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            default:
                Log.d("Debug", "default");
                return super.onOptionsItemSelected(item);
        }
    }

    private void initFirebaseAuth() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        setUpAuthStateListener();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private void initRecycleView() {
        recyclerView = findViewById(R.id.gift_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        giftAdapter = new GiftAdapter(this, homeViewModel.getGiftList().getValue());

        recyclerView.setAdapter(giftAdapter);

        homeViewModel.getGiftList().observe((LifecycleOwner) this, giftInfos -> {
            giftAdapter.notifyDataSetChanged();
        });
    }

    private void initWidget() {
        binding.topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_change_password:
                        Intent intent = new Intent(getApplicationContext(), ChangePasswordFragment.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.rtl_anim, R.anim.ltr_anim);
                        return true;
                    case R.id.ic_logout:
                        homeViewModel.signOut();
                        return true;
                }
                return false;
            }
        });
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
    public void onSuccess() {

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
    public void initBottomSheet(GiftInfo gift) {
        bottomSheetDialog = GiftBottomSheetDialog.getInstance(HomeActivity.this, R.style.BottomSheetDialogTheme, gift);
        bottomSheetDialog.initView();
        bottomSheetDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                homeViewModel.init(mContext);
            }
            else if (requestCode == RESULT_CANCELED) {
                Toast.makeText(this, R.string.sign_in_cancel, Toast.LENGTH_LONG).show();
                finish();
            }
        }
        else if (requestCode == RC_PICK_IMAGE) {
            if (data != null) {
                selectedImageUri = data.getData();
                ImageView imageView = bottomSheetDialog.getGiftImageView();
                Glide.with(mContext).load(selectedImageUri).into(imageView);
                imageView.setTag("isSet");
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
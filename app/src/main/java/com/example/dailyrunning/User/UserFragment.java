package com.example.dailyrunning.User;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dailyrunning.Model.GiftInfo;
import com.example.dailyrunning.R;
import com.example.dailyrunning.Utils.GiftAdapter;
import com.example.dailyrunning.Utils.HomeViewModel;
import com.example.dailyrunning.Utils.MedalAdapter;
import com.example.dailyrunning.Utils.UserViewModel;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.taosif7.android.ringchartlib.RingChart;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class UserFragment extends Fragment {

    private static final int RC_PHOTO_PICKER = 101;
    private static final String EMAIL_PROVIDER_ID = "password";
    private static final String GOOGLE_PROVIDER_ID = "google.com";
    private static final String FACEBOOK_PROVIDER_ID = "facebook.com";

    private FirebaseAuth mFirebaseAuth;
    private RecyclerView mMedalRecyclerView;
    private View rootView;
    private SegmentTabLayout tab_layout;
    private ViewPager2 statisticalViewPager2;
    private RingChart mRingChart;
    private CircleImageView avatarView;
    private TextView userDisplayNameTextView;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mAvatarStorageReference;
    private com.example.dailyrunning.Model.UserInfo mCurrentUser;
    private Fragment mContext = UserFragment.this;
    private RecyclerView mGiftRecyclerView;
    private Button mSeeAllGiftButton;
    private NavController mNavController;
    private UserViewModel mUserViewModel;
    private ScrollView mScrollView;
    private ImageButton mChangeAvatarImageButton;
    private Button mLogOutButton;
    private HomeViewModel mHomeViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    private void onClickSetup() {
        mLogOutButton.setOnClickListener(v -> {
            mFirebaseAuth.signOut();
            LoginManager.getInstance().logOut();
        });
        rootView.findViewById(R.id.setting_imageButton).setOnClickListener(v -> {
            mNavController.navigate(R.id.action_userFragment_to_updateInfoFragment);
        });
    }

    private void restoreState() {
        if (mUserViewModel.mMedalRecyclerViewState != null)
            mMedalRecyclerView.getLayoutManager().onRestoreInstanceState(mUserViewModel.mMedalRecyclerViewState);
        if (mUserViewModel.mGiftRecyclerViewState != null)
            mGiftRecyclerView.getLayoutManager().onRestoreInstanceState(mUserViewModel.mGiftRecyclerViewState);
        if (mUserViewModel.mScrollViewPosition != null) {
            mScrollView.postDelayed(() -> mScrollView.scrollTo(0, mUserViewModel.mScrollViewPosition), 1);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mAvatarStorageReference = mFirebaseStorage.getReference().child("avatar_photos");
        //mCurrentUser = mFirebaseAuth.getCurrentUser();
        //init viewmodel
        mUserViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        mHomeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        //
        initView();

        onClickSetup();


        mUserViewModel.currentUser.observe(getActivity(), currentUser -> {
            mCurrentUser = currentUser;
            updateUI();
        });


        mHomeViewModel.mHomeActivity.getValue().showNavBar();

        mNavController = Navigation.findNavController(view);
        restoreState();


    }

    private void setUpViewAllGiftButton() {
        mSeeAllGiftButton.setOnClickListener(v -> mNavController.navigate(R.id.action_userFragment_to_giftFragment));
    }

    private void setUpGiftRecyclerView() {
        mGiftRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        List<GiftInfo> gifts = new ArrayList<>();
        GiftAdapter adapter = new GiftAdapter(gifts);
        mGiftRecyclerView.setAdapter(adapter);


        gifts.add(new GiftInfo(Uri.parse("Temp_uri"), "Provider 1", "Gift detail 1", (int) (Math.random() * 100), "temp_id"));
        gifts.add(new GiftInfo(Uri.parse("Temp_uri"), "Provider 1", "Gift detail 1", (int) (Math.random() * 100), "temp_id"));
        gifts.add(new GiftInfo(Uri.parse("Temp_uri"), "Provider 1", "Gift detail 1", (int) (Math.random() * 100), "temp_id"));
        gifts.add(new GiftInfo(Uri.parse("Temp_uri"), "Provider 1", "Gift detail 1", (int) (Math.random() * 100), "temp_id"));
        gifts.add(new GiftInfo(Uri.parse("Temp_uri"), "Provider 1", "Gift detail 1", (int) (Math.random() * 100), "temp_id"));
        gifts.add(new GiftInfo(Uri.parse("Temp_uri"), "Provider 1", "Gift detail 1", (int) (Math.random() * 100), "temp_id"));
        gifts.add(new GiftInfo(Uri.parse("Temp_uri"), "Provider 1", "Gift detail 1", (int) (Math.random() * 100), "temp_id"));
        gifts.add(new GiftInfo(Uri.parse("Temp_uri"), "Provider 1", "Gift detail 1", (int) (Math.random() * 100), "temp_id"));
        gifts.add(new GiftInfo(Uri.parse("Temp_uri"), "Provider 1", "Gift detail 1", (int) (Math.random() * 100), "temp_id"));
        adapter.notifyDataSetChanged();

    }

    private void updateUI() {


        userDisplayNameTextView.setText(
                mCurrentUser.getDisplayName().equals("") ? mCurrentUser.getEmail() : mCurrentUser.getDisplayName());
        loadAvatar();
        setUpMedalRecyclerView();
        setUpUpdateAvatar();
        setUpRingChart();
        setUpTabLayout();
        setUpGiftRecyclerView();
        setUpViewAllGiftButton();

    }

    private void loadAvatar() {
        UserInfo userInfo = mFirebaseAuth.getCurrentUser();
        //TODO: Change this
        switch (userInfo.getProviderId()) {
            case EMAIL_PROVIDER_ID:
            case GOOGLE_PROVIDER_ID:
                Glide.with(avatarView.getContext()).load(userInfo.getPhotoUrl()).into(avatarView);
                break;
            case FACEBOOK_PROVIDER_ID:
                GraphRequest request = GraphRequest.newGraphPathRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/" + userInfo.getUid() + "/picture?redirect=0&type=normal",
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                JSONObject res = response.getJSONObject();
                                try {
                                    String avatarUrl = res.getJSONObject("data").getString("url");
                                    Glide.with(avatarView.getContext()).load(avatarUrl).into(avatarView);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                request.executeAsync();
                break;

        }
    }

    private void setUpUpdateAvatar() {
        mChangeAvatarImageButton.setOnClickListener(v -> {
            UserInfo userInfo = mFirebaseAuth.getCurrentUser().getProviderData().get(1);

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);

        });
    }


    private void initView() {
        userDisplayNameTextView = (TextView) rootView.findViewById(R.id.name_textView);
        avatarView = rootView.findViewById(R.id.avatarView);
        mRingChart = rootView.findViewById(R.id.chart_concentric);
        tab_layout = rootView.findViewById(R.id.tl_2);
        statisticalViewPager2 = rootView.findViewById(R.id.statistical_viewPager2);
        mMedalRecyclerView = rootView.findViewById(R.id.medal_recycleView);
        mGiftRecyclerView = rootView.findViewById(R.id.gift_recyclerView);
        mSeeAllGiftButton = rootView.findViewById(R.id.see_all_button);
        mScrollView = rootView.findViewById(R.id.scroll_view);
        mChangeAvatarImageButton = rootView.findViewById(R.id.change_avatar_image_button);
        mLogOutButton = rootView.findViewById(R.id.log_out_button);
    }


    private void setUpRingChart() {
        mRingChart.showLabels(false);
        mRingChart.startAnimateLoading();


        new CountDownTimer(3000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                mRingChart.stopAnimateLoading(0.6f);

            }
        }.start();

    }

    private void setUpTabLayout() {
        tab_layout.setTabData(new String[]{"Theo tuần", "Theo tháng", "Theo năm"});
        StatisticalViewPagerAdapter statisticalViewPagerAdapter = new StatisticalViewPagerAdapter(this);
        statisticalViewPager2.setAdapter(statisticalViewPagerAdapter);
        tab_layout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                statisticalViewPager2.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        ViewPager2.OnPageChangeCallback pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tab_layout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        };
        statisticalViewPager2.registerOnPageChangeCallback(pageChangeCallback);

    }

    private void setUpMedalRecyclerView() {
        mMedalRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));

        List<Integer> medalIDs = new ArrayList<>();
        medalIDs.add(R.drawable.medal_1);
        medalIDs.add(R.drawable.medal_2);
        medalIDs.add(R.drawable.medal_3);
        medalIDs.add(R.drawable.medal_4);
        medalIDs.add(R.drawable.medal_5);
        medalIDs.add(R.drawable.medal_1);
        medalIDs.add(R.drawable.medal_2);
        medalIDs.add(R.drawable.medal_3);
        medalIDs.add(R.drawable.medal_4);
        medalIDs.add(R.drawable.medal_5);
        medalIDs.add(R.drawable.medal_1);
        medalIDs.add(R.drawable.medal_2);
        medalIDs.add(R.drawable.medal_3);
        medalIDs.add(R.drawable.medal_4);
        medalIDs.add(R.drawable.medal_5);

        MedalAdapter adapter = new MedalAdapter(medalIDs);
        mMedalRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //region update avatar
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            FirebaseUser userInfo = mFirebaseAuth.getCurrentUser();

            StorageReference photoRef = mAvatarStorageReference.child(selectedImageUri.getLastPathSegment());
            photoRef.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot -> photoRef.getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        Uri userAvatarUri = uri;
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(mCurrentUser.getUserID());
                        userRef.child("avatarURI").setValue(userAvatarUri.toString());
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(userAvatarUri).build();
                        userInfo.updateProfile(profileUpdates).addOnCompleteListener(task ->
                                Glide.with(avatarView.getContext()).load(userInfo.getPhotoUrl()).into(avatarView));
                    }));

        }
        //endregion
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUserViewModel.mGiftRecyclerViewState = mGiftRecyclerView.getLayoutManager().onSaveInstanceState();
        mUserViewModel.mMedalRecyclerViewState = mMedalRecyclerView.getLayoutManager().onSaveInstanceState();
        mUserViewModel.mScrollViewPosition = mScrollView.getScrollY();
    }
}
package com.example.dailyrunning.user;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.os.CountDownTimer;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dailyrunning.model.GiftInfo;
import com.example.dailyrunning.R;
import com.example.dailyrunning.model.MedalInfo;
import com.example.dailyrunning.model.UserInfo;
import com.example.dailyrunning.user.stepcounter.Singleton;
import com.example.dailyrunning.utils.GiftAdapter;
import com.example.dailyrunning.home.HomeViewModel;
import com.example.dailyrunning.utils.MedalAdapter;
import com.example.dailyrunning.databinding.FragmentUserBinding;
import com.example.dailyrunning.utils.SetStepTargetDialogFragment;
import com.flyco.tablayout.listener.OnTabSelectListener;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.ActionCodeSettings;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.CardSnapHelper;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class UserFragment extends Fragment implements UserNavigator, UserViewModel.SetStepDialog {

    private static final int RC_PHOTO_PICKER = 101;


    private FirebaseAuth mFirebaseAuth;

    private View rootView;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mAvatarStorageReference;
    private UserInfo mCurrentUser;

    private NavController mNavController;
    private UserViewModel mUserViewModel;
    private static final String TEXT_NUM_STEPS = " bước";
    private HomeViewModel mHomeViewModel;
    FragmentUserBinding binding;
    private MedalDialog mMedalDialog;

    private Context mContext;

    private ListUserViewModel mListUserViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentUserBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }


    private void restoreState() {
        if (mUserViewModel.mMedalRecyclerViewState != null)
            binding.medalRecycleView.getLayoutManager().onRestoreInstanceState(mUserViewModel.mMedalRecyclerViewState);
        if (mUserViewModel.mGiftRecyclerViewState != null)
            binding.giftRecyclerView.getLayoutManager().onRestoreInstanceState(mUserViewModel.mGiftRecyclerViewState);
        if (mUserViewModel.mScrollViewPosition != null) {
            binding.scrollView.postDelayed(() -> binding.scrollView.scrollTo(0, mUserViewModel.mScrollViewPosition), 1);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext=getContext();
        rootView = view;
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        //init viewmodel
        mUserViewModel = new ViewModelProvider((ViewModelStoreOwner) mContext).get(UserViewModel.class);
        mHomeViewModel = new ViewModelProvider((ViewModelStoreOwner) mContext).get(HomeViewModel.class);
        mListUserViewModel = new ViewModelProvider((ViewModelStoreOwner) mContext).get(ListUserViewModel.class);

        mUserViewModel.setNavigator(this);
        mUserViewModel.getGiftData();

        binding.setUserViewModel(mUserViewModel);
        binding.setLifecycleOwner((LifecycleOwner) mContext);
        mUserViewModel.setStepDialog=this;
        mMedalDialog=new MedalDialog();
        mMedalDialog = new MedalDialog();

        mUserViewModel.getCurrentUser().observe((LifecycleOwner) mContext, currentUser -> {
            if (currentUser==null)
                return;
            if (!isAdded())
                return;
            mCurrentUser = currentUser;
            mUserViewModel.resetStatisticData();
            mUserViewModel.fetchActivities();
            setUpTabLayout();
            updateUI();
            setFollowCount();
        });

        mHomeViewModel.mHomeActivity.getValue().showNavBar();

        mNavController = Navigation.findNavController(view);
        restoreState();
    }




    private void setUpGiftRecyclerView() {

        int activeCardLeft= (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                50,
                getResources().getDisplayMetrics());
        int cardWidth= (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                160,
                getResources().getDisplayMetrics());
        float cardsGap=  TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                23,
                getResources().getDisplayMetrics());
        binding.giftRecyclerView.setLayoutManager(new CardSliderLayoutManager(activeCardLeft,cardWidth,cardsGap));
        binding.giftRecyclerView.setHasFixedSize(true);
        try {
            new CardSnapHelper().attachToRecyclerView( binding.giftRecyclerView);
        }
        catch (Exception ex){}
        List<GiftInfo> gifts = new ArrayList<>();
        GiftAdapter adapter = new GiftAdapter(gifts);
        binding.giftRecyclerView.setAdapter(adapter);
        mUserViewModel.getGifts().observe((LifecycleOwner) mContext, giftData -> {
            gifts.clear();
            gifts.addAll(giftData);
            adapter.notifyDataSetChanged();
        });


    }

    private void updateUI() {
        binding.nameTextView.setText(
                mCurrentUser.getDisplayName().equals("") ? mCurrentUser.getEmail() : mCurrentUser.getDisplayName());
        setUpMedalRecyclerView();
        setUpRingChart();
        setUpTabLayout();
        setUpGiftRecyclerView();



        Singleton.getInstance().setTV(binding.stepTextView);

    }

    private void setUpRingChart() {
        binding.stepRingChart.showLabels(false);
        binding.stepRingChart.startAnimateLoading();


        new CountDownTimer(3000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                binding.stepRingChart.stopAnimateLoading(mUserViewModel.step.getValue()*1.0f/mUserViewModel.targetStep.getValue());
                mUserViewModel.step.observe((LifecycleOwner) mContext, step->{
                    binding.stepRingChart.stopAnimateLoading(step*1.0f/mUserViewModel.targetStep.getValue());
                });
                mUserViewModel.targetStep.observe((LifecycleOwner) mContext, step->{
                    binding.stepRingChart.stopAnimateLoading(mUserViewModel.step.getValue()*1.0f/step);
                });
            }
        }.start();

    }

    private void setUpTabLayout() {
        binding.statisticTabLayout.setTabData(new String[]{"Theo tuần", "Theo tháng", "Theo năm"});
        StatisticalViewPagerAdapter statisticalViewPagerAdapter = new StatisticalViewPagerAdapter(this,true);
        binding.statisticalViewPager2.setAdapter(statisticalViewPagerAdapter);

        binding.statisticTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                binding.statisticalViewPager2.setCurrentItem(position);
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
                binding.statisticTabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        };

        binding.statisticalViewPager2.registerOnPageChangeCallback(pageChangeCallback);

    }


    private void setFollowCount() {


        // region FollowerCountTextView
        binding.followerLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListUserViewModel.showUserList(mUserViewModel.followerUid.getValue(), "Người theo dõi");
                Navigation.findNavController(getView()).navigate(R.id.action_userFragment_to_listUserFragment);
            }
        });
        // endregion

        // region FollowingCountTextView
        binding.followingLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListUserViewModel.showUserList(mUserViewModel.followingUid.getValue(), "Đang theo dõi");
                Navigation.findNavController(getView()).navigate(R.id.action_userFragment_to_listUserFragment);
            }
        });
        // endregion
    }

    private void setUpMedalRecyclerView() {
        List<MedalInfo> medalInfos = new ArrayList<>();
        MedalAdapter adapter = new MedalAdapter(medalInfos,medalInfo -> {
            mMedalDialog.setMedal(medalInfo);
            mMedalDialog.show(getChildFragmentManager(),"medal dialog");
        });
        binding.medalRecycleView.setAdapter(adapter);

        mUserViewModel.medals.observe((LifecycleOwner) mContext, medals->{
            if(medals!=null)
            {
                medalInfos.clear();
                medalInfos.addAll(medals);
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //region update avatar
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            mUserViewModel.putAvatarToFireStorage(data);
            mUserViewModel.getAvatarUri().observe((LifecycleOwner) mContext, uri->{
                Glide.with(mContext).load(uri).into(binding.avatarView);
            });
        }
        //endregion
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUserViewModel.mGiftRecyclerViewState = binding.giftRecyclerView.getLayoutManager().onSaveInstanceState();
        mUserViewModel.mMedalRecyclerViewState = binding.medalRecycleView.getLayoutManager().onSaveInstanceState();
        mUserViewModel.mScrollViewPosition = binding.scrollView.getScrollY();
    }

    @Override
    public void settingOnClick() {
        mNavController.navigate(R.id.action_userFragment_to_updateInfoFragment);
    }

    @Override
    public void allGiftOnClick() {
        mNavController.navigate(R.id.action_userFragment_to_giftFragment);
    }

    @Override
    public void updateAvatarClick() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
    }



    @Override
    public void pop() {
        mNavController.popBackStack();
    }

    @Override
    public void showDialog(SetStepTargetDialogFragment.ResultCallBack resultCallBack,int initValue) {
        (new SetStepTargetDialogFragment()).showDialog(getChildFragmentManager(),initValue,resultCallBack);
    }
}
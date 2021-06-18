package com.example.dailyrunning.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.dailyrunning.model.GiftInfo;
import com.example.dailyrunning.R;
import com.example.dailyrunning.model.MedalInfo;
import com.example.dailyrunning.model.UserInfo;
import com.example.dailyrunning.user.stepcounter.DatabaseHandler;
import com.example.dailyrunning.user.stepcounter.Singleton;
import com.example.dailyrunning.user.stepcounter.StepModel;
import com.example.dailyrunning.utils.GiftAdapter;
import com.example.dailyrunning.home.HomeViewModel;
import com.example.dailyrunning.utils.MedalAdapter;
import com.example.dailyrunning.databinding.FragmentUserBinding;
import com.flyco.tablayout.listener.OnTabSelectListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.CardSnapHelper;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class UserFragment extends Fragment implements UserNavigator {

    private static final int RC_PHOTO_PICKER = 101;
    private static final String EMAIL_PROVIDER_ID = "password";
    private static final String GOOGLE_PROVIDER_ID = "google.com";
    private static final String FACEBOOK_PROVIDER_ID = "facebook.com";

    private FirebaseAuth mFirebaseAuth;

    private View rootView;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mAvatarStorageReference;
    private UserInfo mCurrentUser;
    private Fragment mContext = UserFragment.this;

    private NavController mNavController;
    private UserViewModel mUserViewModel;
    private static final String TEXT_NUM_STEPS = " bước";
    private HomeViewModel mHomeViewModel;
    FragmentUserBinding binding;
    private MedalDialog mMedalDialog;
    private DatabaseHandler db;
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
        rootView = view;
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        //init viewmodel
        mUserViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        mHomeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        mUserViewModel.setNavigator(this);
        binding.setUserViewModel(mUserViewModel);
        binding.setLifecycleOwner(getActivity());

        mMedalDialog=new MedalDialog();



        mMedalDialog = new MedalDialog();

        mUserViewModel.getCurrentUser().observe(getActivity(), currentUser -> {
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
        binding.giftRecyclerView.setLayoutManager(new CardSliderLayoutManager(getContext()));
        binding.giftRecyclerView.setHasFixedSize(true);
        try {
            new CardSnapHelper().attachToRecyclerView( binding.giftRecyclerView);

        }
        catch (Exception ex){}
        List<GiftInfo> gifts = new ArrayList<>();
        GiftAdapter adapter = new GiftAdapter(gifts);
        binding.giftRecyclerView.setAdapter(adapter);
        mUserViewModel.getGifts().observe(getActivity(), giftData -> {
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

        db = new DatabaseHandler(mContext.getContext());
        db.openDatabase();
        StepModel task = db.getTasks("1");
        Log.d("phu1",task.getTask()+"");
        Log.d("phu2",task.getId()+"");
        Bundle bundle= getArguments();
        if (bundle!=null) {
            //binding.stepTextView.setText(bundle.getInt("Step"));
            mUserViewModel.step.setValue(bundle.getInt("Step"));
        }
        Singleton.getInstance().getTV().setText(task.getId() + TEXT_NUM_STEPS);
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
                binding.stepRingChart.stopAnimateLoading(0.6f);
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
        FirebaseDatabase.getInstance().getReference()
                .child("Follow")
                .child(mUserViewModel.getCurrentUser().getValue().getUserID())
                .get().addOnCompleteListener(task -> {
            int follower = (int) task.getResult().child("followed").getChildrenCount();
            binding.textView3.setText("" + follower);
            int following = (int) task.getResult().child("following").getChildrenCount();
            binding.textView7.setText("" + following);
        });
    }

    private void setUpMedalRecyclerView() {

        List<MedalInfo> medalInfos = new ArrayList<>();
        medalInfos.add(new MedalInfo(R.drawable.medal_1,"Medal Name","This is medal detail"));
        medalInfos.add(new MedalInfo(R.drawable.medal_2,"Medal Name","This is medal detail"));
        medalInfos.add(new MedalInfo(R.drawable.medal_3,"Medal Name","This is medal detail"));
        medalInfos.add(new MedalInfo(R.drawable.medal_4,"Medal Name","This is medal detail"));
        medalInfos.add(new MedalInfo(R.drawable.medal_5,"Medal Name","This is medal detail"));
        medalInfos.add(new MedalInfo(R.drawable.medal_1,"Medal Name","This is medal detail"));
        medalInfos.add(new MedalInfo(R.drawable.medal_2,"Medal Name","This is medal detail"));
        medalInfos.add(new MedalInfo(R.drawable.medal_3,"Medal Name","This is medal detail"));
        medalInfos.add(new MedalInfo(R.drawable.medal_4,"Medal Name","This is medal detail"));
        medalInfos.add(new MedalInfo(R.drawable.medal_5,"Medal Name","This is medal detail"));
        medalInfos.add(new MedalInfo(R.drawable.medal_1,"Medal Name","This is medal detail"));
        medalInfos.add(new MedalInfo(R.drawable.medal_2,"Medal Name","This is medal detail"));
        medalInfos.add(new MedalInfo(R.drawable.medal_3,"Medal Name","This is medal detail"));
        medalInfos.add(new MedalInfo(R.drawable.medal_4,"Medal Name","This is medal detail"));
        medalInfos.add(new MedalInfo(R.drawable.medal_5,"Medal Name","This is medal detail"));

        MedalAdapter adapter = new MedalAdapter(medalInfos,medalInfo -> {

            mMedalDialog.setMedal(medalInfo);
            mMedalDialog.show(getChildFragmentManager(),"medal dialog");
        });
        binding.medalRecycleView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //region update avatar
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            mUserViewModel.putAvatarToFireStorage(data);
            mUserViewModel.getAvatarUri().observe(getActivity(),uri->{
                Glide.with(binding.avatarView.getContext()).load(uri).into(binding.avatarView);
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
}
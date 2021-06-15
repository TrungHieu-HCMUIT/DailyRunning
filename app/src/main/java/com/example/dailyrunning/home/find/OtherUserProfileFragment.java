package com.example.dailyrunning.home.find;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dailyrunning.R;
import com.example.dailyrunning.databinding.FragmentOtherUserProfileBinding;
import com.example.dailyrunning.model.MedalInfo;
import com.example.dailyrunning.user.MedalDialog;
import com.example.dailyrunning.user.StatisticalViewPagerAdapter;
import com.example.dailyrunning.user.UserViewModel;
import com.example.dailyrunning.utils.MedalAdapter;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class OtherUserProfileFragment extends Fragment {

    private static final String TAG = "OtherUserProfileFragment";

    private FragmentOtherUserProfileBinding binding;
    private UserViewModel mUserViewModel;
    private OtherUserProfileViewModel mOtherUserProfileViewModel;
    private MedalDialog mMedalDialog;

    private DatabaseReference mFollowRef_currentUserSide;
    private DatabaseReference mFollowRef_otherUserSide;
    private NavController mNavController;
    private String currentUserID;
    private String otherUserID;
    private static final String INTENT_UserID="UserID";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentOtherUserProfileBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle result = getArguments();
        otherUserID = result.getString("userID");
        mNavController= Navigation.findNavController(getActivity(),R.id.home_fragment_container);
        mUserViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        currentUserID = mUserViewModel.getCurrentUser().getValue().getUserID();

        // init database ref
        mFollowRef_currentUserSide = FirebaseDatabase.getInstance().getReference()
                .child("Follow")
                .child(currentUserID);
        mFollowRef_otherUserSide = FirebaseDatabase.getInstance().getReference()
                .child("Follow")
                .child(otherUserID);

        mOtherUserProfileViewModel = new ViewModelProvider(getActivity()).get(OtherUserProfileViewModel.class);
        mOtherUserProfileViewModel.init(otherUserID);
        binding.setOtherUserViewModel(mOtherUserProfileViewModel);
        binding.setLifecycleOwner(getActivity());
        mMedalDialog = new MedalDialog();

        initUI();

        setUpMedalRecyclerView();
        setUpTabLayout();
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

        MedalAdapter adapter = new MedalAdapter(medalInfos, medalInfo -> {
            mMedalDialog.setMedal(medalInfo);
            mMedalDialog.show(getChildFragmentManager(),"medal dialog");
        });
        binding.otherMedalRecycleView.setAdapter(adapter);
    }
    private void setUpTabLayout() {
        binding.otherStatisticTabLayout.setTabData(new String[]{"Theo tuần", "Theo tháng", "Theo năm"});
        StatisticalViewPagerAdapter statisticalViewPagerAdapter =
                new StatisticalViewPagerAdapter(this,false);
        binding.otherStatisticalViewPager2.setAdapter(statisticalViewPagerAdapter);

        binding.otherStatisticTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                binding.otherStatisticalViewPager2.setCurrentItem(position);
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
                binding.otherStatisticTabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        };

        binding.otherStatisticalViewPager2.registerOnPageChangeCallback(pageChangeCallback);

    }

    private void initUI() {
        checkIsFollowing();

        // region UserInfo
        mOtherUserProfileViewModel.setOtherUserInfo();
        FirebaseDatabase.getInstance().getReference()
                .child("Activity")
                .child(otherUserID)
                .get().addOnCompleteListener(task -> {
                    int numOfActivity = (int) task.getResult().getChildrenCount();
                    binding.activityCountTextView.setText(numOfActivity + " hoạt động");
        });
        // endregion

        // region Follow button
        binding.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFollowRef_currentUserSide.child("following").child(otherUserID).setValue(otherUserID);
                mFollowRef_otherUserSide.child("followed").child(currentUserID).setValue(currentUserID);
                mOtherUserProfileViewModel.setFollowCount();
                binding.followButton.setVisibility(View.INVISIBLE);
                binding.unfollowButton.setVisibility(View.VISIBLE);
            }
        });
        // endregion

        // region Unfollow button
        binding.unfollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFollowRef_currentUserSide.child("following").child(otherUserID).removeValue();
                mFollowRef_otherUserSide.child("followed").child(currentUserID).removeValue();
                mOtherUserProfileViewModel.setFollowCount();
                binding.unfollowButton.setVisibility(View.INVISIBLE);
                binding.followButton.setVisibility(View.VISIBLE);
            }
        });
        // endregion

        binding.activitySeeAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(INTENT_UserID,otherUserID);
                mNavController.navigate(R.id.action_otherUserProfile_to_otherUserActivityFragment, bundle);
            }
        });
    }

    private void checkIsFollowing() {
        AtomicBoolean isFollowing = new AtomicBoolean(false);
        mFollowRef_currentUserSide.child("following").get().addOnCompleteListener(task -> {
           if(task.isSuccessful()) {
               for (DataSnapshot ds : task.getResult().getChildren()) {
                   if (ds.getValue().toString().equals(otherUserID)) {
                       isFollowing.set(true);
                   }
               }
               if (isFollowing.get()) {
                   binding.unfollowButton.setVisibility(View.VISIBLE);
               }
               else {
                   binding.followButton.setVisibility(View.VISIBLE);
               }
           }
        });
    }
}
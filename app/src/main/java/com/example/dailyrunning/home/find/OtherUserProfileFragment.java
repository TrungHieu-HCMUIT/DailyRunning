package com.example.dailyrunning.home.find;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dailyrunning.R;
import com.example.dailyrunning.databinding.FragmentOtherUserProfileBinding;
import com.example.dailyrunning.model.MedalInfo;
import com.example.dailyrunning.user.MedalDialog;
import com.example.dailyrunning.user.StatisticalViewPagerAdapter;
import com.example.dailyrunning.utils.MedalAdapter;
import com.flyco.tablayout.listener.OnTabSelectListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class OtherUserProfileFragment extends Fragment {


    private FragmentOtherUserProfileBinding binding;
    private OtherUserProfileViewModel mOtherUserProfileViewModel;
    private MedalDialog mMedalDialog;

    private String userID;
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
        userID = result.getString("userID");
        Log.d("Other userID", userID);

        mOtherUserProfileViewModel=new ViewModelProvider(getActivity()).get(OtherUserProfileViewModel.class);
        mOtherUserProfileViewModel.init(userID);
        binding.setOtherUserViewModel(mOtherUserProfileViewModel);
        binding.setLifecycleOwner(getActivity());
        mMedalDialog = new MedalDialog();
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

}
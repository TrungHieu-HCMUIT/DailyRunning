package com.example.dailyrunning.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dailyrunning.R;
import com.example.dailyrunning.databinding.FragmentOtherUserProfileBinding;
import com.example.dailyrunning.utils.MedalAdapter;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class OtherUserProfile extends Fragment {


    private FragmentOtherUserProfileBinding binding;
    private OtherUserProfileViewModel mOtherUserProfileViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentOtherUserProfileBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mOtherUserProfileViewModel=new ViewModelProvider(getActivity()).get(OtherUserProfileViewModel.class);
        binding.setOtherUserViewModel(mOtherUserProfileViewModel);
        binding.setLifecycleOwner(getActivity());
        setUpMedalRecyclerView();
        setUpTabLayout();
    }
    private void setUpMedalRecyclerView() {


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
        binding.otherMedalRecycleView.setAdapter(adapter);
    }
    private void setUpTabLayout() {
        binding.otherStatisticTabLayout.setTabData(new String[]{"Theo tuần", "Theo tháng", "Theo năm"});
        //TODO thay user id hiện tại vào đây
        StatisticalViewPagerAdapter statisticalViewPagerAdapter = new StatisticalViewPagerAdapter(this, FirebaseAuth.getInstance().getUid());
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
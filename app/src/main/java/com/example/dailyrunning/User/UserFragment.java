package com.example.dailyrunning.User;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.dailyrunning.R;
import com.example.dailyrunning.Utils.MedalAdapter;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.firebase.auth.FirebaseAuth;
import com.taosif7.android.ringchartlib.RingChart;

import java.util.ArrayList;
import java.util.List;

public class UserFragment extends Fragment  {
    private FirebaseAuth mFirebaseAuth;
    private RecyclerView mMedalRecycleView;
    private View rootView;
    private SegmentTabLayout tab_layout;
    private ViewPager2 statisticalViewPager2;
    private RingChart mRingChart;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        rootView=view;
        mFirebaseAuth = FirebaseAuth.getInstance();
        TextView userTextView=(TextView) view.findViewById(R.id.user_textView);

        userTextView.setOnClickListener(v -> {
            mFirebaseAuth.signOut();
        });


        setUpRingChart();
        setUpMedalRecycleView();
        setUpTabLayout();

        return view;
    }

    private void setUpRingChart()
    {
        mRingChart=rootView.findViewById(R.id.chart_concentric);
        mRingChart.showLabels(false);
        mRingChart.startAnimateLoading();


        new CountDownTimer(3000,1000){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                mRingChart.stopAnimateLoading(0.6f);

            }
        }.start();

    }

    private void setUpTabLayout()
    {
        tab_layout=rootView.findViewById(R.id.tl_2);
        tab_layout.setTabData(new String[]{"Theo tuần","Theo tháng","Theo năm"});
        statisticalViewPager2=rootView.findViewById(R.id.statistical_viewPager2);
        StatisticalViewPagerAdapter statisticalViewPagerAdapter=new StatisticalViewPagerAdapter(this);
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
        ViewPager2.OnPageChangeCallback pageChangeCallback=new ViewPager2.OnPageChangeCallback() {
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
    private void setUpMedalRecycleView()
    {
        mMedalRecycleView = rootView.findViewById(R.id.medal_recycleView);
        mMedalRecycleView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL,false));

        List<Integer> medalIDs=new ArrayList<>();
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

        MedalAdapter adapter=new MedalAdapter(medalIDs);
        mMedalRecycleView.setAdapter(adapter);
    }
}

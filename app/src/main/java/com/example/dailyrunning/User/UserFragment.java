package com.example.dailyrunning.User;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyrunning.R;
import com.example.dailyrunning.Utils.MedalAdapter;
import com.example.dailyrunning.Utils.UserViewModel;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.SlidingTabLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class UserFragment extends Fragment {
    private UserViewModel mUserViewModel;
    private FirebaseAuth mFirebaseAuth;
    private RecyclerView mMedalRecycleView;
    private View rootView;
    private SegmentTabLayout tab_layout;
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

        setUpMedalRecycleView();
        setUpTabLayout();

        return view;
    }

    private void setUpTabLayout()
    {
        tab_layout=rootView.findViewById(R.id.tl_2);
        tab_layout.setTabData(new String[]{"Theo tuần","Theo tháng","Theo năm"});


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

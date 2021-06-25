package com.example.dailyrunning.utils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.dailyrunning.databinding.FragmentFollowerListBinding;
import com.example.dailyrunning.home.find.UserRowAdapter;
import com.example.dailyrunning.model.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;


public class FollowerListFragment extends Fragment {

    private static final String TAG = "FollowerListFragment";

    private String userId;

    private HashMap<String, String> mUserIdList = new HashMap<>();
    private ArrayList<UserInfo> mUserRowList = new ArrayList<>();
    private UserRowAdapter mAdapter;
    private NavController mNavController;

    private FragmentFollowerListBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFollowerListBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle result = getArguments();
        userId = result.getString("userId");

        mNavController = Navigation.findNavController(getView());

        // init recyclerView
        //mAdapter = new UserRowAdapter(mNavController, getContext(), mUserRowList);
        binding.followedUserRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.followedUserRecyclerView.setAdapter(mAdapter);

        populateData();

        initBackButton();
    }

    private void populateData() {
        // get user id list
        FirebaseDatabase.getInstance().getReference()
                .child("Follow")
                .child(userId)
                .child("followed")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DataSnapshot ds: task.getResult().getChildren()) {
                            String userId = ds.getValue(String.class);
                            mUserIdList.put(userId, userId);
                        }
                    }
        });

        FirebaseDatabase.getInstance().getReference()
                .child("UserInfo")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DataSnapshot ds: task.getResult().getChildren()) {
                            UserInfo user = ds.getValue(UserInfo.class);
                            if (mUserIdList.containsKey(user.getUserID())) {
                                mUserRowList.add(user);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
        });

    }

    private void initBackButton() {
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNavController.popBackStack();
            }
        });
    }
}
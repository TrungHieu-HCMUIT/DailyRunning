package com.example.dailyrunning.home;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dailyrunning.model.Activity;
import com.example.dailyrunning.model.Follow;
import com.example.dailyrunning.model.Post;
import com.example.dailyrunning.model.PostData;
import com.example.dailyrunning.R;
import com.example.dailyrunning.model.UserInfo;
import com.example.dailyrunning.model.UserRow;
import com.example.dailyrunning.user.UserViewModel;
import com.firebase.ui.auth.data.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class HomeFollowingFragment extends Fragment {
    private ArrayList<String> list;
    private Context context;
    private ArrayList<PostData> postList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PostViewAdapter postViewAdapter;
    private HomeViewModel mHomeViewModel;
    private UserViewModel mUserViewModel;
    private DatabaseReference mDatabase;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_following, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.home_following_recycleView);
        mUserViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);

        populateData();

        postViewAdapter = new PostViewAdapter(context, postList);
        recyclerView.setAdapter(postViewAdapter);



        mHomeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        if (mHomeViewModel.followingRecyclerViewState != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(mHomeViewModel.followingRecyclerViewState);
            mHomeViewModel.followingRecyclerViewState = null;
        }
    }

    private void populateData() {
        mUserViewModel.getCurrentUser().observe(getActivity(),
                userInfo -> {
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    Query query = mDatabase.child("Follow").child(userInfo.getUserID()).child("following");
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot postSnapshot1: snapshot.getChildren()) {
                                // TODO: handle the post
                                String key=postSnapshot1.getValue().toString();
                                Query query0 = mDatabase.child("UserInfo").child(key);
                                Query query1 = mDatabase.child("Activity").child(key);

                                query0.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot postSnapshot2: snapshot.getChildren()) {
                                            // TODO: handle the post
                                            UserInfo user = snapshot.getValue(UserInfo.class);
                                            query1.addValueEventListener(new ValueEventListener() {
                                                @RequiresApi(api = Build.VERSION_CODES.N)
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    for (DataSnapshot postSnapshot3: dataSnapshot.getChildren()) {
                                                        // TODO: handle the post
                                                        Activity activity = postSnapshot3.getValue(Activity.class);
                                                        postList.add(new PostData(user.getAvatarURI(), user.getDisplayName(), activity.getDateCreated(), activity.getDescribe(), activity.getDistance() + "", activity.getDuration(), activity.getPace(), activity.getPictureURI(), 20, 20));
                                                    }
                                                    postViewAdapter.notifyDataSetChanged();
                                                    recyclerView.setAdapter(postViewAdapter);
                                                    Collections.reverse(postList);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                });
    }







    //region savestate

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHomeViewModel.followingRecyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
    }

    //endregion
}
package com.example.dailyrunning.home;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dailyrunning.model.Post;
import com.example.dailyrunning.R;
import com.example.dailyrunning.user.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class HomeFollowingFragment extends Fragment {

    private static final String TAG = "HomeFollowingFragment";

    private Context context;
    private ArrayList<Post> postList = new ArrayList<>();
    private ArrayList<String> userFollowingIdList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PostViewAdapter postViewAdapter;
    private HomeViewModel mHomeViewModel;
    private UserViewModel mUserViewModel;

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
        postViewAdapter = new PostViewAdapter(context, postList);
        recyclerView.setAdapter(postViewAdapter);

        mUserViewModel.getCurrentUser().observe(getActivity(),user->{
            if(user!=null)
            userFollowingIdList = getUserFollowingIdList();

        });

        mHomeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        if (mHomeViewModel.followingRecyclerViewState != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(mHomeViewModel.followingRecyclerViewState);
            mHomeViewModel.followingRecyclerViewState = null;
        }
    }

    private ArrayList<String> getUserFollowingIdList() {
        ArrayList<String> list = new ArrayList<>();
    String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference()
                .child("Follow")
                .child(currentUserId).child("following")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DataSnapshot ds : task.getResult().getChildren()) {
                            String userId = ds.getValue(String.class);
                            list.add(userId);
                        }
                        setPostList(userFollowingIdList);
                    }
        });
        return list;
    }

    private void setPostList(ArrayList<String> userIdList) {
        // get posts list
        for (String userID: userIdList) {
            FirebaseDatabase.getInstance().getReference()
                    .child("Post")
                    .child(userID)
                    .get().addOnCompleteListener(task -> {
               if (task.isSuccessful()) {
                   for (DataSnapshot ds: task.getResult().getChildren()) {
                       Post post = ds.getValue(Post.class);
                       Log.d(TAG, post.getPostID());
                       postList.add(post);
                   }
                   postViewAdapter.notifyDataSetChanged();
                   sortPostList();
               }
            });
        }
    }

    private void sortPostList() {
        Collections.sort(postList, Collections.reverseOrder());
    }

    //region savestate

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHomeViewModel.followingRecyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
    }

    //endregion
}
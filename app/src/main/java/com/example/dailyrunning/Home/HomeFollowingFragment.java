package com.example.dailyrunning.Home;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dailyrunning.Authentication.LoginActivity;
import com.example.dailyrunning.Model.PostDataTest;
import com.example.dailyrunning.R;
import com.example.dailyrunning.Utils.HomeViewModel;

import java.util.ArrayList;

public class HomeFollowingFragment extends Fragment {

    private Context context;
    private ArrayList<PostDataTest> postList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PostViewAdapter postViewAdapter;
    private HomeViewModel mHomeViewModel;
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
        populateData();
        postViewAdapter = new PostViewAdapter(context, postList);
        recyclerView.setAdapter(postViewAdapter);
        mHomeViewModel=new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        if (mHomeViewModel.followingRecyclerViewState!=null)
        {
            recyclerView.getLayoutManager().onRestoreInstanceState(mHomeViewModel.followingRecyclerViewState);
            mHomeViewModel.followingRecyclerViewState=null;
        }
    }

    private void populateData() {
        postList.add(new PostDataTest(LoginActivity.DEFAULT_AVATAR_URL, "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
        postList.add(new PostDataTest(LoginActivity.DEFAULT_AVATAR_URL, "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
        postList.add(new PostDataTest(LoginActivity.DEFAULT_AVATAR_URL, "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
        postList.add(new PostDataTest(LoginActivity.DEFAULT_AVATAR_URL, "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
        postList.add(new PostDataTest(LoginActivity.DEFAULT_AVATAR_URL, "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
        postList.add(new PostDataTest(LoginActivity.DEFAULT_AVATAR_URL, "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
        postList.add(new PostDataTest(LoginActivity.DEFAULT_AVATAR_URL, "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
        postList.add(new PostDataTest(LoginActivity.DEFAULT_AVATAR_URL, "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
        postList.add(new PostDataTest(LoginActivity.DEFAULT_AVATAR_URL, "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
        postList.add(new PostDataTest(LoginActivity.DEFAULT_AVATAR_URL, "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
        postList.add(new PostDataTest(LoginActivity.DEFAULT_AVATAR_URL, "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
        postList.add(new PostDataTest(LoginActivity.DEFAULT_AVATAR_URL, "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
    }

    //region savestate

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHomeViewModel.followingRecyclerViewState=recyclerView.getLayoutManager().onSaveInstanceState();
    }

    //endregion
}
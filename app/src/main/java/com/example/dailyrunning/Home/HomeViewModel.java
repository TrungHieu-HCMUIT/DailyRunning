package com.example.dailyrunning.Home;

import android.os.Parcelable;

import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    public Parcelable userRecyclerViewState;
    public Parcelable followingRecyclerViewState;
    public Integer tabPosition;
    public Boolean isExpanded;
    public HomeScreenAdapter mHomeScreenAdapter;
}

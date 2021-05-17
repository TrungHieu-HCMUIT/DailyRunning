package com.example.dailyrunning.Home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyrunning.Model.PostDataTest;
import com.example.dailyrunning.R;
import com.example.dailyrunning.Utils.HomeViewModel;

import java.util.ArrayList;

public class HomeUserFragment extends Fragment {

    private Context context;
    private ArrayList<PostDataTest> postList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PostViewAdapter postViewAdapter;

    private HomeViewModel mHomeViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.home_user_recycleView);
        populateData();
        postViewAdapter = new PostViewAdapter(context, postList);
        recyclerView.setAdapter(postViewAdapter);
        mHomeViewModel=new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        if(mHomeViewModel.userRecyclerViewState !=null)
        {
            recyclerView.getLayoutManager().onRestoreInstanceState(mHomeViewModel.userRecyclerViewState);
            mHomeViewModel.userRecyclerViewState=null;
        }
    }

    private void populateData() {
        postList.add(new PostDataTest("https://scontent.fsgn5-4.fna.fbcdn.net/v/t1.6435-9/164769211_2992168127686941_4108362963617110188_n.jpg?_nc_cat=102&ccb=1-3&_nc_sid=09cbfe&_nc_ohc=eJgxmulLiyMAX9Utvc4&_nc_ht=scontent.fsgn5-4.fna&oh=f737beb89998a5af014a1ebf0ca07d4a&oe=6090954B", "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
        postList.add(new PostDataTest("https://scontent.fsgn5-4.fna.fbcdn.net/v/t1.6435-9/164769211_2992168127686941_4108362963617110188_n.jpg?_nc_cat=102&ccb=1-3&_nc_sid=09cbfe&_nc_ohc=eJgxmulLiyMAX9Utvc4&_nc_ht=scontent.fsgn5-4.fna&oh=f737beb89998a5af014a1ebf0ca07d4a&oe=6090954B", "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
        postList.add(new PostDataTest("https://scontent.fsgn5-4.fna.fbcdn.net/v/t1.6435-9/164769211_2992168127686941_4108362963617110188_n.jpg?_nc_cat=102&ccb=1-3&_nc_sid=09cbfe&_nc_ohc=eJgxmulLiyMAX9Utvc4&_nc_ht=scontent.fsgn5-4.fna&oh=f737beb89998a5af014a1ebf0ca07d4a&oe=6090954B", "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
        postList.add(new PostDataTest("https://scontent.fsgn5-4.fna.fbcdn.net/v/t1.6435-9/164769211_2992168127686941_4108362963617110188_n.jpg?_nc_cat=102&ccb=1-3&_nc_sid=09cbfe&_nc_ohc=eJgxmulLiyMAX9Utvc4&_nc_ht=scontent.fsgn5-4.fna&oh=f737beb89998a5af014a1ebf0ca07d4a&oe=6090954B", "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
        postList.add(new PostDataTest("https://scontent.fsgn5-4.fna.fbcdn.net/v/t1.6435-9/164769211_2992168127686941_4108362963617110188_n.jpg?_nc_cat=102&ccb=1-3&_nc_sid=09cbfe&_nc_ohc=eJgxmulLiyMAX9Utvc4&_nc_ht=scontent.fsgn5-4.fna&oh=f737beb89998a5af014a1ebf0ca07d4a&oe=6090954B", "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
    }

    //region save state

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHomeViewModel.userRecyclerViewState =recyclerView.getLayoutManager().onSaveInstanceState();
    }

    //endregion
}
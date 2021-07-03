package com.example.dailyrunning.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyrunning.home.find.OtherUserProfileViewModel;
import com.example.dailyrunning.home.post.PostViewAdapter;
import com.example.dailyrunning.home.post.PostViewModel;
import com.example.dailyrunning.model.Post;
import com.example.dailyrunning.R;
import com.example.dailyrunning.user.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class HomeUserFragment extends Fragment {

    private static final String TAG = "HomeUserFragment";

    private Context context;
    private ArrayList<Post> postList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PostViewAdapter postViewAdapter;
    private UserViewModel mUserViewModel;
    private HomeViewModel mHomeViewModel;
    private PostViewModel mPostViewModel;
    private NavController mNavController;

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

        mUserViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(UserViewModel.class);
        mPostViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(PostViewModel.class);
        mNavController= Navigation.findNavController((Activity) context,R.id.home_fragment_container);
        //populateData();
        postViewAdapter = new PostViewAdapter((HomeActivity) getActivity(), FirebaseAuth.getInstance().getUid(), postList, mNavController,false);
        recyclerView.setAdapter(postViewAdapter);
        listenMyPostChange();
        mHomeViewModel=new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        if(mHomeViewModel.userRecyclerViewState !=null)
        {
            recyclerView.getLayoutManager().onRestoreInstanceState(mHomeViewModel.userRecyclerViewState);
            mHomeViewModel.userRecyclerViewState = null;
        }
    }

    void listenMyPostChange()
    {
        mPostViewModel.myPosts.observe((LifecycleOwner) context, posts -> {
            if(posts!=null)
            {
                postViewAdapter.setPost(posts);
            }
        });
    }
    //region save state

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHomeViewModel.userRecyclerViewState =recyclerView.getLayoutManager().onSaveInstanceState();
    }

    //endregion
}
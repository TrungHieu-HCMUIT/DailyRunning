package com.example.dailyrunning.home;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dailyrunning.home.post.PostViewAdapter;
import com.example.dailyrunning.home.post.PostViewModel;
import com.example.dailyrunning.model.Post;
import com.example.dailyrunning.R;
import com.example.dailyrunning.user.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class HomeFollowingFragment extends Fragment {

    private static final String TAG = "HomeFollowingFragment";

    private Context context;
    private ArrayList<Post> postList = new ArrayList<>();
    private ArrayList<String> userFollowingIdList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NavController mNavController;
    private PostViewAdapter postViewAdapter;
    private HomeViewModel mHomeViewModel;
    private UserViewModel mUserViewModel;
    private PostViewModel mPostViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_following, container, false);
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.home_following_recycleView);
        mUserViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(UserViewModel.class);
        mPostViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(PostViewModel.class);

        mNavController = Navigation.findNavController((Activity) context,R.id.home_fragment_container);

        String userId = FirebaseAuth.getInstance().getUid();
        postViewAdapter = new PostViewAdapter((HomeActivity) context, userId, postList, mNavController,false);
        recyclerView.setAdapter(postViewAdapter);

        mUserViewModel.getCurrentUser().observe((LifecycleOwner) context, user->{
         /*   if(user!=null)
               userFollowingIdList = getUserFollowingIdList();*/
        });
        mPostViewModel.followingPosts.observe((LifecycleOwner) context, posts -> {
            if(posts!=null)
            {
                postViewAdapter.setPost(posts);
                ((HomeActivity)context).dismissDialog();
            }
        });
        mHomeViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(HomeViewModel.class);
        if (mHomeViewModel.followingRecyclerViewState != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(mHomeViewModel.followingRecyclerViewState);
            mHomeViewModel.followingRecyclerViewState = null;
        }
    }

    /*private ArrayList<String> getUserFollowingIdList() {
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
        postList.clear();
        postViewAdapter.notifyDataSetChanged();
        // get posts list
        if(userIdList.isEmpty())
            ((HomeActivity)context).dismissDialog();
        for (String userID: userIdList) {
            FirebaseDatabase.getInstance().getReference()
                    .child("Post")
                    .child(userID)
                    .get().addOnCompleteListener(task -> {
               if (task.isSuccessful()) {
                   for (DataSnapshot ds: task.getResult().getChildren()) {
                       Post post = ds.getValue(Post.class);
                       Log.d(TAG, post.getPostID());
                       if(post.getComments()==null)
                           post.setComments(new ArrayList<>());
                       postList.add(post);
                   }
                   postViewAdapter.notifyDataSetChanged();
                   sortPostList();
                   ((HomeActivity)context).dismissDialog();

               }
            });
        }
    }

    private void sortPostList() {
        Collections.sort(postList, Collections.reverseOrder());
    }*/

    //region savestate

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHomeViewModel.followingRecyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
    }

    //endregion
}
package com.example.dailyrunning.home.find;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dailyrunning.databinding.FragmentActivityListBinding;
import com.example.dailyrunning.home.HomeActivity;
import com.example.dailyrunning.home.post.PostViewAdapter;
import com.example.dailyrunning.model.Post;
import com.example.dailyrunning.user.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class ActivityListFragment extends Fragment {

    private static final String TAG = "ActivityListFragment";

    private String userId;

    private ArrayList<Post> postsList = new ArrayList<>();
    private PostViewAdapter postViewAdapter;
    private UserViewModel mUserViewModel;
    private NavController mNavController;

    private FragmentActivityListBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentActivityListBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNavController = Navigation.findNavController(getView());

        Bundle result = getArguments();
        userId = result.getString("userId");

        mUserViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);

        // init recyclerView
        postViewAdapter = new PostViewAdapter((HomeActivity) getActivity(), FirebaseAuth.getInstance().getUid(), postsList, mNavController,true);
        binding.activitiesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.activitiesRecyclerView.setAdapter(postViewAdapter);

        populateData();

        initBackButton();
    }

    private void populateData() {
        FirebaseDatabase.getInstance().getReference()
                .child("Post")
                .child(userId)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DataSnapshot ds: task.getResult().getChildren()) {
                            Post post = ds.getValue(Post.class);
                            postsList.add(post);
                        }
                        postsList.sort(Post::compareTo);
                        postViewAdapter.notifyDataSetChanged();
                    }
        });
    }

    private void initBackButton() {
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getView()).popBackStack();
            }
        });
    }
}
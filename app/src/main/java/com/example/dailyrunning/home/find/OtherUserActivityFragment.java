package com.example.dailyrunning.home.find;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dailyrunning.R;
import com.example.dailyrunning.databinding.FragmentOtherUserActivityBinding;
import com.example.dailyrunning.home.HomeViewModel;
import com.example.dailyrunning.home.PostViewAdapter;
import com.example.dailyrunning.model.Activity;
import com.example.dailyrunning.model.PostData;
import com.example.dailyrunning.model.UserInfo;
import com.example.dailyrunning.user.UserViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;


public class OtherUserActivityFragment extends Fragment {
    private static final String TAG = "Other User Activity Fragment";
    private Context context;
    private ArrayList<PostData> postList = new ArrayList<>();
    private RecyclerView recyclerView;
    private UserViewModel mUserViewModel;
    private PostViewAdapter postViewAdapter;
    ArrayList<String> listDate = new ArrayList<>();
    private String INTENT_DATECREATED="date";
    private FragmentOtherUserActivityBinding binding;
    private String otherUserID;
    private OtherUserProfileViewModel mOtherUserProfileViewModel;
    private static final String INTENT_UserID="UserID";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentOtherUserActivityBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getContext();
        Bundle result = getArguments();
        otherUserID = result.getString(INTENT_UserID);

        mUserViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);

        mOtherUserProfileViewModel = new ViewModelProvider(getActivity()).get(OtherUserProfileViewModel.class);
        mOtherUserProfileViewModel.init(otherUserID);

        recyclerView = (RecyclerView) view.findViewById(R.id.activities_recycler_view);

        populateData();
        postViewAdapter = new PostViewAdapter(context, postList);
        recyclerView.setAdapter(postViewAdapter);
    }
    @SuppressLint("RestrictedApi")
    private void populateData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rt = database.getReference();
        Query query = rt.child("Activity").child(otherUserID).orderByChild("dateCreated");
        query.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    // TODO: handle the post
                    Activity activity = postSnapshot.getValue(Activity.class);
                    listDate.add(activity.getDateCreated());
                    Log.d(TAG, "" + activity.getPace());
                    postList.add(new PostData(mOtherUserProfileViewModel.getAvatarUrl().getValue(), mOtherUserProfileViewModel.getUserName().getValue(), activity.getDateCreated(), activity.getDescribe(), activity.getDistance()+"", activity.getDuration(), activity.getPace(), activity.getPictureURI(), 20, 20));
                    postViewAdapter.notifyDataSetChanged();
                    Collections.reverse(postList);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }
}
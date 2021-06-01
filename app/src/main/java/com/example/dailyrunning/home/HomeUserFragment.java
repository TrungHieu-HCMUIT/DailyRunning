package com.example.dailyrunning.home;

import android.annotation.SuppressLint;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyrunning.model.Activity;
import com.example.dailyrunning.model.PostDataTest;
import com.example.dailyrunning.R;
import com.example.dailyrunning.user.UserViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeUserFragment extends Fragment {

    private Context context;
    private ArrayList<PostDataTest> postList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PostViewAdapter postViewAdapter;
    private UserViewModel mUserViewModel;
    private HomeViewModel mHomeViewModel;
    private String TAG="cac";
    private NavController mNavController;
    ArrayList<String> listDate = new ArrayList<>();
    private String INTENT_DATECREATED="date";

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

        mUserViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        mNavController= Navigation.findNavController(getActivity(),R.id.home_fragment_container);
        populateData();
        postViewAdapter = new PostViewAdapter(context, postList);
        recyclerView.setAdapter(postViewAdapter);
        mHomeViewModel=new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        if(mHomeViewModel.userRecyclerViewState !=null)
        {
            recyclerView.getLayoutManager().onRestoreInstanceState(mHomeViewModel.userRecyclerViewState);
            mHomeViewModel.userRecyclerViewState=null;
        }

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {
//                Bundle bundle = new Bundle();
//                bundle.putString(INTENT_DATECREATED,listDate.get(position));
//                mNavController.navigate(R.id.action_homeFragment_to_mapViewFragment, bundle);
            }

            @Override public void onLongItemClick(View view, int position) {
                // do whatever
            }
        }));
    }

    @SuppressLint("RestrictedApi")
    private void populateData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rt = database.getReference();
        mUserViewModel.getCurrentUser().observe(getActivity(),
                userInfo -> {
            if (userInfo==null)
                return;
                    Query query = rt.child("Activity").orderByChild("userID").equalTo(userInfo.getUserID());
                    query.addValueEventListener(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                // TODO: handle the post
                                Activity activity = postSnapshot.getValue(Activity.class);
                                listDate.add(activity.getDateCreated());
                                postList.add(new PostDataTest(userInfo.getAvatarURI(), userInfo.getDisplayName(), activity.getDateCreated(), activity.getDescribe(), activity.getDistance()+"", activity.getDuration()+"", activity.getPace()+"", userInfo.getAvatarURI(), 20, 20));
                                postViewAdapter.notifyDataSetChanged();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        }
                    });
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


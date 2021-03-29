package com.example.dailyrunning.User;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dailyrunning.R;
import com.example.dailyrunning.Utils.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class UserFragment extends Fragment {
    private UserViewModel mUserViewModel;
    private FirebaseAuth mFirebaseAuth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View userView= inflater.inflate(R.layout.fragment_user, null);


        mFirebaseAuth=FirebaseAuth.getInstance();
        TextView userTextView=(TextView) userView.findViewById(R.id.user_textView);

        userTextView.setOnClickListener(v -> {
            mFirebaseAuth.signOut();

        });
      /*  //TODO: add auth here
        //init userviewmodel
        mUserViewModel=new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        //getFirebase auth
        mUserViewModel.getFirebaseAuth().observe(getViewLifecycleOwner(),firebaseAuth -> {
            mFirebaseAuth=firebaseAuth;
        });

        UserInfo currentUser=null;
        mUserViewModel.getSelected().observe(getViewLifecycleOwner(),userInfo -> {
            userTextView.setText(userInfo.getDisplayName());
        });*/



        return userView;
    }



}

package com.example.dailyrunning.User;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.dailyrunning.R;
import com.example.dailyrunning.data.UserInfo;
import com.example.dailyrunning.helper.UserViewModel;

public class UserFragment extends Fragment {
    UserViewModel mUserViewModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View userView= inflater.inflate(R.layout.fragment_user, null);

        TextView userTextView=(TextView) userView.findViewById(R.id.user_textView);
        //TODO: add auth here
        //init userviewmodel
        mUserViewModel=new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        UserInfo currentUser=null;
        mUserViewModel.getSelected().observe(getViewLifecycleOwner(),userInfo -> {
            userTextView.setText(userInfo.getDisplayName());

        });



        return userView;
    }

    private void signOut(){

    }

}

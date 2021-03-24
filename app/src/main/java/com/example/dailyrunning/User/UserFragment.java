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

public class UserFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View userView= inflater.inflate(R.layout.fragment_user, null);

        TextView userTextView=(TextView) userView.findViewById(R.id.user_textView);
        //TODO: add auth here
        return userView;
    }
}

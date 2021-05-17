package com.example.dailyrunning.User;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.example.dailyrunning.R;


public class UpdateInfoFragment extends Fragment {


    private View rootView;
    private NumberPicker mHeightPicker;
    private NumberPicker mWeightPicker;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView=view;
        initView();
        setUpNumberPicker();
        viewFunctional();
    }

    private void setUpNumberPicker() {
        mHeightPicker.setMinValue(130);
        mHeightPicker.setMaxValue(220);
        mWeightPicker.setMinValue(30);
        mWeightPicker.setMaxValue(200);

    }

    private void viewFunctional() {
        rootView.findViewById(R.id.back_button).setOnClickListener(v->{
            getActivity().onBackPressed();
        });
    }

    private void initView() {
        mHeightPicker=rootView.findViewById(R.id.height_picker);
        mWeightPicker=rootView.findViewById(R.id.weight_picker);
    }
}
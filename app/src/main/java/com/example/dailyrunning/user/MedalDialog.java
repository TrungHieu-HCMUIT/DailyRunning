package com.example.dailyrunning.user;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dailyrunning.R;
import com.example.dailyrunning.model.MedalInfo;

import org.jetbrains.annotations.NotNull;


public class MedalDialog extends DialogFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_medal_dialog, container, false);
    }
    private MedalInfo medal;
    public MedalDialog() {}
    private View rootView;
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        rootView=view;
        ( (ImageView) rootView.findViewById(R.id.medal_image)).setImageResource(medal.getImageID());
        ( (TextView) rootView.findViewById(R.id.medal_name)).setText(medal.getMedalName());
        ( (TextView) rootView.findViewById(R.id.medal_detail)).setText(medal.getMedalDetail());
        ( (Button) rootView.findViewById(R.id.dismiss_button)).setOnClickListener(v->{
            getDialog().dismiss();
        });
    }

    public void setMedal(MedalInfo medal) {
        this.medal = medal;

    }
}
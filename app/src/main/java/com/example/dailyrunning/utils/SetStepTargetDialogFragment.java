package com.example.dailyrunning.utils;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.dailyrunning.R;
import com.example.dailyrunning.databinding.FragmentSetStepTargetDialogBinding;
import com.google.android.material.slider.Slider;

import org.jetbrains.annotations.NotNull;


public class SetStepTargetDialogFragment extends DialogFragment {


    FragmentSetStepTargetDialogBinding binding;
    ResultCallBack callBack;
    int initValue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentSetStepTargetDialogBinding.inflate(inflater,container,false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        setUpNumberSlider();
        binding.saveButton.setOnClickListener(v->{
            callBack.onResult((int) binding.numberSlider.getValue());
            dismiss();
        });
        binding.numberSlider.setValue(initValue);
        binding.cancelButton.setOnClickListener(v->{
            dismiss();
        });
    }


    @Override
    public void show(@NonNull @NotNull FragmentManager manager, @Nullable @org.jetbrains.annotations.Nullable String tag) {
        Dialog dialogFrg=getDialog();
        if (dialogFrg == null || !dialogFrg.isShowing()) {
            super.show(manager, tag);
        }
    }

    public void showDialog(FragmentManager manager, int initValue,ResultCallBack callBack)
    {
        this.callBack=callBack;
        this.initValue=initValue;
        show(manager,"Set Step Dialog");
    }
    void setUpNumberSlider()
    {
        binding.numberSlider.addOnChangeListener((slider, value, fromUser) -> {
            if(value<4000)
            {
                binding.currentStepTextView.setTextColor(Color.parseColor("#05668D"));
            }
            else if(value<10000)
            {
                binding.currentStepTextView.setTextColor(Color.parseColor("#028090"));
            }
            else if (value <16000)
            {
                binding.currentStepTextView.setTextColor(Color.parseColor("#00A896"));
            }
            else
            {
                binding.currentStepTextView.setTextColor(Color.parseColor("#02C39A"));
            }
            binding.currentStepTextView.setText(String.valueOf((int)value)+" Bước");
        });
    }

    public interface ResultCallBack{
        void onResult(int res);

    }


}
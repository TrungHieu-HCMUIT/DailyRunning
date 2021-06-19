package com.example.dailyrunning.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.dailyrunning.R;
import com.example.dailyrunning.databinding.FragmentCustomDialogBinding;
import com.example.dailyrunning.databinding.RunningDialogBinding;

import org.jetbrains.annotations.NotNull;

public class RunningLoadingDialog extends DialogFragment {
    private RunningDialogBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= RunningDialogBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
    }

    @Override
    public void show(@NonNull @NotNull FragmentManager manager, @Nullable @org.jetbrains.annotations.Nullable String tag) {
        Dialog dialogFrg=getDialog();
        if (dialogFrg == null || !dialogFrg.isShowing()) {
            super.show(manager, tag);
        }

    }

    @Override
    public void dismiss() {
        Dialog dialogFrg=getDialog();
        if (dialogFrg != null && dialogFrg.isShowing()) {
            super.dismiss();
        }
    }

}

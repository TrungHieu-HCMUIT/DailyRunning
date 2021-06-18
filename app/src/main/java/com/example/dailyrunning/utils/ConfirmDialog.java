package com.example.dailyrunning.utils;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dailyrunning.R;
import com.example.dailyrunning.databinding.FragmentConfirmDialogBinding;

import org.jetbrains.annotations.NotNull;


public class ConfirmDialog extends DialogFragment {

    FragmentConfirmDialogBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentConfirmDialogBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setLifecycleOwner(getActivity());
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        binding.titleTextView.setText(title);
        binding.descriptionTextView.setText(description);
        binding.cancelButton.setOnClickListener(v->{
            onCancel.onClick(v);
            dismiss();
        });
        binding.confirmButton.setOnClickListener(v->{
            onConfirm.onClick(v);
            dismiss();
        });
    }
    String title,description;
    View.OnClickListener onCancel,onConfirm;
    public void show(FragmentManager manager, String title, String description, View.OnClickListener onCancel, View.OnClickListener onConfirm)
    {
        this.title=title;
        this.description=description;
        this.onCancel=onCancel;
        this.onConfirm=onConfirm;
        Dialog dialogFrg=getDialog();
        if (dialogFrg == null || !dialogFrg.isShowing()) {
            show(manager,"ConfirmTAGG");
        }
    }
}
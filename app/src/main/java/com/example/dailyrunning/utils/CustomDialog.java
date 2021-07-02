package com.example.dailyrunning.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.example.dailyrunning.R;
import com.example.dailyrunning.databinding.FragmentCustomDialogBinding;

import org.jetbrains.annotations.NotNull;


public class CustomDialog extends DialogFragment {

    FragmentCustomDialogBinding binding;

    private boolean isSuccess;
    private int remainingPoint;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentCustomDialogBinding.inflate(inflater,container,false);
        binding.setIsSuccess(isSuccess);
        binding.setRemainingPoint(remainingPoint);
        if(isSuccess)
        {
            binding.animation.setAnimation(R.raw.done_animation);

            binding.animation.setMinAndMaxFrame(18,66);
        }
        else
        {
            binding.animation.setAnimation(R.raw.anim_failed);
            binding.animation.setMinFrame(0);
        }
        binding.animation.playAnimation();
        return binding.getRoot();
    }
    Handler handler=new Handler();
    Runnable runnable =new Runnable() {
        @Override
        public void run() {
            getDialog().dismiss();
        }
    };
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);

    }

    public void showDialog(FragmentManager manager,boolean isSuccess,int remainingPoint)
    {
        this.isSuccess=isSuccess;
        this.remainingPoint=remainingPoint;
        show(manager,"RunningPointExchangedDialog");
    }
    @Override
    public void show(@NonNull @NotNull FragmentManager manager, @Nullable @org.jetbrains.annotations.Nullable String tag) {
        Dialog dialogFrg=getDialog();
        if (dialogFrg == null || !dialogFrg.isShowing()) {
            super.show(manager, tag);
            handler.postDelayed(runnable,3000);
        }
    }

    @Override
    public void onDismiss(@NonNull @NotNull DialogInterface dialog) {
        super.onDismiss(dialog);
        handler.removeCallbacks(runnable);
    }
}
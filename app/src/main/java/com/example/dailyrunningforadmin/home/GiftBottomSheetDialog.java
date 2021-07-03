package com.example.dailyrunningforadmin.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.dailyrunningforadmin.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class GiftBottomSheetDialog extends BottomSheetDialog {
    private View mView;

    public PickImageListener pickImageListener;

    public GiftBottomSheetDialog(@NonNull Context context, int theme) {
        super(context, theme);
        pickImageListener = (PickImageListener) context;
    }

    public View getView() {
        return mView;
    }

    public void initView() {
        mView = LayoutInflater.from(getContext())
                .inflate(R.layout.bottom_sheet_layout, (ConstraintLayout)findViewById(R.id.bottom_sheet_container));
        setContentView(mView);

        setBehavior();

        addOnListener();
    }

    private void setBehavior() {
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) mView.getParent());
        mBehavior.setPeekHeight(1500);
    }

    private void addOnListener() {
        mView.findViewById(R.id.exit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });

        mView.findViewById(R.id.select_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageListener.pickImageFromGallery();
            }
        });
    }
}

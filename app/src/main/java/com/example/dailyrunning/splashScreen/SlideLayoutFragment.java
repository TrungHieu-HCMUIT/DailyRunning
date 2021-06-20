package com.example.dailyrunning.splashScreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.example.dailyrunning.R;
import com.example.dailyrunning.databinding.SlideLayoutBinding;
import com.example.dailyrunning.home.HomeActivity;

import org.jetbrains.annotations.NotNull;

import static com.example.dailyrunning.splashScreen.SplashActivity.ITEM_DELAY;

public class SlideLayoutFragment extends Fragment {

    int position;
    SlideLayoutFragment(int position)
    {
     this.position=position;
    }
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        return  inflater.inflate(R.layout.slide_layout, container, false);
    }

    private void setDimensions(View view, int width) {

        android.view.ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, getResources().getDisplayMetrics());
        view.setLayoutParams(params);
        view.requestLayout();
    }
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView header=view.findViewById(R.id.header_text_view);
        TextView content=view.findViewById(R.id.content_text_view);
        ImageView imageView=view.findViewById(R.id.onboarding_img);
        if(position!=3) {
            header.setText(OnboardingAdapter.header[position]);
            content.setText(OnboardingAdapter.description[position]);
        }
        if(position==3)
        {
            Intent intent =new Intent(getContext(), HomeActivity.class);
            header.setOnClickListener(v->{
                startActivity(intent);
                ((Activity)getContext()).finish();
            });
        }
        switch (position) {
            case 0:
                setDimensions(imageView,260);
                imageView.setImageResource(R.drawable.onboarding_1);
                break;
            case 1:
                setDimensions(imageView,300);

                imageView.setImageResource(R.drawable.onboarding_2);
                break;
            case 2:
                setDimensions(imageView,300);

                imageView.setImageResource(R.drawable.onboarding_3);

                break;
            case 3:
                imageView.setImageResource(R.drawable.onboarding_4);
                setDimensions(imageView,100);
                header.setText(OnboardingAdapter.header[position]);
                header.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
                header.setPadding(0,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()),0,0);


                break;
            default:
                break;

        }

    }
}

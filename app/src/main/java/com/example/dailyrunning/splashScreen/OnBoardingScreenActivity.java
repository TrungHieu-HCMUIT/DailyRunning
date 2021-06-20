package com.example.dailyrunning.splashScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.databinding.BindingAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.example.dailyrunning.R;
import com.example.dailyrunning.databinding.ActivityOnBoardingScreenBinding;

import static com.example.dailyrunning.splashScreen.SplashActivity.ITEM_DELAY;

public class OnBoardingScreenActivity extends AppCompatActivity {

    ActivityOnBoardingScreenBinding binding;
    TextView[] dots;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         binding =ActivityOnBoardingScreenBinding.inflate(getLayoutInflater());

        OnboardingAdapter adapter =new OnboardingAdapter(this);
        binding.boardingPager.setAdapter(adapter);
        setContentView(binding.getRoot());
        binding.boardingPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                addDot(position);

            }
        });
        (new Handler()).postDelayed(()->{
            addDot(0);

        },100);


    }
    void addDot(int pos)
    {
        binding.dotLayout.setVisibility(View.VISIBLE);


        dots=new TextView[3];
        binding.dotLayout.removeAllViews();
        if(pos==3)
            binding.dotLayout.setVisibility(View.INVISIBLE);
        for(int i=0;i<3;i++)
        {
            dots[i]=new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.transparentWhite));
            binding.dotLayout.addView(dots[i]);

        }

        if(dots.length>0 && pos!=3)
        {
            dots[pos].setTextColor(getResources().getColor(R.color.white));
        }

    }

}
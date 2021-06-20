package com.example.dailyrunning.splashScreen;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.dailyrunning.R;
import com.example.dailyrunning.databinding.SlideLayoutBinding;

import org.jetbrains.annotations.NotNull;

public class OnboardingAdapter extends FragmentStateAdapter {

    Context context;
    LayoutInflater layoutInflater;

    OnboardingAdapter(Context context) {
        super((FragmentActivity) context);
        this.context = context;
    }

    static public final String[] header = {
            "Kết nối cộng đồng",
            "Theo dõi quá trình",
            "Voucher phần thưởng",
            "Bắt đầu thôi !"
    };
    static public final String[] description = {
            "Chia sẻ quá trình của bạn và theo dõi\nquá trình của bạn bè. ",
            "Thống kê thông số và mô phỏng\nlại quá trình chính xác",
            "Nhiều voucher hấp dẫn theo\nmốc điểm hoàn toàn miễn phí ",
    };




    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        return new SlideLayoutFragment(position);
    }

    @Override
    public int getItemCount() {
        return header.length;
    }
}

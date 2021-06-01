package com.example.dailyrunning.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.dailyrunning.R;
import com.example.dailyrunning.databinding.FragmentStatisticalBinding;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StatisticalFragment extends Fragment {


    private final int STATISTICAL_WEEK=0;
    private final int STATISTICAL_MONTH=1;
    private final int STATISTICAL_YEAR=2;
    private FragmentStatisticalBinding binding;
    private StatisticalViewModel statisticalViewModel;
    private int currentPage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //region get View
        binding=FragmentStatisticalBinding.inflate(inflater,container,false);
        //endregion

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        statisticalViewModel=new ViewModelProvider(requireActivity()).get(StatisticalViewModel.class);
        binding.setStatisticalViewModel(statisticalViewModel);
        binding.setLifecycleOwner(requireActivity());
        currentPage=getArguments().getInt("position");
        binding.setCurrentPage(currentPage);
        setData(currentPage);

    }

    public static Fragment newInstance(int position) {
        StatisticalFragment mCurrentFragment = new StatisticalFragment();
        Bundle args = new Bundle();
        args.putInt("position",position);
        mCurrentFragment.setArguments(args);
        return mCurrentFragment;
    }
    private void setData(int position)
    {
        //TODO: get current user, get statistical info
        statisticalViewModel.fetchActivities();
       /* switch (position)
        {
            case STATISTICAL_WEEK:
                distance=12;
                timeWorking=Calendar.getInstance().getTime();
                workingCount=3;
                break;
            case STATISTICAL_MONTH:
                distance=20;
                timeWorking=Calendar.getInstance().getTime();
                workingCount=10;
                break;
            case STATISTICAL_YEAR:
                distance=50;
                timeWorking=Calendar.getInstance().getTime();
                workingCount=30;
                break;
            default:
                break;
        }
        distanceTextView.setText(String.valueOf(distance)+" Km");
        SimpleDateFormat dateFormat=new SimpleDateFormat("hh:mm:ss");
        timeWorkingTextView.setText(dateFormat.format(timeWorking));
        workingCountTextView.setText(String.valueOf(workingCount));*/
    }
}

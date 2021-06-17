package com.example.dailyrunning.user;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.dailyrunning.databinding.FragmentGiftBinding;
import com.example.dailyrunning.model.GiftInfo;
import com.example.dailyrunning.R;
import com.example.dailyrunning.utils.AllGiftAdapter;
import com.example.dailyrunning.home.HomeViewModel;
import com.example.dailyrunning.utils.CustomDialog;

import java.util.ArrayList;


public class GiftFragment extends Fragment {

    private RecyclerView mGiftRecyclerView;
    private AllGiftAdapter mAllGiftAdapter;
    private ImageButton backButton;
    View rootView;
    private HomeViewModel mHomeViewModel;
    private FragmentGiftBinding binding;
    private GiftViewModel mGiftViewModel;
    private CustomDialog mCustomDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentGiftBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView=view;
        mGiftViewModel=new ViewModelProvider(getActivity()).get(GiftViewModel.class);
        binding.setGiftViewModel(mGiftViewModel);
        binding.setLifecycleOwner(getActivity());
        initView();
        setUpGiftRecyclerView();
        mHomeViewModel.mHomeActivity.getValue().hideNavBar();
        mCustomDialog=new CustomDialog();
        backButton.setOnClickListener(v->{
            getActivity().onBackPressed();
        });
    }

    private void initView() {
        backButton=rootView.findViewById(R.id.back_button);
        mGiftRecyclerView=rootView.findViewById(R.id.gift_recyclerView);
        mHomeViewModel=new ViewModelProvider(getActivity()).get(HomeViewModel.class);
    }

    private void setUpGiftRecyclerView() {
        mGiftRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));

        ArrayList<GiftInfo> giftInfos=new ArrayList<GiftInfo>();
        giftInfos.add(new GiftInfo(Uri.parse("Temp_uri"),"Provider 1","Gift detail 1",(int)(Math.random()*100),"temp_id"));
        giftInfos.add(new GiftInfo(Uri.parse("Temp_uri"),"Provider 1","Gift detail 1",(int)(Math.random()*100),"temp_id"));
        giftInfos.add(new GiftInfo(Uri.parse("Temp_uri"),"Provider 1","Gift detail 1",(int)(Math.random()*100),"temp_id"));
        giftInfos.add(new GiftInfo(Uri.parse("Temp_uri"),"Provider 1","Gift detail 1",(int)(Math.random()*100),"temp_id"));
        giftInfos.add(new GiftInfo(Uri.parse("Temp_uri"),"Provider 1","Gift detail 1",(int)(Math.random()*100),"temp_id"));
        giftInfos.add(new GiftInfo(Uri.parse("Temp_uri"),"Provider 1","Gift detail 1",(int)(Math.random()*100),"temp_id"));
        giftInfos.add(new GiftInfo(Uri.parse("Temp_uri"),"Provider 1","Gift detail 1",(int)(Math.random()*100),"temp_id"));
        giftInfos.add(new GiftInfo(Uri.parse("Temp_uri"),"Provider 1","Gift detail 1",(int)(Math.random()*100),"temp_id"));
        giftInfos.add(new GiftInfo(Uri.parse("Temp_uri"),"Provider 1","Gift detail 1",(int)(Math.random()*100),"temp_id"));
        AllGiftAdapter allGiftAdapter=new AllGiftAdapter(giftInfos, gift -> {
            mGiftViewModel.exchangeClick(() -> {
                mCustomDialog.show(getChildFragmentManager(),"tag");
            });
        });
        mGiftRecyclerView.setAdapter(allGiftAdapter);

    }
}
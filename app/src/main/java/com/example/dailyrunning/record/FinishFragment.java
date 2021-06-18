package com.example.dailyrunning.record;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dailyrunning.databinding.FragmentFindBinding;
import com.example.dailyrunning.databinding.FragmentFinishBinding;
import com.example.dailyrunning.model.Activity;
import com.example.dailyrunning.model.Comment;
import com.example.dailyrunning.model.LatLng;
import com.example.dailyrunning.R;
import com.example.dailyrunning.model.Like;
import com.example.dailyrunning.model.Post;
import com.example.dailyrunning.model.UserInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class FinishFragment extends Fragment  {

    private RecordViewModel mRecordViewModel;

    FragmentFinishBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentFinishBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setLifecycleOwner(getActivity());
        mRecordViewModel=new ViewModelProvider(getActivity()).get(RecordViewModel.class);
        binding.setRecordViewModel(mRecordViewModel);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
              confirmCancelActivity();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);
        binding.cancelButton.setOnClickListener(v-> confirmCancelActivity());
    }

    void confirmCancelActivity()
    {
        mRecordViewModel.confirmDialog.show("Hủy bỏ hoạt động","Bạn có muốn hủy bỏ hoạt động hiện tại ?"
                ,v->{ },v->{
                    getActivity().finish();
                });
    }


}
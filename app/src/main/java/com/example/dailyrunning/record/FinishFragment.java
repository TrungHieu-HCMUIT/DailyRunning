package com.example.dailyrunning.record;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

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
import com.example.dailyrunning.model.Comment;
import com.example.dailyrunning.model.LatLng;
import com.example.dailyrunning.R;
import com.example.dailyrunning.model.Post;
import com.example.dailyrunning.model.UserInfo;
import com.example.dailyrunning.user.UserViewModel;
import com.google.android.datatransport.runtime.dagger.multibindings.ElementsIntoSet;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
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


public class FinishFragment extends Fragment implements UserViewModel.RunningSnackBar {

    private RecordViewModel mRecordViewModel;

    FragmentFinishBinding binding;
    Context mContext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentFinishBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext=getContext();
        binding.setLifecycleOwner((LifecycleOwner) mContext);
        mRecordViewModel=new ViewModelProvider((ViewModelStoreOwner) mContext).get(RecordViewModel.class);
        binding.setRecordViewModel(mRecordViewModel);
        requireActivity().getOnBackPressedDispatcher().addCallback((LifecycleOwner) mContext, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                confirmCancelActivity();
            }
        });
        binding.cancelButton.setOnClickListener(v-> confirmCancelActivity());
        binding.saveButton.setOnClickListener(v->{
            mRecordViewModel.onSaveClick(result -> {
                if(result) {
                    showSnackBar("Lưu hoạt động thành công",new Snackbar.Callback(){
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);
                            Intent point = new Intent();
                            point.putExtra("point",mRecordViewModel.runningPointAcquired.getValue());
                            ((Activity)mContext).setResult(Activity.RESULT_OK,point);
                            ((Activity)mContext).finish();
                        }
                    });

                }
                else
                {
                    showSnackBar("Lưu hoạt động thất bại",new Snackbar.Callback(){
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);
                            ((Activity)mContext).setResult(Activity.RESULT_CANCELED);
                            ((Activity)mContext).finish();
                        }
                    });
                }

            });
        });
    }

    void confirmCancelActivity()
    {
        mRecordViewModel.confirmDialog.show("Hủy bỏ hoạt động","Bạn có muốn hủy bỏ hoạt động hiện tại ?"
                ,v->{ },v->{
                    getActivity().finish();
                });
    }


    @Override
    public void showSnackBar(String content, Snackbar.Callback callback) {
        Snackbar.make(binding.rootScrollView, content, Snackbar.LENGTH_SHORT).setTextColor(ContextCompat.getColor(mContext, R.color.color_palette_3))
                .addCallback(callback).show();
    }
}
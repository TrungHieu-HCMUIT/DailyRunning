package com.example.dailyrunningforadmin.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.bumptech.glide.Glide;
import com.example.dailyrunningforadmin.R;
import com.example.dailyrunningforadmin.model.GiftInfo;
import com.example.dailyrunningforadmin.viewmodel.HomeViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class GiftBottomSheetDialog extends BottomSheetDialog {

    static GiftBottomSheetDialog instance;

    private View mView;
    static HomeViewModel homeViewModel;
    static GiftInfo mGift;
    static Context mContext;

    private ImageView giftImageView;
    private EditText providerEditText;
    private EditText describeEditText;
    private EditText runningPointEditText;
    private Button confirmButton;
    private Button deleteButton;

    static HomeActivityCallBack homeActivityCallBack;

    private GiftBottomSheetDialog(@NonNull Context context, int theme, GiftInfo giftInfo) {
        super(context, theme);
        mContext = context;
        homeActivityCallBack = (HomeActivityCallBack) context;
        mGift = giftInfo;

        homeViewModel = new ViewModelProvider((ViewModelStoreOwner) mContext).get(HomeViewModel.class);
    }

    public static GiftBottomSheetDialog getInstance(@NonNull Context context, int theme, GiftInfo giftInfo) {
        instance = new GiftBottomSheetDialog(context, theme, giftInfo);
        return instance;
    }

    public ImageView getGiftImageView() {
        return giftImageView;
    }

    public void initView() {
        mView = LayoutInflater.from(getContext())
                .inflate(R.layout.bottom_sheet_layout, (ConstraintLayout)findViewById(R.id.bottom_sheet_container));
        setContentView(mView);

        giftImageView = mView.findViewById(R.id.gift_imageView);
        providerEditText = mView.findViewById(R.id.provider_name_editText);
        describeEditText = mView.findViewById(R.id.describe_editText);
        runningPointEditText = mView.findViewById(R.id.running_point_editText);
        confirmButton = mView.findViewById(R.id.confirm_button);
        deleteButton = mView.findViewById(R.id.delete_button);

        initWidget();

        setBehavior();

        addOnListener();
        
    }

    private void initWidget() {

        if (mGift != null) {
            Glide.with(mContext).load(mGift.getPhotoUri()).into(giftImageView);
            providerEditText.setText(mGift.getProviderName());
            describeEditText.setText(mGift.getGiftDetail());
            runningPointEditText.setText(String.valueOf(mGift.getPoint()));
            deleteButton.setVisibility(View.VISIBLE);
        }
        else {
            giftImageView.setImageResource(R.drawable.image_placeholder);
            deleteButton.setVisibility(View.GONE);
        }
    }

    private void setBehavior() {
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) mView.getParent());
        if (deleteButton.getVisibility() == View.GONE) {
            mBehavior.setPeekHeight(1500);
        }
        else {
            mBehavior.setPeekHeight(2000);
        }
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
                homeActivityCallBack.pickImageFromGallery();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValidInput = checkInput(providerEditText.getText().toString(),
                        describeEditText.getText().toString(),
                        runningPointEditText.getText().toString(),
                        giftImageView.getTag().toString());
                if (isValidInput) {
                    GiftInfo gift;
                    Bitmap bitmap = ((BitmapDrawable) giftImageView.getDrawable()).getBitmap();
                    if (mGift == null) {
                        gift = new GiftInfo(
                                null,
                                null,
                                providerEditText.getText().toString(),
                                describeEditText.getText().toString(),
                                Integer.parseInt(runningPointEditText.getText().toString()));
                        homeViewModel.addGift(mContext, gift, bitmap);
                    }
                    else {
                        gift = mGift;
                        homeViewModel.updateGift(mContext, gift, bitmap);
                    }
                }
                hide();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeViewModel.deleteGift(mContext, mGift);
                hide();
                Toast.makeText(mContext, "Xóa thành công", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean checkInput(String providerName, String description, String runningPoint, String imageTag) {
        if (providerName.isEmpty() || description.isEmpty() || runningPoint.isEmpty()) {
            Toast.makeText(mContext, "Vui lòng nhập đủ thông tin", Toast.LENGTH_LONG).show();
            return false;
        }
        else if (Integer.parseInt(runningPoint) <= 0) {
            Toast.makeText(mContext, "Định dạng số cho điểm Running chưa đúng", Toast.LENGTH_LONG).show();
            return false;
        }
        else if (imageTag.equals("isUnset")) {
            Toast.makeText(mContext, "Vui lòng chọn ảnh", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}

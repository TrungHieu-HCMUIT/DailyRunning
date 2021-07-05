package com.example.dailyrunningforadmin.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dailyrunningforadmin.R;
import com.example.dailyrunningforadmin.model.GiftInfo;

import java.util.List;

public class GiftAdapter extends RecyclerView.Adapter<GiftAdapter.ViewHolder>{

    private List<GiftInfo> mGifts;
    private Context mContext;
    private HomeActivityCallBack homeActivityCallBack;

    public GiftAdapter(Context context,List<GiftInfo> mGifts)
    {
        this.mContext = context;
        this.mGifts=mGifts;
        homeActivityCallBack = (HomeActivityCallBack) mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mGiftImageView;
        public TextView mProviderNameTextView;
        public TextView mGiftDetailTextView;
        public TextView mPointTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            mGiftImageView = itemView.findViewById(R.id.gift_imageView);
            mProviderNameTextView=itemView.findViewById(R.id.provider_editText);
            mGiftDetailTextView=itemView.findViewById(R.id.gift_detail_textView);
            mPointTextView =itemView.findViewById(R.id.point_textView);
        }
    }

    @NonNull
    @Override
    public GiftAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View giftView = inflater.inflate(R.layout.gift_item_layout, parent, false);

        // Return a new holder instance
        return new ViewHolder(giftView);
    }

    @Override
    public void onBindViewHolder(@NonNull GiftAdapter.ViewHolder holder, int position) {
        GiftInfo currentGift = mGifts.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeActivityCallBack.initBottomSheet(currentGift);
            }
        });

        Glide.with(mContext).load(currentGift.getPhotoUri()).into(holder.mGiftImageView);
        holder.mGiftDetailTextView.setText(currentGift.getGiftDetail());
        holder.mProviderNameTextView.setText(currentGift.getProviderName());
        holder.mPointTextView.setText(String.valueOf(currentGift.getPoint()));
    }

    @Override
    public int getItemCount() {
        return mGifts.size();
    }
}

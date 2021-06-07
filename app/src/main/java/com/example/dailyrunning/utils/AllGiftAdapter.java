package com.example.dailyrunning.utils;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dailyrunning.model.GiftInfo;
import com.example.dailyrunning.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ramotion.foldingcell.FoldingCell;


import java.util.List;

public class AllGiftAdapter  extends RecyclerView.Adapter<AllGiftAdapter.ViewHolder>{
    public interface OnGiftClickListener{
        void onGiftClick(GiftInfo gift);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView mGiftImageView;
        public TextView mProviderNameTextView;
        public TextView mGiftDetailTextView;
        public TextView mPointTextView;
        public FoldingCell mFoldingCell;
        public Button mExchangeButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            mFoldingCell=itemView.findViewById(R.id.folding_cell);
            mFoldingCell.setOnClickListener(v -> mFoldingCell.toggle(false));
            mGiftImageView=itemView.findViewById(R.id.gift_imageView);
            mProviderNameTextView=itemView.findViewById(R.id.provider_textView);
            mGiftDetailTextView=itemView.findViewById(R.id.gift_detail_textView);
            mPointTextView=itemView.findViewById(R.id.point_textView);
            mExchangeButton=itemView.findViewById(R.id.exchange_button);



        }
    }
    private List<GiftInfo> mGifts;
    private final StorageReference mGiftImageReference;
    private OnGiftClickListener mOnGiftClickListener;
    public AllGiftAdapter(List<GiftInfo> mGifts,OnGiftClickListener mOnGiftClickListener)
    {
        this.mGifts=mGifts;
        mGiftImageReference=FirebaseStorage.getInstance().getReference().child("gift_images");
        this.mOnGiftClickListener=mOnGiftClickListener;
    }

    @NonNull
    @Override
    public AllGiftAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View giftView = inflater.inflate(R.layout.gift_item_all_gift, parent, false);

        // Return a new holder instance
        return new ViewHolder(giftView);
    }

    @Override
    public void onBindViewHolder(@NonNull AllGiftAdapter.ViewHolder holder, int position) {
        GiftInfo currentGift=mGifts.get(position);
        mGiftImageReference.child("place_holder_picture.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.mGiftImageView.getContext()).load(uri).into(holder.mGiftImageView);
            }
        });
        holder.mGiftDetailTextView.setText(currentGift.getGiftDetail());
        holder.mProviderNameTextView.setText(currentGift.getProviderName());
        holder.mPointTextView.setText(String.valueOf(currentGift.getPoint()));
        holder.mExchangeButton.setOnClickListener(v->{
            mOnGiftClickListener.onGiftClick(currentGift);
        });
    }

    @Override
    public int getItemCount() {
        return mGifts.size();
    }
}

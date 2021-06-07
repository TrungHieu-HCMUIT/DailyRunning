package com.example.dailyrunning.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyrunning.R;
import com.example.dailyrunning.model.MedalInfo;

import java.util.List;

public class MedalAdapter extends RecyclerView.Adapter<MedalAdapter.ViewHolder> {
    public interface OnMedalClickListener
    {
        void onMedalClickListener(MedalInfo medalInfo);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView mMedalImageView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            mMedalImageView=itemView.findViewById(R.id.medal_item_imageView);

        }
    }
    private List<MedalInfo> medals;
    private OnMedalClickListener mOnMedalClickListener;
    public MedalAdapter(List<MedalInfo> medals,OnMedalClickListener mOnMedalClickListener)
    {
        this.medals =medals;
        this.mOnMedalClickListener=mOnMedalClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View medalView = inflater.inflate(R.layout.medal_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(medalView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MedalAdapter.ViewHolder holder, int position) {
        MedalInfo currentMedal = medals.get(position);

        ImageView medalImageView=holder.mMedalImageView;
        medalImageView.setImageResource(currentMedal.getImageID());
        holder.itemView.setOnClickListener(v->{
            mOnMedalClickListener.onMedalClickListener(currentMedal);
        });
    }

    @Override
    public int getItemCount() {
        return medals.size();
    }
}

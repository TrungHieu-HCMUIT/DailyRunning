package com.example.dailyrunning.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyrunning.R;

import java.util.List;

public class MedalAdapter extends RecyclerView.Adapter<MedalAdapter.ViewHolder> {

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
    private List<Integer> mMedalID;
    public MedalAdapter(List<Integer> mMedalID)
    {
        this.mMedalID=mMedalID;
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
        Integer medalID=mMedalID.get(position);

        ImageView medalImageView=holder.mMedalImageView;
        medalImageView.setImageResource(medalID);
    }

    @Override
    public int getItemCount() {
        return mMedalID.size();
    }
}

package com.example.dailyrunning.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dailyrunning.model.PostDataTest;
import com.example.dailyrunning.R;

import java.util.ArrayList;

public class PostViewAdapter extends RecyclerView.Adapter<PostViewAdapter.ViewHolder>{
    // TODO: Use ArrayList<Object> instead
    private Context mContext;
    private ArrayList<PostDataTest> listItem;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // TODO: Edit widgets here
        public ImageView userAvatar;
        public TextView userName;
        public TextView dateTime;
        public TextView content;
        public TextView distance;
        public TextView duration;
        public TextView pace;
        public TextView like;
        public TextView comment;
        public ImageView image;

        public ViewHolder(@NonNull View view) {
            super(view);

            // TODO: Edit binding view here
            userAvatar = (ImageView) view.findViewById(R.id.ivUserAvatar);
            userName = (TextView) view.findViewById(R.id.tvUsername);
            dateTime = (TextView) view.findViewById(R.id.tvDateTime);
            content = (TextView) view.findViewById(R.id.tvContent);
            distance = (TextView) view.findViewById(R.id.tvDistance);
            duration = (TextView) view.findViewById(R.id.tvDuration);
            pace = (TextView) view.findViewById(R.id.tvPace);
            image = (ImageView) view.findViewById(R.id.ivMap);
            like = (TextView) view.findViewById(R.id.tvNumOfLike);
            comment = (TextView) view.findViewById(R.id.tvNumOfComment);
        }
    }

    public PostViewAdapter(@NonNull Context context, ArrayList<PostDataTest> data) {
        this.mContext = context;
        listItem = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // TODO: Insert views here
        Glide.with(mContext).load(listItem.get(position).getUserAvatarUri()).into(holder.userAvatar);
        holder.userName.setText(listItem.get(position).getUsername());
        holder.dateTime.setText(listItem.get(position).getDateTime());
        holder.content.setText(listItem.get(position).getContent());
        holder.distance.setText(listItem.get(position).getDistance());
        holder.duration.setText(listItem.get(position).getDuration());
        holder.pace.setText(listItem.get(position).getPace());
        //Glide.with(mContext).load(base64ToBitmap(listItem.get(position).getImage())).into(holder.image);
        Glide.with(mContext).load(listItem.get(position).getImage()).into(holder.image);
        holder.like.setText("" + listItem.get(position).getNumOfLike());
        holder.comment.setText("" + listItem.get(position).getNumOfComment());
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }
}

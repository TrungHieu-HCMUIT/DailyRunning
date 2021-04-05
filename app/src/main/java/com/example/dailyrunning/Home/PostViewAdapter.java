package com.example.dailyrunning.Home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dailyrunning.Model.PostDataTest;
import com.example.dailyrunning.R;

import java.util.ArrayList;

public class PostViewAdapter extends RecyclerView.Adapter<PostViewAdapter.ViewHolder> {
    // TODO: Use ArrayList<Object> instead
    private Context mContext;
    private ArrayList<PostDataTest> listItem;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // TODO: Edit widgets here
        private final ImageView userAvatar;
        private final TextView userName;
        private final TextView dateTime;
        private final TextView content;
        private final TextView distance;
        private final TextView duration;
        private final TextView pace;
        private final TextView like;
        private final TextView comment;

        public ViewHolder(@NonNull View view) {
            super(view);

            // TODO: Edit binding view here
            userAvatar = (ImageView) view.findViewById(R.id.ivUserAvatar);
            userName = (TextView) view.findViewById(R.id.tvUsername);
            dateTime = (TextView) view.findViewById(R.id.tvDateTime);
            content = (TextView) view.findViewById(R.id.tvContent);
            distance = (TextView) view.findViewById(R.id.tvDuration);
            duration = (TextView) view.findViewById(R.id.tvDuration);
            pace = (TextView) view.findViewById(R.id.tvPace);
            like = (TextView) view.findViewById(R.id.tvNumOfLike);
            comment = (TextView) view.findViewById(R.id.tvNumOfComment);
        }

        public ImageView getUserAvatar() {
            return userAvatar;
        }

        public TextView getUserName() {
            return userName;
        }

        public TextView getDateTime() {
            return dateTime;
        }

        public TextView getContent() {
            return content;
        }

        public TextView getDistance() {
            return distance;
        }

        public TextView getDuration() {
            return duration;
        }

        public TextView getPace() {
            return pace;
        }

        public TextView getLike() {
            return like;
        }

        public TextView getComment() {
            return comment;
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
        holder.like.setText("" + listItem.get(position).getNumOfLike());
        holder.comment.setText("" + listItem.get(position).getNumOfComment());
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }
}

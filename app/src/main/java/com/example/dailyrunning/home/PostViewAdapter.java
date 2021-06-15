package com.example.dailyrunning.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dailyrunning.model.Post;
import com.example.dailyrunning.R;

import java.util.ArrayList;

public class PostViewAdapter extends RecyclerView.Adapter<PostViewAdapter.ViewHolder>{
    private Context mContext;
    private ArrayList<Post> listItem;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView userAvatar;
        public TextView userName;
        public TextView dateTime;
        public TextView content;
        public TextView distance;
        public TextView duration;
        public TextView pace;
        public ImageView image;
        public TextView like;
        public TextView comment;

        public ViewHolder(@NonNull View view) {
            super(view);

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

    public PostViewAdapter(@NonNull Context context, ArrayList<Post> data) {
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
        Glide.with(mContext).load(listItem.get(position).getOwnerAvatarUrl()).into(holder.userAvatar);
        holder.userName.setText(listItem.get(position).getOwnerName());
        holder.dateTime.setText(listItem.get(position).getActivity().getDateCreated());
        holder.content.setText(listItem.get(position).getActivity().getDescribe());
        holder.distance.setText(listItem.get(position).getActivity().getDistance() + " km");
        holder.duration.setText(DateUtils.formatElapsedTime(listItem.get(position).getActivity().getDuration()));
        holder.pace.setText(listItem.get(position).getActivity().getPace()+ " m/s");
        Glide.with(mContext).load(listItem.get(position).getActivity().getPictureURI()).into(holder.image);
        if (listItem.get(position).getComments() == null) {
            listItem.get(position).setComments(new ArrayList<>());
        }
        holder.like.setText("" + listItem.get(position).getComments().size());
        if (listItem.get(position).getLikes() == null) {
            listItem.get(position).setLikes(new ArrayList<>());
        }
        holder.comment.setText("" + listItem.get(position).getLikes().size());
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }
    private Bitmap base64ToBitmap(String b64) {
        //String base64String = "data:image/png;base64,"+b64;
        //String base64Image = base64String.split(",")[1];
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

}

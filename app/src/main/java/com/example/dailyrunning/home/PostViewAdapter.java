package com.example.dailyrunning.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dailyrunning.model.Post;
import com.example.dailyrunning.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PostViewAdapter extends RecyclerView.Adapter<PostViewAdapter.ViewHolder>{
    private Context mContext;
    private String currentUserId;
    private ArrayList<Post> postsList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView userAvatar;
        public TextView userName;
        public TextView dateTime;
        public TextView content;
        public TextView distance;
        public TextView duration;
        public TextView pace;
        public ImageView image;
        public Button pressToLikeBtn;
        public Button pressToUnlikeBtn;
        public Button commentBtn;
        public TextView likeTv;
        public TextView commentTv;

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
            pressToLikeBtn = (Button) view.findViewById(R.id.btnPressToLike);
            pressToUnlikeBtn = (Button) view.findViewById(R.id.btnPressToUnlike);
            commentBtn = (Button) view.findViewById(R.id.btnComment);
            likeTv = (TextView) view.findViewById(R.id.tvNumOfLike);
            commentTv = (TextView) view.findViewById(R.id.tvNumOfComment);
        }
    }

    public PostViewAdapter(Context mContext, String currentUserId, ArrayList<Post> postsList) {
        this.mContext = mContext;
        this.currentUserId = currentUserId;
        this.postsList = postsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(mContext).load(postsList.get(position).getOwnerAvatarUrl()).into(holder.userAvatar);
        holder.userName.setText(postsList.get(position).getOwnerName());
        holder.dateTime.setText(postsList.get(position).getActivity().getDateCreated());
        holder.content.setText(postsList.get(position).getActivity().getDescribe());
        holder.distance.setText(postsList.get(position).getActivity().getDistance() + " km");
        holder.duration.setText(DateUtils.formatElapsedTime(postsList.get(position).getActivity().getDuration()));
        holder.pace.setText(postsList.get(position).getActivity().getPace()+ " m/s");
        Glide.with(mContext).load(postsList.get(position).getActivity().getPictureURI()).into(holder.image);

        if (postsList.get(position).getLikesUserId() == null) {
            postsList.get(position).setLikesUserId(new ArrayList<>());
        }
        holder.likeTv.setText("" + postsList.get(position).getLikesUserId().size());
        if (postsList.get(position).getCommentsUserId() == null) {
            postsList.get(position).setCommentsUserId(new ArrayList<>());
        }

        if (postsList.get(position).getLikesUserId().contains(currentUserId)) {
            holder.pressToLikeBtn.setVisibility(View.INVISIBLE);
            holder.pressToUnlikeBtn.setVisibility(View.VISIBLE);
        }
        else {
            holder.pressToLikeBtn.setVisibility(View.VISIBLE);
            holder.pressToUnlikeBtn.setVisibility(View.INVISIBLE);
        }

        holder.pressToLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postsList.get(position).getLikesUserId().add(currentUserId);
                DatabaseReference postRef = FirebaseDatabase.getInstance().getReference()
                        .child("Post")
                        .child(postsList.get(position).getOwnerID())
                        .child(postsList.get(position).getPostID());
                postRef.setValue(postsList.get(position));
                setViewToUnlike(holder.pressToLikeBtn, holder.pressToUnlikeBtn);
                postRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int numOfLikes = task.getResult().getValue(Post.class).getLikesUserId().size();
                        holder.likeTv.setText("" + numOfLikes);
                    }
                });
            }
        });
        holder.pressToUnlikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postsList.get(position).getLikesUserId().remove(currentUserId);
                DatabaseReference postRef = FirebaseDatabase.getInstance().getReference()
                        .child("Post")
                        .child(postsList.get(position).getOwnerID())
                        .child(postsList.get(position).getPostID());
                postRef.setValue(postsList.get(position));
                setViewToLike(holder.pressToLikeBtn, holder.pressToUnlikeBtn);
                postRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<String> likeList = (ArrayList<String>) task.getResult().getValue(Post.class).getLikesUserId();
                        if (likeList == null) {
                            likeList = new ArrayList<>();
                        }
                        int numOfLikes = likeList.size();
                        holder.likeTv.setText("" + numOfLikes);
                    }
                });
            }
        });

        holder.commentTv.setText("" + postsList.get(position).getCommentsUserId().size());
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }
    private Bitmap base64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    private void setViewToLike(View like, View unlike) {
        like.setVisibility(View.VISIBLE);
        unlike.setVisibility(View.INVISIBLE);
    }

    private void setViewToUnlike(View like, View unlike) {
        like.setVisibility(View.INVISIBLE);
        unlike.setVisibility(View.VISIBLE);
    }
}

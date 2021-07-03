package com.example.dailyrunning.home.post;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.dailyrunning.home.HomeActivity;
import com.example.dailyrunning.home.find.UserRowAdapter;
import com.example.dailyrunning.model.Post;
import com.example.dailyrunning.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.Period;

import java.util.ArrayList;

import io.square1.richtextlib.v2.parser.handlers.Markers;

public class PostViewAdapter extends RecyclerView.Adapter<PostViewAdapter.ViewHolder> {
    private HomeActivity homeActivity;
    private String currentUserId;
    private ArrayList<Post> postsList;
    private NavController navController;
    private boolean isOtherProfile;
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
        public ShimmerFrameLayout shimmerFrameLayout;

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
            shimmerFrameLayout = view.findViewById(R.id.act_image_shimmer);
        }
    }

    public PostViewAdapter(HomeActivity homeActivity, String currentUserId, ArrayList<Post> postsList, NavController navController,boolean isOtherProfile) {
        this.homeActivity = homeActivity;
        this.currentUserId = currentUserId;
        this.postsList = postsList;
        this.navController = navController;
        this.isOtherProfile=isOtherProfile;
    }
    public void  setPost(ArrayList<Post> data)
    {
        this.postsList=data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(holder.itemView.getContext()).load(postsList.get(position).getOwnerAvatarUrl()).into(holder.userAvatar);
        holder.userName.setText(postsList.get(position).getOwnerName());
        holder.dateTime.setText(postsList.get(position).getActivity().getDateCreated());
        holder.content.setText(postsList.get(position).getActivity().getDescribe());
        holder.distance.setText(String.format("%.2f", postsList.get(position).getActivity().getDistance() / 1000) + " Km");
        holder.duration.setText(getTimeWorkingString(postsList.get(position).getActivity().getDuration()));
        holder.pace.setText(postsList.get(position).getActivity().getPace() + " m/s");
        Glide.with(holder.itemView.getContext())
                .load(postsList.get(position).getActivity().getPictureURI())
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.shimmerFrameLayout.hideShimmer();
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        (new Handler()).postDelayed(()->{
                            holder.shimmerFrameLayout.hideShimmer();
                        },1000);
                        return false;
                    }
                })
                .into(holder.image);
        View.OnLongClickListener onLikeLongClick;
        if(!isOtherProfile) {
            View.OnClickListener onUserClickListener= v -> {
                if (postsList.get(position).getOwnerID().equals(currentUserId))
                    return;
                homeActivity.mOtherUserProfileViewModel.onUserSelected(postsList.get(position).getOwnerID());
                navController.navigate(R.id.action_homeFragment_to_otherUserProfile);
            };

            onLikeLongClick = v -> {
                homeActivity.mListUserViewModel.showUserList((ArrayList<String>) postsList.get(position).getLikesUserId(),"Người thích");
                navController.navigate(R.id.action_homeFragment_to_listUserFragment);
                return false;
            };
            holder.userName.setOnClickListener(onUserClickListener);

            holder.userAvatar.setOnClickListener(onUserClickListener);
            holder.image.setOnClickListener(v -> {
                homeActivity.mPostViewModel.selectPost(postsList.get(position));
                homeActivity.hideNavBar();
                homeActivity.onPostSelected(postsList.get(position), true);
            });
            holder.commentBtn.setOnClickListener(v -> {
                homeActivity.mPostViewModel.selectPost(postsList.get(position));
                homeActivity.hideNavBar();
                homeActivity.onPostSelected(postsList.get(position), false);
            });
        }
        else
        {
            onLikeLongClick = v -> {
                homeActivity.mListUserViewModel.showUserList((ArrayList<String>) postsList.get(position).getLikesUserId(),"Người thích");
                navController.navigate(R.id.action_activityListFragment_to_listUserFragment);
                return false;
            };
            holder.image.setOnClickListener(v -> {
                homeActivity.mPostViewModel.selectPost(postsList.get(position));
                homeActivity.hideNavBar();
                navController.navigate(R.id.action_activityListFragment_to_mapViewFragment);

            });
            holder.commentBtn.setOnClickListener(v -> {
                homeActivity.mPostViewModel.selectPost(postsList.get(position));
                homeActivity.hideNavBar();
                navController.navigate(R.id.action_activityListFragment_to_postDetailFragment);
            });
        }


        if (postsList.get(position).getLikesUserId() == null) {
            postsList.get(position).setLikesUserId(new ArrayList<>());
        }
        holder.likeTv.setText("" + postsList.get(position).getLikesUserId().size());
        if (postsList.get(position).getComments() == null) {
            postsList.get(position).setComments(new ArrayList<>());
        }

        if (postsList.get(position).getLikesUserId().contains(currentUserId)) {
            holder.pressToLikeBtn.setVisibility(View.INVISIBLE);
            holder.pressToUnlikeBtn.setVisibility(View.VISIBLE);
        } else {
            holder.pressToLikeBtn.setVisibility(View.VISIBLE);
            holder.pressToUnlikeBtn.setVisibility(View.INVISIBLE);
        }






        holder.pressToLikeBtn.setOnLongClickListener(onLikeLongClick);
        holder.pressToUnlikeBtn.setOnLongClickListener(onLikeLongClick);
        holder.likeTv.setOnLongClickListener(onLikeLongClick);
        holder.pressToLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postsList.get(position).getLikesUserId().add(currentUserId);
                DatabaseReference likesUserRef = FirebaseDatabase.getInstance().getReference()
                        .child("Post")
                        .child(postsList.get(position).getOwnerID())
                        .child(postsList.get(position).getPostID())
                        .child("likesUserId");
                likesUserRef.setValue(postsList.get(position).getLikesUserId());
                setViewToUnlike(holder.pressToLikeBtn, holder.pressToUnlikeBtn);
                likesUserRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int numOfLikes = (int) task.getResult().getChildrenCount();
                        holder.likeTv.setText("" + numOfLikes);
                    }
                });
            }
        });
        holder.pressToUnlikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postsList.get(position).getLikesUserId().removeIf(userID -> userID.equals(currentUserId));
                DatabaseReference likesUserRef = FirebaseDatabase.getInstance().getReference()
                        .child("Post")
                        .child(postsList.get(position).getOwnerID())
                        .child(postsList.get(position).getPostID())
                        .child("likesUserId");
                likesUserRef.setValue(postsList.get(position).getLikesUserId());
                setViewToLike(holder.pressToLikeBtn, holder.pressToUnlikeBtn);
                likesUserRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int numOfLikes = (int) task.getResult().getChildrenCount();
                        holder.likeTv.setText("" + numOfLikes);
                    }
                });
            }
        });

        holder.commentTv.setText("" + postsList.get(position).getComments().size());
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

    public interface PostUtils {
        void onPostSelected(Post post, boolean isMap);
    }

    String getTimeWorkingString(long timeWorkingInSec) {
        Period period = new Period(timeWorkingInSec * 1000L);
        String time = String.format("%02d:%02d:%02d", period.getHours(), period.getMinutes(), period.getSeconds());
        return time;
    }
}

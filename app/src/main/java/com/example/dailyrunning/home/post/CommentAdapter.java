package com.example.dailyrunning.home.post;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.dailyrunning.R;
import com.example.dailyrunning.databinding.CommentItemBinding;
import com.example.dailyrunning.home.HomeActivity;
import com.example.dailyrunning.home.find.UserRowAdapter;
import com.example.dailyrunning.model.Comment;
import com.example.dailyrunning.model.UserInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import io.square1.richtextlib.v2.content.RichTextDocumentElement;


public class CommentAdapter extends   RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    ArrayList<Comment> comments;
    Context mContext;
    UserRowAdapter.OnUserClick onUserClick;
    CommentAdapter(ArrayList<Comment> data, Context mContext, UserRowAdapter.OnUserClick onUserClick)
    {
        this.onUserClick=onUserClick;
        this.mContext= mContext;
        comments=data;
    }
    public void updateComment( ArrayList<Comment> newComment)
    {
       comments=newComment;
        notifyDataSetChanged();
    }
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        CommentItemBinding itemBinding =
                CommentItemBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        final DatabaseReference userRef=FirebaseDatabase.getInstance().getReference().child("UserInfo");
        public ViewHolder(CommentItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        private final CommentItemBinding binding;
        public void bind(Comment item) {
            userRef.child(item.getOwnerID()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    UserInfo userInfo=dataSnapshot.getValue(UserInfo.class);
                    RichTextDocumentElement element=new RichTextDocumentElement
                            .TextBuilder(userInfo.getDisplayName())
                            .bold()
                            .color(Color.BLACK)
                            .append("  "+item.getContent())
                            .color(Color.parseColor("#666666"))
                            .build();
                    binding.contentRichText.setText(element);
                    PostViewModel.getCommentDuration(binding.commentDate,item.getDateCreated());
                    binding.commentDateShimmer.hideShimmer();
                    binding.contentShimmer.hideShimmer();
                    Glide.with(itemView.getContext()).load(userInfo.getAvatarURI())
                            .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            binding.avatarShimmer.hideShimmer();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            binding.avatarShimmer.hideShimmer();
                            return false;
                        }
                    }).into(binding.avatarImageView);
                    binding.avatarImageView.setOnClickListener(v->{
                        onUserClick.onUserClick(userInfo);
                    });
                }
            });

        }
    }
}

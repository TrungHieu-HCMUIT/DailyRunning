package com.example.dailyrunning.home.find;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dailyrunning.R;
import com.example.dailyrunning.model.UserInfo;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UserRowAdapter extends RecyclerView.Adapter<UserRowAdapter.ViewHolder>{
    private ArrayList<UserInfo> mUserList;
    private OnUserClick onUserClick;

    public void updateUserList(ArrayList<UserInfo> data)
    {
        mUserList=data;
        notifyDataSetChanged();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView userAvatar;
        public TextView userName;
        private ItemClickListener mItemClickListener;

        public ViewHolder(@NonNull @NotNull View view) {
            super(view);

            view.setOnClickListener(this);

            userAvatar = (ImageView) view.findViewById(R.id.user_avatar);
            userName = (TextView) view.findViewById(R.id.user_name);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.mItemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            mItemClickListener.onClick(v, getAdapterPosition());
        }

    }

    public UserRowAdapter(OnUserClick onUserClick, ArrayList<UserInfo> mUserList) {
        this.onUserClick = onUserClick;

        this.mUserList = mUserList;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_user_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext()).load(mUserList.get(position).getAvatarURI()).into(holder.userAvatar);
        holder.userName.setText(mUserList.get(position).getDisplayName());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle resultOnClick = new Bundle();
                resultOnClick.putString("userID", mUserList.get(position).getUserID());
                onUserClick.onUserClick(mUserList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public interface OnUserClick{
        void onUserClick(UserInfo user);
    }
}

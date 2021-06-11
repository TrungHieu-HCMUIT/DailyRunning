package com.example.dailyrunning.home.find;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dailyrunning.R;
import com.example.dailyrunning.model.UserRow;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UserRowAdapter extends RecyclerView.Adapter<UserRowAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<UserRow> mUserList;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView userAvatar;
        public TextView userName;

        public ViewHolder(@NonNull @NotNull View view) {
            super(view);

            userAvatar = (ImageView) view.findViewById(R.id.user_avatar);
            userName = (TextView) view.findViewById(R.id.user_name);
        }
    }

    public UserRowAdapter(@NonNull Context mContext, ArrayList<UserRow> mUserList) {
        this.mContext = mContext;
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
        Glide.with(mContext).load(mUserList.get(position).getAvatarURI()).into(holder.userAvatar);
        holder.userName.setText(mUserList.get(position).getDisplayName());
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }
}

package com.example.dailyrunning.record.spotify;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dailyrunning.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import kaaes.spotify.webapi.android.models.PlaylistSimple;

public class DiscoverPlaylistAdapter extends RecyclerView.Adapter<DiscoverPlaylistAdapter.ViewHolder> {
    private SpotifyViewModel mSpotifyViewModel;
    private NavController mNavController;
    DiscoverPlaylistAdapter(List<PlaylistSimple> data, Context context) {
        playlists = data;
        mNavController= Navigation.findNavController((Activity) context,R.id.spotify_fragment_container);
        mSpotifyViewModel=new ViewModelProvider((ViewModelStoreOwner) context).get(SpotifyViewModel.class);
    }

    List<PlaylistSimple> playlists;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discover_playlist_item, parent, false);
        return new DiscoverPlaylistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlaylistSimple currentItem = playlists.get(position);
        if (currentItem.images.size() != 0)
            Glide.with(holder.mPlaylistThumbnail).load(currentItem.images.get(0).url).into(holder.mPlaylistThumbnail);
        holder.mPlaylistTitle.setText(currentItem.name);
        holder.mPlaylistDescription.setText(currentItem.tracks.total + " songs • " + currentItem.owner.display_name);
        holder.mPlayButton.setOnClickListener(v->{
            mSpotifyViewModel.spotifyAppRemote.getValue().getPlayerApi().play(currentItem.uri);
        });
        holder.view.setOnClickListener(v->{
            Bundle bundle=new Bundle();
            bundle.putParcelable("playlist",currentItem);
            mNavController.navigate(R.id.action_musicMainFragment_to_playlistViewFragment,bundle);
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView mPlaylistThumbnail;
        public TextView mPlaylistTitle;
        public TextView mPlaylistDescription;
        public FloatingActionButton mPlayButton;
        public View view;
        private void marqueeAnimationForTextView() {
            mPlaylistTitle.setSelected(true);
            mPlaylistDescription.setSelected(true);
        }

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            view=itemView;

            mPlaylistThumbnail = itemView.findViewById(R.id.discover_playlist_thumbnail_image_view);
            mPlaylistTitle = itemView.findViewById(R.id.discover_playlist_title_text_view);
            mPlaylistDescription = itemView.findViewById(R.id.discover_playlist_description_text_view);
            mPlayButton = itemView.findViewById(R.id.discover_playlist_play_button);
            marqueeAnimationForTextView();

        }
    }
}

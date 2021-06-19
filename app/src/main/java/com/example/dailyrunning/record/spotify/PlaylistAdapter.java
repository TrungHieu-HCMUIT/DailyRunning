package com.example.dailyrunning.record.spotify;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.PlaylistSimple;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    private List<PlaylistSimple> playlistInfos;
    private List<PlaylistSimple> playlistInfos_copy;
    private SpotifyViewModel mSpotifyViewModel;
    private NavController mNavController;
    private Context context;

    public PlaylistAdapter(List<PlaylistSimple> data, Context context) {
        playlistInfos = data;
        playlistInfos_copy = new ArrayList<>();
        playlistInfos_copy.addAll(data);
        this.context=context;
        mSpotifyViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(SpotifyViewModel.class);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.playlist_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(view);
    }

    public void filter(String text) {
        playlistInfos.clear();
        if (text.isEmpty()) {
            playlistInfos.addAll(playlistInfos_copy);
        } else {
            text = text.toLowerCase();
            for (PlaylistSimple item : playlistInfos_copy) {
                if (item.name.toLowerCase().contains(text)) {
                    playlistInfos.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlaylistSimple currentItem = playlistInfos.get(position);
        if (!currentItem.images.isEmpty())
            Glide.with(holder.mPlaylistThumbnail).load(currentItem.images.get(0).url).into(holder.mPlaylistThumbnail);
        holder.mPlaylistTitle.setText(currentItem.name);
        holder.mPlaylistDescription.setText(currentItem.tracks.total + " songs • " + currentItem.owner.display_name);
        holder.mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpotifyViewModel.spotifyAppRemote.getValue().getPlayerApi().play(currentItem.uri);
            }
        });
        holder.view.setOnClickListener(v->{
            Bundle bundle=new Bundle();
            bundle.putParcelable("playlist",currentItem);
           // mSpotifyViewModel.mMapsActivity.getValue().callOnPostResume();
            mNavController= Navigation.findNavController(mSpotifyViewModel.mMapsActivity.getValue(), R.id.spotify_fragment_container);

            mNavController.navigate(R.id.action_musicMainFragment_to_playlistViewFragment,bundle);
        });


    }

    @Override
    public int getItemCount() {
        return playlistInfos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView mPlaylistThumbnail;
        public TextView mPlaylistTitle;
        public TextView mPlaylistDescription;
        public ImageButton mPlayButton;
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
            mPlaylistThumbnail = itemView.findViewById(R.id.playlist_thumbnail_image_view);
            mPlaylistTitle = itemView.findViewById(R.id.playlist_title_text_view);
            mPlaylistDescription = itemView.findViewById(R.id.playlist_description_text_view);
            mPlayButton = itemView.findViewById(R.id.play_button);
            marqueeAnimationForTextView();

        }
    }


}

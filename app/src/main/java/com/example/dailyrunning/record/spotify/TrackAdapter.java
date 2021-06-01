package com.example.dailyrunning.record.spotify;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dailyrunning.R;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {
    SpotifyViewModel mSpotifyViewModel;
    String playlistUri;
    TrackAdapter(List<Track> tracks,String playlistUri, Activity viewModelOwner) {
        this.tracks = tracks;
        tracks_copy=new ArrayList<>();
        this.playlistUri=playlistUri;
        tracks_copy.addAll(tracks);
        mSpotifyViewModel=new ViewModelProvider((ViewModelStoreOwner) viewModelOwner).get(SpotifyViewModel.class);
    }

    private List<Track> tracks;
    private List<Track> tracks_copy;

    public void filter(String text) {
        tracks.clear();
        if(text.isEmpty()){
            tracks.addAll(tracks_copy);
        } else{
            text = text.toLowerCase();
            for(Track item: tracks_copy){
                if(item.name.toLowerCase().contains(text) || item.artists.get(0).name.toLowerCase().contains(text)){
                    tracks.add(item);
                }
            }
        }
        notifyDataSetChanged();
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

    static public String getDuration(long duration_ms)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        String formatted = sdf.format(new Date(duration_ms));
        return  formatted;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Track currentTrack=tracks.get(position);
        List<Image> imageList=currentTrack.album.images;
        if(imageList.size()!=0)
        Glide.with(holder.mTrackThumbnail).load(imageList.get(imageList.size()-1).url).into(holder.mTrackThumbnail);
        holder.mTrackTitle.setText(currentTrack.name);
        holder.playButton.setOnClickListener(v -> {

            SpotifyAppRemote spotifyAppRemote=mSpotifyViewModel.spotifyAppRemote.getValue();
            if(playlistUri==null)
            spotifyAppRemote.getPlayerApi().play(currentTrack.uri);
            else
                spotifyAppRemote.getPlayerApi().skipToIndex(playlistUri,position);
        });
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        String formatted = sdf.format(new Date(currentTrack.duration_ms));
        String description=currentTrack.artists.get(0).name+" • "+formatted;
        holder.mTrackDescription.setText(description);
    }


    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView mTrackThumbnail;
        public TextView mTrackTitle;
        public TextView mTrackDescription;
        public ImageButton playButton;
        private void marqueeAnimationForTextView()
        {
            mTrackTitle.setSelected(true);
            mTrackDescription.setSelected(true);
        }
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            mTrackThumbnail = itemView.findViewById(R.id.playlist_thumbnail_image_view);
            mTrackTitle = itemView.findViewById(R.id.playlist_title_text_view);
            mTrackDescription = itemView.findViewById(R.id.playlist_description_text_view);
            playButton=itemView.findViewById(R.id.play_button);
            marqueeAnimationForTextView();


        }
    }
}

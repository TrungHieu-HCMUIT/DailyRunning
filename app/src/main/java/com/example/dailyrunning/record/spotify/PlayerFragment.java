package com.example.dailyrunning.record.spotify;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.dailyrunning.R;
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.protocol.types.Repeat;

import org.jetbrains.annotations.NotNull;


public class PlayerFragment extends Fragment {

    private RestoreStateViewModel mRestoreStateViewModel;
    private SpotifyViewModel mSpotifyViewModel;
    private ImageView mThumbnailImageView;
    private TextView mTitleTextView;
    private TextView mArtistTextView;
    private SeekBar mSeekBar;
    private ImageButton mPauseButton;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;
    private ImageButton mShuffleButton;
    private ImageButton mRepeatButton;
    private ImageButton mLikeButton;
    private TextView mDuration;
    private TextView mCurrentTime;
    private View rootView;
    private long mPlaybackPosition = 0;
    private long mDurationTime;
    private boolean isPlaying = false;
    private Handler updateHandler;
    private Context mContext;
    private Runnable timerRunnable;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_player, container, false);
       
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext=getContext();
        mRestoreStateViewModel = new ViewModelProvider((ViewModelStoreOwner) mContext).get(RestoreStateViewModel.class);
        mSpotifyViewModel = new ViewModelProvider((ViewModelStoreOwner) mContext).get(SpotifyViewModel.class);

        findView();
        initState();
        playerFunction();
    }

    private void marqueeAnimationForTextView()
    {
        mTitleTextView.setSelected(true);
        mArtistTextView.setSelected(true);
    }
    private void findView() {
        mThumbnailImageView = rootView.findViewById(R.id.thumbnail_image_view);
        mTitleTextView = rootView.findViewById(R.id.track_title_text_view);
        mArtistTextView = rootView.findViewById(R.id.track_artist_text_view);
        mSeekBar = rootView.findViewById(R.id.seek_bar);
        mPauseButton = rootView.findViewById(R.id.play_pause_button_player);
        mNextButton = rootView.findViewById(R.id.next_button);
        mPreviousButton = rootView.findViewById(R.id.previous_button);
        mShuffleButton = rootView.findViewById(R.id.shuffle_button);
        mRepeatButton = rootView.findViewById(R.id.repeat_button);
        mLikeButton = rootView.findViewById(R.id.like_button);
        mDuration = rootView.findViewById(R.id.duration_text_view);
        mCurrentTime = rootView.findViewById(R.id.current_time_text_view);


    }
    private void initState()
    {
        marqueeAnimationForTextView();
        updateHandler = new Handler();
        timerRunnable = new Runnable() {

            public void run() {
                // Get mediaplayer time and set the value
                mPlaybackPosition+= 1000;
                mSeekBar.setProgress((int) mPlaybackPosition);
                try {
                    ((Activity)mContext).runOnUiThread(() -> mCurrentTime.setText("" + TrackAdapter.getDuration(mPlaybackPosition)));
                }
                catch (Exception f){}

                // This will trigger itself every one second.
                updateHandler.postDelayed(this, 1000);
            }

        };

        mSpotifyViewModel.spotifyAppRemote.getValue().getPlayerApi().getPlayerState().setResultCallback(playerState -> {
            mPlaybackPosition=playerState.playbackPosition;
            mSeekBar.setProgress((int) mPlaybackPosition);
            if (playerState.isPaused) {
                mPauseButton.setTag("play");
                mPauseButton.setImageResource(R.drawable.play);
                updateHandler.removeCallbacks(timerRunnable);

            } else {
                updateHandler.postDelayed(timerRunnable, 1000);
                mPlaybackPosition = playerState.playbackPosition;
                mPauseButton.setTag("pause");
                mPauseButton.setImageResource(R.drawable.pause);
            }
            mCurrentTime.setText("" + TrackAdapter.getDuration(mPlaybackPosition));

            //region shuffle
          /*  if(mShuffleButton.getTag().toString().equals("on"))
            {
                mSpotifyViewModel.spotifyAppRemote.getValue().getPlayerApi().setShuffle(true);
                ImageViewCompat.setImageTintList(mShuffleButton, ColorStateList.valueOf(Color.parseColor("#1DB954")));
                mShuffleButton.setTag("on");
            }
            else if(mShuffleButton.getTag().toString().equals("off"))
            {
                mSpotifyViewModel.spotifyAppRemote.getValue().getPlayerApi().setShuffle(false);
                ImageViewCompat.setImageTintList(mShuffleButton, ColorStateList.valueOf(Color.parseColor("#333333")));
                mShuffleButton.setTag("off");
            }*/
            if(playerState.playbackOptions.isShuffling)
            {
                ImageViewCompat.setImageTintList(mShuffleButton, ColorStateList.valueOf(Color.parseColor("#1DB954")));
                mShuffleButton.setTag("on");
            }
            else if(!playerState.playbackOptions.isShuffling)
            {
                ImageViewCompat.setImageTintList(mShuffleButton, ColorStateList.valueOf(Color.parseColor("#333333")));
                mShuffleButton.setTag("off");
            }
            //endregion

            //region repeat
         /*   if(mRepeatButton.getTag().toString().equals("off"))
            {
                mRepeatButton.setTag("all");
                mSpotifyViewModel.spotifyAppRemote.getValue().getPlayerApi().setRepeat(Repeat.ALL);
                mRepeatButton.setImageResource(R.drawable.ic_repeat);
                ImageViewCompat.setImageTintList(mRepeatButton, ColorStateList.valueOf(Color.parseColor("#1DB954")));

            }
            else if(mRepeatButton.getTag().toString().equals("all"))
            {
                mRepeatButton.setTag("one");
                mSpotifyViewModel.spotifyAppRemote.getValue().getPlayerApi().setRepeat(Repeat.ONE);
                mRepeatButton.setImageResource(R.drawable.ic_icons8_repeat_one);
                ImageViewCompat.setImageTintList(mRepeatButton, ColorStateList.valueOf(Color.parseColor("#1DB954")));

            }
            else if(mRepeatButton.getTag().toString().equals("one"))
            {
                mRepeatButton.setTag("off");
                mSpotifyViewModel.spotifyAppRemote.getValue().getPlayerApi().setRepeat(Repeat.OFF);
                mRepeatButton.setImageResource(R.drawable.ic_repeat);
                ImageViewCompat.setImageTintList(mRepeatButton, ColorStateList.valueOf(Color.parseColor("#333333")));
            }*/
            if(playerState.playbackOptions.repeatMode==Repeat.ALL)
            {
                mRepeatButton.setTag("all");
                mRepeatButton.setImageResource(R.drawable.ic_repeat);
                ImageViewCompat.setImageTintList(mRepeatButton, ColorStateList.valueOf(Color.parseColor("#1DB954")));

            }
            else if(playerState.playbackOptions.repeatMode==Repeat.ONE)
            {
                mRepeatButton.setTag("one");
                mRepeatButton.setImageResource(R.drawable.ic_icons8_repeat_one);
                ImageViewCompat.setImageTintList(mRepeatButton, ColorStateList.valueOf(Color.parseColor("#1DB954")));

            }
            else if(playerState.playbackOptions.repeatMode==Repeat.OFF)
            {
                mRepeatButton.setTag("off");
                mSpotifyViewModel.spotifyAppRemote.getValue().getPlayerApi().setRepeat(Repeat.OFF);
                mRepeatButton.setImageResource(R.drawable.ic_repeat);
                ImageViewCompat.setImageTintList(mRepeatButton, ColorStateList.valueOf(Color.parseColor("#333333")));
            }
            //endregion
        });
    }

    private void playerFunction() {


        mSpotifyViewModel.mCurrentTrack.observe((LifecycleOwner) mContext, currentTrack -> {
            //region check like status of current track
            mSpotifyViewModel.spotifyAppRemote.getValue().getUserApi().getLibraryState(currentTrack.uri).setResultCallback(libraryState -> {
                if (libraryState.isAdded)
                {
                    mLikeButton.setImageResource(R.drawable.liked);
                    mLikeButton.setTag("liked");
                }
                else
                {
                    mLikeButton.setImageResource(R.drawable.like);
                    mLikeButton.setTag("like");

                }
            });
            //endregion
            mTitleTextView.setText(currentTrack.name);
            mArtistTextView.setText(currentTrack.artist.name);
            mDurationTime = currentTrack.duration;
            mDuration.setText("" + TrackAdapter.getDuration(currentTrack.duration));
            mSeekBar.setMax((int) currentTrack.duration);
            String imageURI = currentTrack.imageUri.raw.replace("spotify:image:", "");
            imageURI = "https://i.scdn.co/image/" + imageURI;
            Glide.with(mThumbnailImageView).asDrawable().load(imageURI).into(new CustomTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    mRestoreStateViewModel.mThumbnailImage.setValue(resource);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }
            });
        });
        PlayerApi mPlayerApi = mSpotifyViewModel.spotifyAppRemote.getValue().getPlayerApi();
        mNextButton.setOnClickListener(v -> {
            mPlayerApi.skipNext();
        });
        mPreviousButton.setOnClickListener(v -> {
            mPlayerApi.skipPrevious();
        });

        mPauseButton.setOnClickListener(v ->
        {
            if (mPauseButton.getTag().toString().equals("pause")) {
                updateHandler.removeCallbacks(timerRunnable);
                mPauseButton.setTag("play");
                mPauseButton.setImageResource(R.drawable.play);
                mPlayerApi.pause();
            } else {
                mPauseButton.setTag("pause");
                mPauseButton.setImageResource(R.drawable.pause);
                updateHandler.postDelayed(timerRunnable, 1000);
                mPlayerApi.resume();
            }
        });

        mSpotifyViewModel.mPlayerState.observe((LifecycleOwner) mContext, state -> {
            mPlaybackPosition=state.playbackPosition;
            mSeekBar.setProgress((int) mPlaybackPosition);

            if (state.isPaused) {
                mPauseButton.setTag("play");
                mPauseButton.setImageResource(R.drawable.play);
            } else {
                mPlaybackPosition = state.playbackPosition;
                mPauseButton.setTag("pause");
                mPauseButton.setImageResource(R.drawable.pause);
            }
            mCurrentTime.setText("" + TrackAdapter.getDuration(mPlaybackPosition));
        });
        mRestoreStateViewModel.mThumbnailImage.observe((LifecycleOwner) mContext, image -> {
            if (image != null)
                mThumbnailImageView.setImageDrawable(image);
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSpotifyViewModel.spotifyAppRemote.getValue().getPlayerApi().seekTo(seekBar.getProgress());
                Log.v("SEEKBAR", "Seek success " + seekBar.getProgress());
            }
        });
        //region working with shuffle button
        mShuffleButton.setOnClickListener(v->{
            if(mShuffleButton.getTag().toString().equals("off"))
            {
                mSpotifyViewModel.spotifyAppRemote.getValue().getPlayerApi().setShuffle(true);
                ImageViewCompat.setImageTintList(mShuffleButton, ColorStateList.valueOf(Color.parseColor("#1DB954")));
                mShuffleButton.setTag("on");
            }
            else if(mShuffleButton.getTag().toString().equals("on"))
            {
                mSpotifyViewModel.spotifyAppRemote.getValue().getPlayerApi().setShuffle(false);
                ImageViewCompat.setImageTintList(mShuffleButton, ColorStateList.valueOf(Color.parseColor("#333333")));
                mShuffleButton.setTag("off");
            }
        });
        //endregion
        //region working with repeat button
        mRepeatButton.setOnClickListener(v->{
            if(mRepeatButton.getTag().toString().equals("off"))
            {
                mRepeatButton.setTag("all");
                mSpotifyViewModel.spotifyAppRemote.getValue().getPlayerApi().setRepeat(Repeat.ALL);
                mRepeatButton.setImageResource(R.drawable.ic_repeat);
                ImageViewCompat.setImageTintList(mRepeatButton, ColorStateList.valueOf(Color.parseColor("#1DB954")));

            }
            else if(mRepeatButton.getTag().toString().equals("all"))
            {
                mRepeatButton.setTag("one");
                mSpotifyViewModel.spotifyAppRemote.getValue().getPlayerApi().setRepeat(Repeat.ONE);
                mRepeatButton.setImageResource(R.drawable.ic_icons8_repeat_one);
                ImageViewCompat.setImageTintList(mRepeatButton, ColorStateList.valueOf(Color.parseColor("#1DB954")));

            }
            else if(mRepeatButton.getTag().toString().equals("one"))
            {
                mRepeatButton.setTag("off");
                mSpotifyViewModel.spotifyAppRemote.getValue().getPlayerApi().setRepeat(Repeat.OFF);
                mRepeatButton.setImageResource(R.drawable.ic_repeat);
                ImageViewCompat.setImageTintList(mRepeatButton, ColorStateList.valueOf(Color.parseColor("#333333")));
            }

        });
        //endregion

        //region like button

        mLikeButton.setOnClickListener(v->{
            String currentTrackID=mSpotifyViewModel.mCurrentTrack.getValue().uri;
            if(mLikeButton.getTag().toString().equals("like"))
            {
                mSpotifyViewModel.spotifyAppRemote.getValue().getUserApi().addToLibrary(currentTrackID);
                mLikeButton.setImageResource(R.drawable.liked);
                mLikeButton.setTag("liked");
            }
            else if(mLikeButton.getTag().toString().equals("liked"))
            {
                mSpotifyViewModel.spotifyAppRemote.getValue().getUserApi().removeFromLibrary(currentTrackID);
                mLikeButton.setImageResource(R.drawable.like);
                mLikeButton.setTag("like");
            }
        });
        //endregion

 /*   mSpotifyViewModel.spotifyService.getValue().getNewReleases(new Callback<NewReleases>() {
        @Override
        public void success(NewReleases newReleases, Response response) {
            newReleases.albums.items.
        }

        @Override
        public void failure(RetrofitError error) {

        }
    })*/
    }


}

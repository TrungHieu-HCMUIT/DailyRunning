package com.example.dailyrunning.record.spotify;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.dailyrunning.R;
import com.example.dailyrunning.record.RecordFragment;
import com.spotify.android.appremote.api.PlayerApi;

import org.jetbrains.annotations.NotNull;


public class BottomPlayerFragment extends Fragment {


    private View rootView;
    private SpotifyViewModel mSpotifyViewModel;
    private ImageView mThumbnailImageView;
    private TextView mTitleTextView;
    private TextView mArtistTextView;
    private ImageButton mPauseButton;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;
    private NavController mNavController;
    private RestoreStateViewModel mRestoreStateViewModel;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bottom_player, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext=getContext();
        mSpotifyViewModel = new ViewModelProvider((ViewModelStoreOwner) mContext).get(SpotifyViewModel.class);
        mRestoreStateViewModel = new ViewModelProvider((ViewModelStoreOwner) mContext).get(RestoreStateViewModel.class);
        Fragment parentFragment = this.getParentFragment();
        rootView.setOnClickListener(v -> {
            if (parentFragment instanceof RecordFragment) {
                mNavController = Navigation.findNavController((Activity) mContext, R.id.record_fragment_container);
                mNavController.navigate(R.id.action_recordFragment_to_playerFragment2);
            }
            else if (parentFragment instanceof PlaylistViewFragment) {
                mNavController = Navigation.findNavController((Activity) mContext, R.id.spotify_fragment_container);

                mNavController.navigate(R.id.action_playlistViewFragment_to_playerFragment);
            }
            else if (parentFragment instanceof MusicMainFragment) {
                mNavController = Navigation.findNavController((Activity) mContext, R.id.spotify_fragment_container);
                mNavController.navigate(R.id.action_musicMainFragment_to_playerFragment);
            }
        });
        findView();
        marqueeAnimationForTextView();
        initUI();
    }

    private void marqueeAnimationForTextView() {
        mTitleTextView.setSelected(true);
        mArtistTextView.setSelected(true);
    }

    private void findView() {
        mTitleTextView = rootView.findViewById(R.id.title_text_view);
        mArtistTextView = rootView.findViewById(R.id.artist_text_view);
        mPauseButton = rootView.findViewById(R.id.play_pause_button);
        mNextButton = rootView.findViewById(R.id.next_button);
        mPreviousButton = rootView.findViewById(R.id.previous_button);
        mThumbnailImageView = rootView.findViewById(R.id.thumbnail_image_view);
    }

    private void initUI() {
        mSpotifyViewModel.spotifyAppRemote.observe((LifecycleOwner) mContext, remote -> {
            PlayerApi mPlayerApi = remote.getPlayerApi();
            mNextButton.setOnClickListener(v -> {
                mPlayerApi.skipNext();
            });
            mPreviousButton.setOnClickListener(v -> {
                mPlayerApi.skipPrevious();
            });

            mPauseButton.setOnClickListener(v ->
            {
                if (mPauseButton.getTag().toString().equals("pause")) {
                    mPauseButton.setTag("play");
                    mPauseButton.setImageResource(R.drawable.play);
                    mPlayerApi.pause();

                } else {
                    mPauseButton.setTag("pause");
                    mPauseButton.setImageResource(R.drawable.pause);

                    mPlayerApi.resume();
                }
            });
            mSpotifyViewModel.mPlayerState.observe((LifecycleOwner) mContext, playerState -> {
                if (playerState==null) {
                    Log.e("SpotifyIn","playstate is null");
                    return;
                }
                if (playerState.isPaused) {
                    mPauseButton.setTag("play");
                    mPauseButton.setImageResource(R.drawable.play);
                } else {
                    mPauseButton.setTag("pause");
                    mPauseButton.setImageResource(R.drawable.pause);
                }

            });

            mSpotifyViewModel.mCurrentTrack.observe((LifecycleOwner) mContext, currentTrack -> {
                if (currentTrack != null) {
                    rootView.setVisibility(View.VISIBLE);

                    String imageURI = currentTrack.imageUri.raw.replace("spotify:image:", "");
                    imageURI = "https://i.scdn.co/image/" + imageURI;
                    Glide.with(mThumbnailImageView).asDrawable().load(imageURI).into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            mThumbnailImageView.setImageDrawable(resource);
                            mRestoreStateViewModel.mThumbnailImage.setValue(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
                    mTitleTextView.setText(currentTrack.name);
                    mArtistTextView.setText(currentTrack.artist.name);

                } else {
                    rootView.setVisibility(View.GONE);
                }
            });

        });
    }


}
package com.example.dailyrunning.Record.Spotify;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.dailyrunning.R;
import com.spotify.android.appremote.api.PlayerApi;


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bottom_player, container, false);
        mSpotifyViewModel = new ViewModelProvider(getActivity()).get(SpotifyViewModel.class);
        mRestoreStateViewModel = new ViewModelProvider(getActivity()).get(RestoreStateViewModel.class);
        rootView.setOnClickListener(v -> {
            mNavController = Navigation.findNavController(getActivity(), R.id.fragment);
            mNavController.navigate(R.id.action_musicMainFragment_to_playerFragment);
        });
        findView();
        marqueeAnimationForTextView();
        initUI();
        return rootView;
    }

    private void marqueeAnimationForTextView()
    {
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
        mSpotifyViewModel.spotifyAppRemote.observe(getActivity(), remote -> {
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
            mSpotifyViewModel.mPlayerState.observe(getActivity(), playerState -> {
                if (playerState.isPaused) {
                    mPauseButton.setTag("play");
                    mPauseButton.setImageResource(R.drawable.play);
                } else {
                    mPauseButton.setTag("pause");
                    mPauseButton.setImageResource(R.drawable.pause);
                }

            });

            mSpotifyViewModel.mCurrentTrack.observe(getActivity(), currentTrack -> {
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
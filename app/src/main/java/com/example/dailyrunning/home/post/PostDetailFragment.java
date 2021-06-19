package com.example.dailyrunning.home.post;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.dailyrunning.R;
import com.example.dailyrunning.databinding.FragmentPostDetailBinding;
import com.example.dailyrunning.model.Comment;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import io.square1.richtextlib.v2.content.RichTextDocumentElement;

import static com.example.dailyrunning.record.RecordViewModel.activityDateFormat;

public class PostDetailFragment extends Fragment {

    FragmentPostDetailBinding binding;
    PostViewModel mPostViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentPostDetailBinding.inflate(inflater,container,false);
        binding.setLifecycleOwner(getActivity());
        mPostViewModel=new ViewModelProvider(getActivity()).get(PostViewModel.class);
        binding.setPostViewModel(mPostViewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        initRecyclerView();
        loadContent();
        setUpPostCommentClick();
        loadAvatar();

    }

    private void loadAvatar() {
        Glide.with(this)
                .load(mPostViewModel.getSelectedPost().getValue().getOwnerAvatarUrl())
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
                })
                .into(binding.postDetailOwnerAvatar);

        Glide.with(this)
                .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        binding.currentUserAvatarShimmer.hideShimmer();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        binding.currentUserAvatarShimmer.hideShimmer();

                        return false;
                    }
                })
                .into(binding.currentUserAvatar);
    }

    private void initRecyclerView()
    {
        CommentAdapter adapter=new CommentAdapter((ArrayList<Comment>) mPostViewModel.getSelectedPost().getValue().getComments());
        binding.rvComment.setAdapter(adapter);
        binding.backButton.setOnClickListener(v->{getActivity().onBackPressed();});

        mPostViewModel.getSelectedPost().observe(getActivity(),updatedPost->{
            if(updatedPost!=null)
            adapter.updateComment((ArrayList<Comment>) updatedPost.getComments());
        });

    }
    private void setUpPostCommentClick()
    {
        binding.publishTv.setOnClickListener(v->{
            if(!binding.commentEdt.getText().toString().trim().isEmpty())
                mPostViewModel.postComment(new Comment(
                        UUID.randomUUID().toString(),
                        binding.commentEdt.getText().toString().trim(),
                        activityDateFormat.format(Calendar.getInstance().getTime()),
                        FirebaseAuth.getInstance().getCurrentUser().getUid()
                ));
            binding.commentEdt.setText("");
        });
    }
    private void loadContent()
    {
        RichTextDocumentElement element=new RichTextDocumentElement
                .TextBuilder(mPostViewModel.getSelectedPost().getValue().getOwnerName())
                .bold()
                .font("font/svn_avo_bold.ttf")
                .color(Color.BLACK)
                .append("  "+mPostViewModel.getSelectedPost().getValue().getActivity().getDescribe())
                .color(getResources().getColor(R.color.describe_color))
                .build();
        binding.postDetailDescribe.setText(element);
    }
}
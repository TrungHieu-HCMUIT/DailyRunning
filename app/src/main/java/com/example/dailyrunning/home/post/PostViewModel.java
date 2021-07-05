package com.example.dailyrunning.home.post;


import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dailyrunning.model.Activity;
import com.example.dailyrunning.model.Comment;
import com.example.dailyrunning.model.Post;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.example.dailyrunning.record.RecordViewModel.activityDateFormat;

public class PostViewModel extends ViewModel {
    private MutableLiveData<Post> selectedPost=new MutableLiveData<>();
    public MutableLiveData<ArrayList<Post>> myPosts=new MutableLiveData<>();
    public MutableLiveData<ArrayList<Post>> followingPosts=new MutableLiveData<>();
    DatabaseReference postRef;
    ChildEventListener mMyPostEventListener;
    ValueEventListener postChangeListener;
    ChildEventListener mfollowingPostEventListener;
    DatabaseReference followingRef;
    DatabaseReference followingPostRef;
    DatabaseReference myPostRef;
    public PostViewModel()
    {
        myPosts.setValue(new ArrayList<>());
        followingPosts.setValue(new ArrayList<>());
        postChangeListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null)
                {
                    Post temp=snapshot.getValue(Post.class);
                    if(temp.getComments()==null)
                        temp.setComments(new ArrayList<>());
                        selectedPost.setValue(temp);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };
        mMyPostEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                ArrayList<Post> temp=myPosts.getValue();
                Post newPost=snapshot.getValue(Post.class);
                if(newPost!=null) {
                    if (temp.stream().anyMatch((post -> newPost.getPostID().equals(post.getPostID())))) {
                        return;
                    }
                    temp.add(newPost);
                    temp.sort(Post::compareTo);
                    myPosts.postValue(temp);
                }
            }


            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                ArrayList<Post> temp=myPosts.getValue();
                Post changedPost=snapshot.getValue(Post.class);
                if(temp!=null && changedPost!=null)
                {
                    Post postInMyPost=temp.stream()
                            .filter(post -> post.getPostID().equals(changedPost.getPostID()))
                            .findFirst().orElse(new Post());
                    if(postInMyPost.getPostID()!=null)
                    {
                        temp.set(temp.indexOf(postInMyPost),changedPost);
                    }
                }
                temp.sort(Post::compareTo);
                myPosts.postValue(temp);
            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };
        mfollowingPostEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                ((Runnable) () -> addFollowingPost(snapshot.getValue(String.class))).run();
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {
                removeFollowing(snapshot.getValue(String.class));
            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        };
    }
    //region selected post
    public LiveData<Post> getSelectedPost()
    {return selectedPost;}
    public void selectPost(Post newSelectedPost)
    {
        if(postRef!=null && postChangeListener!=null) {
            postRef.removeEventListener(postChangeListener);
        }
        selectedPost.setValue(newSelectedPost);
        postRef=FirebaseDatabase.getInstance().getReference().child("Post").child(newSelectedPost.getOwnerID()).child(newSelectedPost.getPostID());
        postRef.addValueEventListener(postChangeListener);
    }

    public void postComment(Comment comment)
    {
        ArrayList<Comment> updatedComment= (ArrayList<Comment>) selectedPost.getValue().getComments();
        updatedComment.add(comment);
        postRef.child("comments").setValue(updatedComment);
    }


    @BindingAdapter({"postDate"})
    public static void getCommentDuration(TextView view, String date) {
        Log.i("postDate","DDDDD" );

        if (date == null)
            return;
        Date dateFormatted=null;
        try {
            dateFormatted = activityDateFormat.parse(date);
        }
        catch (Exception e)
        {
            Log.e("post date format error",e.getMessage());
        }
        DateTime dateTime=new DateTime(dateFormatted);
        long milis=DateTime.now().getMillis()-dateTime.getMillis();
        double minus=milis*1.0/60000;
        double hour=milis*1.0/3600000;
        double day=milis*1.0 / (1000*60*60*24);
        String newText;
        if(minus<=60)
        {
            newText=String.valueOf((int)minus)+" phút";
        }
        else if(hour<24)
        {
            newText=(String.valueOf((int)hour)+" giờ");
        }
        else if(day<30)
        {
            newText=(String.valueOf((int)day)+" ngày");
        }
        else if(day<365)
        {
            newText=(String.valueOf((int)day/30)+" tháng");
        }
        else
        {
            newText=(String.valueOf((int)day/365)+" năm");
        }
        Log.i("newtext",newText );


        if (view.getText().toString().equals(newText))
            return;
        view.setText(newText);
    }

    //endregion

    //region my posts
    public void getMyPosts()
    {

        myPosts.setValue(new ArrayList<>());
        if(myPostRef!=null)
            myPostRef.removeEventListener(mMyPostEventListener);
        myPostRef=FirebaseDatabase.getInstance().getReference().child("Post")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        myPostRef.removeEventListener(mMyPostEventListener);
        myPostRef.addChildEventListener(mMyPostEventListener);
    }

    private void removeFollowing(String userID)
    {

        ArrayList<Post> temp=followingPosts.getValue();
        temp= (ArrayList<Post>) temp.stream().filter(post -> !post.getOwnerID().equals(userID)).collect(Collectors.toList());
        followingPosts.setValue(temp);
    }
    private void addFollowingPost(String userID)
    {

        followingPostRef=FirebaseDatabase.getInstance().getReference().child("Post")
                .child(userID);
        followingPostRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

                ArrayList<Post> temp=followingPosts.getValue();
                Post newPost=snapshot.getValue(Post.class);
                if(newPost!=null) {
                    if (temp.stream().anyMatch((post -> {
                        return newPost.getPostID().equals(post.getPostID());
                    }))) {
                        return;
                    }
                        temp.add(newPost);
                    temp.sort(Post::compareTo);
                    followingPosts.postValue(temp);
                }
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                ArrayList<Post> temp=followingPosts.getValue();
                Post changedPost=snapshot.getValue(Post.class);
                if(temp!=null && changedPost!=null)
                {
                    Post postInMyPost=temp.stream()
                            .filter(post -> post.getPostID().equals(changedPost.getPostID()))
                            .findFirst().orElse(new Post());
                    if(postInMyPost.getPostID()!=null)
                    {
                        temp.set(temp.indexOf(postInMyPost),changedPost);
                    }
                }
                temp.sort(Post::compareTo);
                followingPosts.postValue(temp);
            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    public void getFollowingUser()
    {
        followingPosts.setValue(new ArrayList<>());
        if(followingRef!=null)
            followingRef.removeEventListener(mfollowingPostEventListener);
        followingRef= FirebaseDatabase.getInstance().getReference()
                .child("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");
        followingRef.removeEventListener(mfollowingPostEventListener);

        followingRef.addChildEventListener(mfollowingPostEventListener);
    }
    //endregion

}

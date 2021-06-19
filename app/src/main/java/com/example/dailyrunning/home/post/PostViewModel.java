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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.dailyrunning.record.RecordViewModel.activityDateFormat;

public class PostViewModel extends ViewModel {
    private MutableLiveData<Post> selectedPost=new MutableLiveData<>();
    DatabaseReference postRef;
    ValueEventListener postChangeListener;
    {
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
    }
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

}

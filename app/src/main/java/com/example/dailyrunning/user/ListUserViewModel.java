package com.example.dailyrunning.user;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dailyrunning.model.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListUserViewModel  extends ViewModel {
    public MutableLiveData<String> title=new MutableLiveData<>();
    public MutableLiveData<ArrayList<UserInfo>> users=new MutableLiveData<>();
    private ArrayList<String> ids=new ArrayList<>();
    {
        users.setValue(new ArrayList<>());
    }
    public void showUserList(ArrayList<String> ids,String title)
    {
        users.setValue(new ArrayList<>());
        this.ids=ids;
        this.title.setValue(title);
        getFollowInfo();
    }

    public void getFollowInfo()
    {
        DatabaseReference userInfoRef= FirebaseDatabase.getInstance().getReference().child("UserInfo");
        ArrayList<UserInfo> data=new ArrayList<>();
        new Runnable() {
            @Override
            public void run() {
                ids.forEach(uid->{
                    userInfoRef.child(uid).get().addOnSuccessListener(dataSnapshot -> {
                        data.add(dataSnapshot.getValue(UserInfo.class));
                        users.postValue(data);
                    });
                });
            }
        }.run();
    }

}

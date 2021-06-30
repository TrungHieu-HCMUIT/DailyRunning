package com.example.dailyrunningforadmin.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.dailyrunningforadmin.DataLoadListener;
import com.example.dailyrunningforadmin.model.GiftInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Repo {

    static Repo instance;
    static Context mContext;

    private ArrayList<GiftInfo> giftList = new ArrayList<>();
    static DataLoadListener dataLoadListener;

    public static Repo getInstance(Context context) {
        mContext = context;
        if (instance == null) {
            instance = new Repo();
        }
        dataLoadListener = (DataLoadListener) mContext;
        return instance;
    }

    public MutableLiveData<ArrayList<GiftInfo>> getGiftList() {
        loadGiftList();

        MutableLiveData<ArrayList<GiftInfo>> list = new MutableLiveData<>();
        list.setValue(giftList);

        return list;
    }

    private void loadGiftList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child("Gift");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    giftList.add(ds.getValue(GiftInfo.class));
                }
                dataLoadListener.onGiftLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

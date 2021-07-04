package com.example.dailyrunningforadmin.repository;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.dailyrunningforadmin.DataLoadListener;
import com.example.dailyrunningforadmin.model.GiftInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
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
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                giftList.clear();
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

    public void addGift(GiftInfo gift, Bitmap bitmap) {
        String id;

        DatabaseReference giftDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Gift");
        StorageReference giftStorageReference = FirebaseStorage.getInstance().getReference().child("gift_images");

        id = giftDatabaseReference.push().getKey();

        byte[] imageData = bitmapToByteArray(bitmap);
        
        UploadTask uploadTask = giftStorageReference.child(id).putBytes(imageData);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                gift.setID(id);
                gift.setPhotoUri(id);
                giftDatabaseReference.child(id).setValue(gift).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadGiftList();
                    }
                });
            }
        });
    }

    public void deleteGift(GiftInfo gift) {
        DatabaseReference giftRef = FirebaseDatabase.getInstance().getReference().child("Gift");
        giftRef.child(gift.getID()).removeValue();
        getGiftList();
    }

    private byte[] bitmapToByteArray(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        return bytes.toByteArray();
    }
}

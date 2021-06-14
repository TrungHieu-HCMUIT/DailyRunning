package com.example.dailyrunning.record;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dailyrunning.model.Activity;
import com.example.dailyrunning.model.LatLng;
import com.example.dailyrunning.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class FinishFragment extends Fragment {

    private static final String TAG = "Finish Fragment";

    private static final String INTENT_IMAGE = "pictureURL";
    private final String INTENT_DISTANCE_KEY = "distance";
    private final String INTENT_TIME_KEY = "time";
    private String INTENT_DATE_CREATED = "datecreated";
    private String INTENT_LATLNG_LIST = "latlngarrlist";
    EditText describeText;
    ArrayList<LatLng> list = new ArrayList<>();
    DatabaseReference exampleRun;
    Button buttonSave;
    Button buttonBack;
    TextView distanceTextView;
    TextView timeTextView;
    TextView paceTextView;
    Bitmap  bitmap;
    double pace;
    Uri downloadUrl=null;
    private View rootView;
    StorageReference reference;
    String newActivityID;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_finish, container, false);
        //setTitle(R.string.runCompleted);

        Bundle resultFromRecordFragment = getArguments();
        double completedDist = resultFromRecordFragment.getDouble(INTENT_DISTANCE_KEY);
        long completedDuration = resultFromRecordFragment.getLong(INTENT_TIME_KEY);
        list = (ArrayList<LatLng>) resultFromRecordFragment.get(INTENT_LATLNG_LIST);
        String formattedDate = resultFromRecordFragment.getString(INTENT_DATE_CREATED);
        byte[] byteArray = resultFromRecordFragment.getByteArray(INTENT_IMAGE);
        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);


        String  speed = getSpeed(completedDist, completedTime);
        getPace(completedDist, completedTime);

        
        int runningPoint= (int) completedDist/1000;

        ((TextView)rootView.findViewById(R.id.record_running_point_textView)).setText(runningPoint+" điểm Running");



        describeText = rootView.findViewById(R.id.describe_editText);
        buttonSave = rootView.findViewById(R.id.btnSave);
        buttonBack = rootView.findViewById(R.id.btnBack);
        distanceTextView = rootView.findViewById(R.id.km);
        timeTextView = rootView.findViewById(R.id.time);
        paceTextView = rootView.findViewById(R.id.pace);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference activityRef = database.getReference().child("Activity").child(user.getUid());
        newActivityID = activityRef.push().getKey();
        reference=FirebaseStorage.getInstance().getReference().child("imageMap");

        pace = getPace(completedDist, completedDuration);

        distanceTextView.setText(completedDist + " km");
        timeTextView.setText(formatDuration(completedDuration));
        paceTextView.setText(pace + " m/s");

        buttonSave.setOnClickListener(v -> {
            uploadImage(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    downloadUrl=uri;
                    Activity activity = new Activity(newActivityID,
                            user.getUid(),
                            formattedDate,
                            completedDist,
                            completedDuration,
                            downloadUrl.toString(),
                            pace,
                            describeText.getText().toString(),
                            list
                    );
                    activityRef.child(newActivityID).setValue(activity);
                    Intent point=new Intent();
                    point.putExtra("point",runningPoint);
                    getActivity().setResult(android.app.Activity.RESULT_OK,point);
                    getActivity().finish();
                }
            });

        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        return rootView;
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    private void uploadImage(OnSuccessListener<Uri> mSuccessCallback) {
        reference.putFile(getImageUri(getContext(),bitmap))
       .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
           @Override
           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               reference.getDownloadUrl().addOnSuccessListener(mSuccessCallback);
           }
       });
    }

    public String formatDuration(long pDuration) {
        return DateUtils.formatElapsedTime(pDuration);
    }

    public double getPace(double distance, long time) {
        double speed = (double) (distance * 1000 / time);
        return  speed * 3.6;
    }
}
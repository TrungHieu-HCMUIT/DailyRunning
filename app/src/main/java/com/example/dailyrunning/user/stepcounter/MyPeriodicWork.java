package com.example.dailyrunning.user.stepcounter;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.dailyrunning.R;
import com.example.dailyrunning.user.UserFragment;
import com.example.dailyrunning.user.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.SENSOR_SERVICE;

public class MyPeriodicWork extends Worker implements SensorEventListener, StepListener{

    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = " bước";

    private Context applicationContext = getApplicationContext();

    private Calendar c = Calendar.getInstance();;
    private  SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyy");
    private  String formattedDate = df.format(c.getTime());

    private  FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference step = database.getReference().child("Step");

    private int numSteps;
    public MyPeriodicWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

    }

    @NonNull
    @Override
    public Result doWork() {
        step.child(user.getUid()).get().addOnCompleteListener(task -> {
            try{
                if (task.getResult().child(formattedDate).getValue()!=null){
                    long step = (long) task.getResult().child(formattedDate).getValue();
                    numSteps= Integer.parseInt(String.valueOf(step));}
            }
            catch (Exception e)
            {
                Log.v("Cant update step",e.getMessage());
            }

        });
        stepCount();
        return Result.success();
    }

    private void sendNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);

        //If on Oreo then notification required a notification channel.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Default", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setContentTitle(title)
                .setContentText(numSteps+TEXT_NUM_STEPS)
                .setSmallIcon(R.mipmap.ic_launcher);

        notificationManager.notify(1, notification.build());
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void step(long timeNs) {
        numSteps++;
        Log.d("step",numSteps+"");
        if (Singleton.getInstance().getTV()!=null)
            Singleton.getInstance().getTV().setText(numSteps + TEXT_NUM_STEPS);
        Map<String, Object> stepUpdates = new HashMap<>();
        stepUpdates.put(formattedDate, numSteps);
        step.child(user.getUid()).updateChildren(stepUpdates);
    }
    void stepCount()
    {
        sensorManager = (SensorManager) applicationContext.getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
    }
}

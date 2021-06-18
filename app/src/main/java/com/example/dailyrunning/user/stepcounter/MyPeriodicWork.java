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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.SENSOR_SERVICE;

public class MyPeriodicWork extends Worker implements SensorEventListener, StepListener{
    private static final String DEFAULT_START_TIME = "00:00";
    private static final String DEFAULT_END_TIME = "23:59";

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = " bước";

    private Context applicationContext = getApplicationContext();
    private DatabaseHandler db;
    private int numSteps=0;
    public MyPeriodicWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        db = new DatabaseHandler(getApplicationContext());
        db.openDatabase();
        StepModel task1 = db.getTasks("0");
        numSteps=task1.getId();
    }

    @NonNull
    @Override
    public Result doWork() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        String formattedDate = dateFormat.format(date);
        try {
            Date currentDate = dateFormat.parse(formattedDate);
            Date startDate = dateFormat.parse(DEFAULT_START_TIME);
            Date endDate = dateFormat.parse(DEFAULT_END_TIME);
            if (currentDate.after(startDate) && currentDate.before(endDate)) {
                try {
                    StepModel task = new StepModel(0, 0);

                    db.insertTask(task);
                    sensorManager = (SensorManager) applicationContext.getSystemService(SENSOR_SERVICE);
                    accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    simpleStepDetector = new StepDetector();
                    simpleStepDetector.registerListener(this);
                    sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
                    // Sending Data to MainActivity.
                } catch (Throwable throwable) {

                    return Result.failure();
                }
            }
        } catch (ParseException ignored) {

        }
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
        Log.d("Step",numSteps+TEXT_NUM_STEPS);
        if (Singleton.getInstance().getTV()!=null)
            Singleton.getInstance().getTV().setText(numSteps + TEXT_NUM_STEPS);
        db.updateTask(0,numSteps+TEXT_NUM_STEPS);
        Bundle bundle =new Bundle();
        bundle.putInt("Step",numSteps);
    }
}

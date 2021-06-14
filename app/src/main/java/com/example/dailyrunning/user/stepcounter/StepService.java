package com.example.dailyrunning.user.stepcounter;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

import static com.example.dailyrunning.user.stepcounter.ForegroundService.CHANNEL_ID;

public class StepService extends JobService {
    private static final String TAG = "JobService";
    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        createNotificationChannel();
        doBackgroundWork(params);
        return true;
    }
    private void doBackgroundWork(final JobParameters params) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                    Log.d(TAG, "Job finished");
                    Intent serviceIntent = new Intent(getApplication(), ForegroundService.class);
                    ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);
                    if (jobCancelled) {
                        return;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                Log.d(TAG, "Job finished");
                jobFinished(params, false);
            }
        }).start();
    }
    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}

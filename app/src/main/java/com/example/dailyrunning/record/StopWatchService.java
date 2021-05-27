package com.example.dailyrunning.record;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * Service that runs the stopwatch
 */

public class StopWatchService extends Service {

    com.example.dailyrunning.record.StopWatch stopWatch = new com.example.dailyrunning.record.StopWatch();

    public StopWatchService() {

    }

    public class LocalBinder extends Binder {
        com.example.dailyrunning.record.StopWatchService getService() {
            return com.example.dailyrunning.record.StopWatchService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();


    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /*
    When the service is started, start stopwatch.
     */
    public int onStartCommand(Intent intent, int flags, int startId) {
        stopWatch.start();


        return START_STICKY;
    }

    /* return how much time has passed in seconds */
    public long getElapsedTime() {
        return stopWatch.getElapsedTime();
    }


    @Override
    public boolean stopService(Intent name) {
        stopWatch.stop();
        return super.stopService(name);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopWatch.stop();
    }
}

package com.example.dailyrunning.user.stepcounter;

import android.widget.TextView;

public class Singleton {
    private static Singleton instance = new Singleton();

    public synchronized  static Singleton getSingletonModel() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;

    }
    private static TextView textView;

    public static Singleton getInstance() {
        return instance;
    }

    public void setTV(TextView tv){
        textView = tv;
    }

    public TextView getTV(){
        return textView;
    }
}

package com.example.android.yamba;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

abstract public class YambaBaseActivity extends AppCompatActivity {
    static long totalTimeCounter = 0;
    static long avgTimeCounter = 0;
    static int totalOnCreates = 0;

    long timeOnCreated = 0;

    abstract void onCreated(@Nullable Bundle savedInstanceState);

    public void onResumed() {
    }

    @Override
    final protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timeOnCreated = System.currentTimeMillis();
        onCreated(savedInstanceState);
    }

    @Override
    final protected void onResume() {
        super.onResume();
        onResumed();
        if (timeOnCreated > 0) {
            totalTimeCounter += System.currentTimeMillis() - timeOnCreated;
            totalOnCreates++;
            timeOnCreated = 0;
            avgTimeCounter = totalTimeCounter / totalOnCreates;
            Log.d("XXX", "totalTimeCounter=" + totalTimeCounter);
            Log.d("XXX", "totalOnCreates=" + totalOnCreates);
            Log.d("XXX", "avgTimeCounter=" + avgTimeCounter);
        }
    }
}

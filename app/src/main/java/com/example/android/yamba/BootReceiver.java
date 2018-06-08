package com.example.android.yamba;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = BootReceiver.class.getSimpleName();
    private static final long DEFAULT_INTERVAL = 0;
    //Constant for the JobScheduler job id
    private static final int REFRESH_JOB_ID = 44;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        long interval = Long.parseLong(
                prefs.getString(context.getString(R.string.interval_key), Long.toString(DEFAULT_INTERVAL)));

        PendingIntent operation = PendingIntent.getService(context, -1,
                new Intent(context, RefreshService.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        //JobScheduler is only available on API 21+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler =
                    (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

            if (interval == 0) {
                //Cancel the job
                jobScheduler.cancel(REFRESH_JOB_ID);
                Log.d(TAG, "cancelling scheduled job");
            } else {
                //Update the scheduled job
                JobInfo job = new JobInfo.Builder(REFRESH_JOB_ID,
                        new ComponentName(context.getPackageName(),
                                ScheduledJobService.class.getName()))
                        //Let the framework reschedule our job on device reboots
                        .setPersisted(true)
                        //Set the trigger interval
                        .setPeriodic(interval)
                        .build();

                jobScheduler.schedule(job);
                Log.d(TAG, "setting scheduled job for: " + interval);
            }
        } else {
            //Use the legacy alarm features to trigger the event
            AlarmManager alarmManager = (AlarmManager) context
                    .getSystemService(Context.ALARM_SERVICE);

            if (interval == 0) {
                alarmManager.cancel(operation);
                Log.d(TAG, "cancelling repeat operation");
            } else {
                alarmManager.setInexactRepeating(AlarmManager.RTC,
                        System.currentTimeMillis(), interval, operation);
                Log.d(TAG, "setting repeat operation for: " + interval);
            }
        }
    }
}

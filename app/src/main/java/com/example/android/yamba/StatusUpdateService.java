package com.example.android.yamba;


import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.thenewcircle.yamba.client.YambaClient;
import com.thenewcircle.yamba.client.YambaClientInterface;

public class StatusUpdateService extends IntentService {
    private static final String TAG =
            StatusUpdateService.class.getSimpleName();

    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_LOCATION = "location";

    public static final int NOTIFICATION_ID = 43;

    private NotificationManager mNotificationManager;

    public StatusUpdateService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Gather received parameters
        String message = intent.getStringExtra(EXTRA_MESSAGE);
        Location location = intent.getParcelableExtra(EXTRA_LOCATION);

        try {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(this);
            final String username = prefs.getString(getString(R.string.username_key), "");
            final String password = prefs.getString(getString(R.string.password_key), "");

            // Check that username and password are not empty
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Log.w(TAG, "Please update your username and password");
                return;
            }

            //Notify the user via a notification
            postProgressNotification();

            YambaClientInterface cloud = YambaClient.getClient(username, password);
            if (location != null) {
                cloud.postStatus(message,
                        location.getLatitude(), location.getLongitude());
            } else {
                cloud.postStatus(message);
            }

            //Hide progress when completed successfully
            Log.d(TAG, "Successfully posted to the cloud: " + message);
            mNotificationManager.cancel(NOTIFICATION_ID);
        } catch (Exception e) {
            Log.e(TAG, "Failed to post to the cloud", e);

            postErrorNotification(message);
        }
    }

    private void postProgressNotification() {
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Posting Status")
                .setContentText("Status update in progress...")
                .setSmallIcon(android.R.drawable.sym_action_email)
                .setOngoing(true)
                .build();

        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void postErrorNotification(String originalMessage) {
        Intent intent = new Intent(this, StatusActivity.class);
        intent.putExtra(EXTRA_MESSAGE, originalMessage);

        PendingIntent operation = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Post Error")
                .setContentText("Error posting status update. Tap to try again.")
                .setSmallIcon(android.R.drawable.sym_action_email)
                .setContentIntent(operation)
                .setAutoCancel(true)
                .build();

        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }
}

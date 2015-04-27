package com.example.android.yamba;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.thenewcircle.yamba.client.YambaClient;
import com.thenewcircle.yamba.client.YambaClientException;
import com.thenewcircle.yamba.client.YambaClientInterface;
import com.thenewcircle.yamba.client.YambaStatus;

import java.util.List;

public class RefreshService extends IntentService {
    private static final String TAG =
            RefreshService.class.getSimpleName();

    public static final int NOTIFICATION_ID = 42;

    private NotificationManager mNotificationManager;

    public RefreshService() {
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
        try {
            final String username = "student";
            final String password = "password";

            YambaClientInterface cloud = YambaClient.getClient(username, password);
            List<YambaStatus> timeline = cloud.getTimeline(20);

            ContentValues values = new ContentValues();
            int count = 0;
            for (YambaStatus status : timeline) {
                values.clear();
                values.put(StatusContract.Column.ID, status.getId());
                values.put(StatusContract.Column.USER, status.getUser());
                values.put(StatusContract.Column.MESSAGE, status.getMessage());
                values.put(StatusContract.Column.CREATED_AT, status
                        .getCreatedAt().getTime());

                Uri uri = getContentResolver().insert(
                        StatusContract.CONTENT_URI, values);
                if (uri != null) {
                    //Increment count for successful inserts
                    count++;

                    Log.d(TAG,
                            String.format("%s: %s", status.getUser(),
                                    status.getMessage()));
                }
            }

            if (count > 0 && !MainActivity.isInTimeline()) {
                postStatusNotification(count);
            }
        } catch (YambaClientException e) {
            Log.e(TAG, "Failed to fetch the timeline", e);
        }
    }

    private void postStatusNotification(int count) {
        Intent intent = new Intent(this, MainActivity.class);

        PendingIntent operation = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("New tweets!")
                .setContentText("You've got " + count + " new tweets")
                .setSmallIcon(android.R.drawable.sym_action_email)
                .setContentIntent(operation)
                .setAutoCancel(true)
                .build();

        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }
}
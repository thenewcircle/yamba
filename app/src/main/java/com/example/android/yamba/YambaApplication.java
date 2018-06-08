package com.example.android.yamba;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.IntentFilter;
import android.os.Build;

public class YambaApplication extends Application {
    public final static String POST_PROGRESS_CHANNEL_ID = "POST_PROGRESS_CHANNEL_ID";
    public final static String REFRESH_NOTIFICATION_CHANNEL_ID = "REFRESH_NOTIFICATION_CHANNEL_ID";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.android.yamba.action.UPDATED_INTERVAL");
        registerReceiver(new BootReceiver(), filter);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_post_progress);
            String description = getString(R.string.channel_post_progress_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel =
                    new NotificationChannel(POST_PROGRESS_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            name = getString(R.string.channel_refresh);
            description = getString(R.string.channel_refresh_description);
            importance = NotificationManager.IMPORTANCE_DEFAULT;
            channel = new NotificationChannel(REFRESH_NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

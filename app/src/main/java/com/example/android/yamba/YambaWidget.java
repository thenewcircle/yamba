package com.example.android.yamba;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.widget.RemoteViews;


public class YambaWidget extends AppWidgetProvider {
    private static final String TAG = YambaWidget.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Get the latest update
        Cursor cursor = context.getContentResolver().query(
                StatusContract.CONTENT_URI, null, null, null,
                StatusContract.DEFAULT_SORT);
        if (!cursor.moveToFirst())
            return;

        String user = cursor.getString(cursor
                .getColumnIndex(StatusContract.Column.USER));
        String message = cursor.getString(cursor
                .getColumnIndex(StatusContract.Column.MESSAGE));
        long createdAt = cursor.getLong(cursor
                .getColumnIndex(StatusContract.Column.CREATED_AT));

        PendingIntent operation = PendingIntent.getActivity(context, -1,
                new Intent(context, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Loop through all the instances of YambaWidget
        for (int appWidgetId : appWidgetIds) {
            // Update the view
            RemoteViews view = new RemoteViews(context.getPackageName(),
                    R.layout.widget);

            // Update the remote view
            view.setTextViewText(R.id.text_user, user);
            view.setTextViewText(R.id.text_message, message);
            view.setTextViewText(R.id.text_created_at,
                    DateUtils.getRelativeTimeSpanString(createdAt));

            view.setOnClickPendingIntent(R.id.item_content, operation);

            // Update the widget
            appWidgetManager.updateAppWidget(appWidgetId, view);
        }
    }
}

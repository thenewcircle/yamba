package com.example.android.yamba;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class TimelineAdapter extends CursorAdapter {

    public TimelineAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public TimelineAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //Create a new view instance
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.list_item, parent, false);

        //Attach the holder using view's "tag" feature
        itemView.setTag(new ViewHolder(itemView));

        return itemView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Access all child view elements through the holder
        ViewHolder holder = (ViewHolder) view.getTag();

        holder.userView.setText(
                getStringValue(cursor, StatusContract.Column.USER) );
        holder.messageView.setText(
                getStringValue(cursor,StatusContract.Column.MESSAGE) );
        holder.createdAtView.setText( getTimestampDisplay(cursor) );
    }

    private String getStringValue(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    private CharSequence getTimestampDisplay(Cursor cursor) {
        long timestamp = cursor.getLong(cursor.getColumnIndex(StatusContract.Column.CREATED_AT));
        return DateUtils.getRelativeTimeSpanString(timestamp);
    }

    /* ViewHolder to cache references from findViewById() */
    private static class ViewHolder {
        public TextView userView;
        public TextView messageView;
        public TextView createdAtView;

        public ViewHolder(View itemView) {
            this.userView =
                    (TextView) itemView.findViewById(R.id.text_user);
            this.messageView =
                    (TextView) itemView.findViewById(R.id.text_message);
            this.createdAtView =
                    (TextView) itemView.findViewById(R.id.text_created_at);
        }
    }
}

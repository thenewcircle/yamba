package com.example.android.yamba;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class StatusProvider extends ContentProvider {
    private static final String TAG = StatusProvider.class.getSimpleName();
    private DbHelper mDbHelper;

    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    static {
        // content://com.example.android.yamba.StatusProvider/status
        sURIMatcher.addURI(StatusContract.AUTHORITY, StatusContract.TABLE,
                StatusContract.STATUS_DIR);
        // content://com.example.android.yamba.StatusProvider/status/id
        sURIMatcher.addURI(StatusContract.AUTHORITY, StatusContract.TABLE
                + "/#", StatusContract.STATUS_ITEM);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        switch (sURIMatcher.match(uri)) {
            case StatusContract.STATUS_DIR:
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                int inserted = 0;
                try {
                    //Insert all the provided values inside of a transaction
                    db.beginTransaction();

                    for (ContentValues item : values) {
                        //If a duplicate id already exists, ignore the insert
                        long id = db.insertWithOnConflict(StatusContract.TABLE,
                                null, item, SQLiteDatabase.CONFLICT_IGNORE);
                        if (id >= 0) {
                            inserted++;
                        }
                    }

                    //Without this, the transaction defaults to failure
                    db.setTransactionSuccessful();

                    getContext().getContentResolver().notifyChange(uri, null);
                } finally {
                    db.endTransaction();
                }

                return inserted;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }
    }

    // SELECT username, message, created_at FROM status WHERE user='bob' ORDER
    // BY created_at DESC;
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables( StatusContract.TABLE );

        switch (sURIMatcher.match(uri)) {
            case StatusContract.STATUS_DIR:
                break;
            case StatusContract.STATUS_ITEM:
                qb.appendWhere(StatusContract.Column.ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        String orderBy = (TextUtils.isEmpty(sortOrder)) ?
                StatusContract.DEFAULT_SORT : sortOrder;

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = qb.query(db, projection, selection, selectionArgs,
                null, null, orderBy);

        // register for uri changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        Log.d(TAG, "queried records: " + cursor.getCount());
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri ret = null;

        // Assert correct uri
        if (sURIMatcher.match(uri) != StatusContract.STATUS_DIR) {
            throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long rowId = db.insertWithOnConflict(StatusContract.TABLE, null,
                values, SQLiteDatabase.CONFLICT_IGNORE);

        // Was insert successful?
        if (rowId != -1) {
            long id = values.getAsLong(StatusContract.Column.ID);
            ret = ContentUris.withAppendedId(uri, id);
            Log.d(TAG, "inserted uri: " + ret);

            // Notify that data for this uri has changed
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return ret;
    }

    // DELETE FROM status WHERE id=? AND user='?'
    // uri: content://com.example.android.yamba.StatusProvider/status/47
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String where;

        switch (sURIMatcher.match(uri)) {
            case StatusContract.STATUS_DIR:
                // so we count deleted rows
                where = (selection == null) ? "1" : selection;
                break;
            case StatusContract.STATUS_ITEM:
                long id = ContentUris.parseId(uri);
                where = StatusContract.Column.ID
                        + "="
                        + id
                        + (TextUtils.isEmpty(selection) ? "" : " and ( "
                        + selection + " )");
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int ret = db.delete(StatusContract.TABLE, where, selectionArgs);

        if(ret>0) {
            // Notify that data for this uri has changed
            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.d(TAG, "deleted records: " + ret);
        return ret;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //We do not need to support this operation for Yamba
        throw new UnsupportedOperationException("This provider does not support updates");
    }
}

package com.example.android.yamba;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

public class StatusProviderTest extends ProviderTestCase2<StatusProvider> {

    private MockContentResolver mContentResolver;

    public StatusProviderTest() {
        super(StatusProvider.class, StatusContract.AUTHORITY);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mContentResolver = getMockContentResolver();
    }

    public void testInsert() {
        int testRecordCount = 5;
        long now = System.currentTimeMillis();

        for (int i=0; i < testRecordCount; i++) {
            final ContentValues item = new ContentValues();
            item.put(StatusContract.Column.ID, i);
            item.put(StatusContract.Column.CREATED_AT, now);
            item.put(StatusContract.Column.USER, "Fake Student");
            item.put(StatusContract.Column.MESSAGE, "Fake Message #"+i);

            //Pre-fill the mock database
            mContentResolver.insert(StatusContract.CONTENT_URI, item);
        }

        //Validate the insert
        Cursor c = mContentResolver
                .query(StatusContract.CONTENT_URI, null, null, null, null);

        //Ensure the cursor exists, is not empty, and has the right count
        assertNotNull(c);
        assertEquals(true, c.moveToFirst());
        assertEquals(testRecordCount, c.getCount());

        c.close();
    }
}

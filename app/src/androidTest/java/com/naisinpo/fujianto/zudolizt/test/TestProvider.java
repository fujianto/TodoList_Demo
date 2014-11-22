package com.naisinpo.fujianto.zudolizt.test;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.naisinpo.fujianto.zudolizt.data.TodoContract.TodoEntry;

/**
 * Created by fujianto on 19/11/14.
 */
public class TestProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void testInsertReadProvider() {
        // Create a new map of values, where column names are the key.
        ContentValues values = new ContentValues();
        values.put(TodoEntry.COLUMN_CATEGORY, "Reminder");
        values.put(TodoEntry.COLUMN_SUMMARY, " Chapter 161: Princess Visit");
        values.put(TodoEntry.COLUMN_DESCRIPTION, "With Firo advancing at a slow pace, we needed 4 days to reach the village. We turned the monsters in our path into food and our carriage journey advanced with no problems.All the slaves who ate my food for the first time had the same reaction. Was it really that delicious?By the way, Fohl became sulky whereas Atla praised it greatly.");

        Uri todoUri = mContext.getContentResolver().insert(TodoEntry.CONTENT_URI, values);
        long todoRowId = ContentUris.parseId(todoUri);

        // Verify we got a row back.
        assertTrue(todoRowId != -1);
        Log.d(LOG_TAG, "New row id: " + todoRowId);
    }
}

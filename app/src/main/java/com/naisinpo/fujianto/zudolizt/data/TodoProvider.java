package com.naisinpo.fujianto.zudolizt.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.naisinpo.fujianto.zudolizt.data.TodoContract.TodoEntry;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by fujianto on 18/11/14.
 */
public class TodoProvider extends ContentProvider {
    // database
    private TodoDbHelper mOpenHelper;

    //todo 3: Create URI matcher property used for the UriMacher
    private static final int TODOS = 10;
    private static final int TODO_ID = 20;

    //todo 4: create buildUriMatcher method
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    // Build provider URI, used to Determine what URI should be Used for specific content.
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TodoContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, TodoContract.PATH_TODO, TODOS);
        matcher.addURI(authority, TodoContract.PATH_TODO + "/#", TODO_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        //Create the Database
        mOpenHelper = new TodoDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Using SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        // check if the caller has requested a column which does not exists
        checkColumns(projection);
        // Set the table
        queryBuilder.setTables(TodoEntry.TABLE_NAME);

        final int match = sUriMatcher.match(uri);
        switch (match){
            case TODOS:

                break;
            case TODO_ID:
                // adding the ID to the original query
                queryBuilder.appendWhere(TodoEntry._ID + "=" + uri.getLastPathSegment());
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(
                db,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match){
            case TODOS:
                return TodoContract.TodoEntry.CONTENT_TYPE;
            case TODO_ID:
                return TodoContract.TodoEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        long id;

        switch (match){
            case TODOS: {
                id = db.insert(TodoEntry.TABLE_NAME, null, values);

                if (id > 0) {
                    returnUri = TodoEntry.buildTodoUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted;

        switch (match){
            case TODOS:
                rowsDeleted = db.delete(TodoEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case TODO_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(
                            TodoEntry.TABLE_NAME,
                            TodoEntry._ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = db.delete(
                            TodoEntry.TABLE_NAME,
                            TodoEntry._ID + "=" + id+ " and " + selection,
                            selectionArgs);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);

        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated;

        switch (match){
            case TODOS:
                rowsUpdated = db.update(TodoEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TODO_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(
                            TodoEntry.TABLE_NAME,
                            values,
                            TodoEntry._ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = db.update(
                            TodoEntry.TABLE_NAME,
                            values,
                            TodoEntry._ID + "=" + id + " and " + selection,
                            selectionArgs);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        return rowsUpdated;
    }

    //todo 6: Check the Column Availability
    private void checkColumns(String[] projection) {
        String[] available = { TodoEntry.COLUMN_CATEGORY,
                TodoEntry.COLUMN_SUMMARY, TodoEntry.COLUMN_DESCRIPTION,
                TodoEntry._ID };

        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        return super.bulkInsert(uri, values);
    }
}

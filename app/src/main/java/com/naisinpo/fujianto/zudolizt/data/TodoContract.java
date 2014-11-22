package com.naisinpo.fujianto.zudolizt.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by fujianto on 18/11/14.
 */
public class TodoContract {
    //todo 1: create ContentProvider property
    public static final String CONTENT_AUTHORITY = "com.naisinpo.fujianto.zudolizt";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_TODO = "todos";

    public static final class TodoEntry implements BaseColumns{
        //todo 2: content provider URI for specific Table

        //content://com.naisinpo.fujianto.zudolizt/todos
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TODO).build();

        //vnd.android.cursor.dir/todos
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PATH_TODO;

        //vnd.android.cursor.item/todos
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + PATH_TODO;

        // Database table
        public static final String TABLE_NAME = "todo";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_SUMMARY = "summary";
        public static final String COLUMN_DESCRIPTION = "description";

        //todo 3: build table URI
        //content://com.naisinpo.fujianto.zudolizt/todos/10
        public static Uri buildTodoUri(Long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}

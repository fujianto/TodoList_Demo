package com.naisinpo.fujianto.zudolizt;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.naisinpo.fujianto.zudolizt.data.TodoContract;
import com.naisinpo.fujianto.zudolizt.data.TodoContract.TodoEntry;
/**
 * Created by fujianto on 19/11/14.
 */
public class TodoDetailActivity extends Activity{
    private final static String LOG_TAG = TodoDetailActivity.class.getSimpleName();
    private Spinner mCategory;
    private EditText mTitleText;
    private EditText mBodyText;
    private Button mEditButton;

    private Uri todoUri;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.todo_edit);

        mCategory = (Spinner) findViewById(R.id.category);
        mTitleText = (EditText) findViewById(R.id.todo_edit_summary);
        mBodyText = (EditText) findViewById(R.id.todo_edit_description);
        mEditButton = (Button) findViewById(R.id.todo_edit_button);

        //Used to get Passed param from MainActivity
        Bundle extras = getIntent().getExtras();

        //Use URI from saved instance
        if(bundle != null){
            todoUri = bundle.getParcelable(TodoContract.TodoEntry.CONTENT_ITEM_TYPE);
        }

        //Use Uri from Passed param
        if (extras != null) {
            todoUri = extras.getParcelable(TodoEntry.CONTENT_ITEM_TYPE);   //getParcelableExtra to get URI from extra
            getTodoListDetail(todoUri);
        }

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTodoList();
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TodoEntry.CONTENT_ITEM_TYPE, todoUri);
    }

    // Grab todolist from database and display it to UI.
    public void getTodoListDetail(Uri uri){
        String[] projection  = {TodoEntry._ID, TodoEntry.COLUMN_CATEGORY, TodoEntry.COLUMN_SUMMARY, TodoEntry.COLUMN_DESCRIPTION};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if(cursor != null){
            // Get the first result  with cursor
            cursor.moveToFirst();
            String category = cursor.getString(cursor.getColumnIndexOrThrow(TodoEntry.COLUMN_CATEGORY));
            String todoTitle = cursor.getString(cursor.getColumnIndexOrThrow(TodoEntry.COLUMN_SUMMARY));
            String todoBody = cursor.getString(cursor.getColumnIndexOrThrow(TodoEntry.COLUMN_DESCRIPTION));

            // Set Value from database to UI
            for (int i = 0; i < mCategory.getCount(); i++) {
                String selected = (String) mCategory.getItemAtPosition(i);
                if (selected.equalsIgnoreCase(category)) {
                    mCategory.setSelection(i);
                }
            }

            mTitleText.setText(todoTitle);
            mBodyText.setText(todoBody);

            // always close the cursor
            cursor.close();
        }
    }

    // Insert or Update TodoList item
    public void createTodoList(){
        String title =  mTitleText.getText().toString();
        String body = mBodyText.getText().toString();
        String category = (String) mCategory.getSelectedItem();

        if(title.length() > 0 && body.length() > 0) {
            // Create a new map of values, where column names are the key.
            ContentValues values = new ContentValues();
            values.put(TodoContract.TodoEntry.COLUMN_CATEGORY, category);
            values.put(TodoContract.TodoEntry.COLUMN_SUMMARY, title);
            values.put(TodoContract.TodoEntry.COLUMN_DESCRIPTION, body);

            if(todoUri == null) {
                // content://com.naisinpo.fujianto.zudolizt/todos/id
                todoUri = getContentResolver().insert(TodoContract.TodoEntry.CONTENT_URI, values);
            } else{
                getContentResolver().update(todoUri, values, null, null);
            }

            //Close the edit and back to main.
            this.finish();
        } else{
            Toast.makeText(this, "Please fill Summary and Description", Toast.LENGTH_SHORT).show();
        }
    }

}

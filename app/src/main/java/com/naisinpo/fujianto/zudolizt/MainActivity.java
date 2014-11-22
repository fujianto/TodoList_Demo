package com.naisinpo.fujianto.zudolizt;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.naisinpo.fujianto.zudolizt.data.TodoContract.TodoEntry;

public class MainActivity extends Activity implements LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    // private Cursor cursor;
    private SimpleCursorAdapter mTodoAdapter;

    private static final int TODO_LOADER = 0;
    private static final int ACTIVITY_EDIT = 0;
    private static final int ACTIVITY_DELETE = 1;
//    private static final int DELETE_ID = Menu.FIRST + 1;

    private ListView listViewTodo;

    @Override
    protected void onResume() {
        super.onResume();
        // todo: refresh listview with newer data when update / insert done
        getLoaderManager().restartLoader(TODO_LOADER, null, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_list);

        getTodoList();
    }

    private void getTodoList(){
        // Fields from the database (projection)
        String[] from = new String[] {TodoEntry.COLUMN_SUMMARY};

        // Fields on the UI to which we map
        int[] to = new int[] { R.id.label };

        listViewTodo = (ListView) findViewById(R.id.listTodo);

        //Init loader for first time use.
        getLoaderManager().initLoader(TODO_LOADER, null, this);
        mTodoAdapter = new SimpleCursorAdapter(this, R.layout.todo_row, null, from,  to, 0);
        listViewTodo.setEmptyView(findViewById(R.id.txvEmptyList));         //Set view if listview is empty
        listViewTodo.setAdapter(mTodoAdapter);
        listViewTodo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(view.getContext(), TodoDetailActivity.class);
                // content://com.naisinpo.fujianto.zudolizt/todos/2
                Uri todoUri = TodoEntry.buildTodoUri(id);
                i.putExtra(TodoEntry.CONTENT_ITEM_TYPE, todoUri);
                startActivity(i);
            }
        });

        //Initialize context menu for ListView, Long press to show it.
        registerForContextMenu(listViewTodo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == R.id.insert){
            //todo: Create todolist
            Intent intent = new Intent(this, TodoDetailActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        int id = v.getId();
        if (id == R.id.listTodo) {
            menu.setHeaderTitle("Manage Todo list");
            String[] menuItems = getResources().getStringArray(R.array.manage_todo);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        long id = item.getItemId();
        AdapterContextMenuInfo info =
                (AdapterContextMenuInfo) item.getMenuInfo();      //Used to get id from the selected item

        if(id == ACTIVITY_EDIT){
            Intent intent = new Intent(this, TodoDetailActivity.class);
            Uri todoUri = TodoEntry.buildTodoUri(info.id);
            intent.putExtra(TodoEntry.CONTENT_ITEM_TYPE, todoUri);
            startActivity(intent);
        }

        if(id == ACTIVITY_DELETE){
            Uri todoUri = TodoEntry.buildTodoUri(info.id);
            getContentResolver().delete(todoUri, null, null);
            getLoaderManager().restartLoader(TODO_LOADER, null, this);      // Refresh listview to show the latest item from db after delete.
            Toast.makeText(this, " Item Deleted ", Toast.LENGTH_SHORT).show();
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { TodoEntry._ID, TodoEntry.COLUMN_SUMMARY };
        String sortOrder = TodoEntry._ID + " DESC ";

        CursorLoader cursorLoader = new CursorLoader(
                this, TodoEntry.CONTENT_URI, projection, null, null, sortOrder);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mTodoAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        mTodoAdapter.swapCursor(null);
    }
}

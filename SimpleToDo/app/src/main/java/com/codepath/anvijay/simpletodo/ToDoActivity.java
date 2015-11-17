package com.codepath.anvijay.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ToDoActivity extends AppCompatActivity {

    ListView lvItems;
    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;

    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //get a handle to the listView
        lvItems = (ListView) findViewById(R.id.lvItems);
        readItems();
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);

        //setup the ListView Listeners
        setupListViewListener();
    }

    //When the Edit Item Activity ends, this method is invoked to do something with the results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            String editedItem = data.getExtras().getString("EditedItem");
            int position = data.getExtras().getInt("Position", 0);

            //put the edited item in the position in the arrarylist
            items.set(position, editedItem);
            itemsAdapter.notifyDataSetChanged();
            writeItems();

            // Toast the name to display temporarily on screen
            Toast.makeText(this, editedItem, Toast.LENGTH_SHORT).show();
        }
    }

    //setup a listViewListener
    private void setupListViewListener(){
            lvItems.setOnItemLongClickListener(
                    new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(
                                AdapterView<?> adapter,
                                View item,
                                int pos, long id) {
                            Log.d("myTag", "fired");
                            items.remove(pos);
                            itemsAdapter.notifyDataSetChanged();
                            writeItems();
                            return true;
                        }
                    }
            );

            lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(
                        AdapterView<?> adapter,
                        View item,
                        int pos, long id) {
                    //get the string value of the item
                    String itemText = items.get(pos).toString();

                    //setup an intent
                    Intent i = new Intent(ToDoActivity.this, EditItemActivity.class);

                    //pass the string value to the intent
                    i.putExtra("Item", itemText);
                    i.putExtra("Position", pos);

                    //start the edit activity
                    startActivityForResult(i, REQUEST_CODE);
                }
            });
    }

    //method to add a new item
    public void onAddItem(View v){
        EditText etAddItem = (EditText) findViewById(R.id.editText);
        String todoItem = etAddItem.getText().toString();
        items.add(todoItem);
        etAddItem.setText("");
        writeItems();
    }

    //read a file
    private void readItems(){
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try{
            items = new ArrayList<String>(FileUtils.readLines(todoFile));
        } catch (Exception ex){
            items = new ArrayList<String>();
            ex.printStackTrace();
        }
    }

    //write to a file
    private void writeItems(){
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            FileUtils.writeLines(todoFile, items);
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_to_do, menu);
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

        return super.onOptionsItemSelected(item);
    }
}

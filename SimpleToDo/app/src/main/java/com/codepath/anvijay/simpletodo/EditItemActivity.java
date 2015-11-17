package com.codepath.anvijay.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {

    String inputParam_itemToEdit;
    int pos;

    EditText etItemToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
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

        //get the item text to edit and the position as passed in by the intent
        inputParam_itemToEdit = getIntent().getStringExtra("Item");
        pos = getIntent().getIntExtra("Position", 0);

        //bind to the text view and set the text to the string that was passed in
        etItemToEdit = (EditText) findViewById(R.id.etItemToEdit);
        etItemToEdit.setText(inputParam_itemToEdit);
    }

    //method to add a new item
    public void onEditedItemSave(View v) {
        String editedItem = ((EditText) findViewById(R.id.etItemToEdit)).getText().toString();

        // Prepare data intent
        Intent data = new Intent();

        // Pass relevant data back as a result
        data.putExtra("EditedItem", editedItem);
        data.putExtra("Position", pos); // ints work too

        // Activity finished ok, return the data
        setResult(RESULT_OK, data); // set result code and bundle data for response
        finish(); // closes the activity, pass data to parent
    }
}

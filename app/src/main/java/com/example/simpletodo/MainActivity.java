package com.example.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    //Member data
    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;

    List<String> tasks; //The list that will contain all of the tasks to display
    Button buttonAdd;   //Button at bottom-right to add new tasks to the list
    EditText addItemBox;    //Text field to add the new tasks
    RecyclerView itemsView; //View to help display the list of tasks
    ItemsAdapter itemsAdapter;  //Adapter class for the RecyclerView

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonAdd = findViewById(R.id.buttonAdd);
        addItemBox = findViewById(R.id.addItemBox);
        itemsView = findViewById(R.id.itemsView);

        loadItems();    //Loads saved state from file system
        //Preset data to load

        //Anonymous class that implements onLongClickListener interface
        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position)
            {
                tasks.remove(position);
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item removed", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };

        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener()
        {
            @Override
            public void onItemClicked(int position)
            {
                Log.d("MainActivity", "Single click at position " + position);
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                i.putExtra(KEY_ITEM_TEXT, tasks.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);
                startActivityForResult(i, EDIT_TEXT_CODE);
            }
        };
        itemsAdapter = new ItemsAdapter(tasks, onLongClickListener, onClickListener);
        itemsView.setAdapter(itemsAdapter);
        itemsView.setLayoutManager(new LinearLayoutManager(this));

        //Anonymous class that ?implements? View.OnClickListener
        buttonAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Turn text typed in into String and add to the task list
                String todoItem = addItemBox.getText().toString();
                tasks.add(todoItem);

                itemsAdapter.notifyItemInserted(tasks.size() - 1);
                addItemBox.setText("");
                Toast.makeText(getApplicationContext(), "Item added", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);
            tasks.set(position, itemText);
            itemsAdapter.notifyItemChanged(position);
            saveItems();
            Toast.makeText(getApplicationContext(), "Item updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }
    }

    private File getDataFile()
    {
        return new File(getFilesDir(), "data.txt");
    }

    //Retrieve the list of items from local storage
    private void loadItems()
    {
        try
        {
            tasks = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        }
        catch (IOException e)
        {
            Log.e("MainActivity", "Error reading items", e);
            tasks = new ArrayList<>();
        }
    }

    //Write the list of items to local storage
    private void saveItems()
    {
        try
        {
            FileUtils.writeLines(getDataFile(), tasks);
        }
        catch (IOException e)
        {
            Log.e("MainActivity", "Error reading items", e);
        }
    }
}
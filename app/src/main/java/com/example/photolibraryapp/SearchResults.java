package com.example.photolibraryapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SearchResults extends AppCompatActivity {

    ArrayList<Photo> results = new ArrayList<>();
    private ListView listView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_layout);
        listView = findViewById(R.id.album_layout);
        Bundle bundle = getIntent().getExtras();
        results = (ArrayList<Photo>) bundle.getSerializable("results");
        MyListView itemsAdapter = new MyListView(this, results);
        listView.setAdapter(itemsAdapter);


    }

    public void createAlbumFromResults(){
        /**
         * TODO create a dialog to ask the user for the information regarding the album name
         */
    }
}

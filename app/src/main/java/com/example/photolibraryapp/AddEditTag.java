package com.example.photolibraryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class AddEditTag extends AppCompatActivity {

    public ArrayList<Tag> tags = new ArrayList<>();
    private ListView listView;
    private String albumName;
    private Album album;
    private String photoLocation;
    private Photo photo;
    private Spinner spinner;
    private EditText editText;
    private ArrayList<Tag> finalTags;
    ArrayAdapter<Tag> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_tag);
        spinner = findViewById(R.id.tagTypeId);
        listView = findViewById(R.id.tagListView);
        editText = findViewById(R.id.et);
        Bundle bundle = getIntent().getExtras();
        albumName = bundle.getString(AddEditPhoto.ALBUM_NAME);

        album = getAlbum();
        photoLocation = bundle.getString(AddEditPhoto.PHOTO_LOCATION);

        if(album == null){
            AlertDialog.Builder alert= new AlertDialog.Builder(this);
            alert.setTitle("There was an issue loading the photo");
            alert.setMessage("Aborting.");
            alert.show();
            return;
        }
        for(Photo p : album.getPhotos()){
            if(p.getLocation().equals(photoLocation)){
                photo = p;
                break;
            }
        }

        if(photo == null){
            tags = new ArrayList<>();
        }else{
            tags = (ArrayList<Tag>) photo.getTags();
        }

        List<String> u = new ArrayList<>();
        u.add("location");
        u.add("person");
        adapter = new ArrayAdapter<>(this, R.layout.album, tags);
        ArrayAdapter<String> tagTypes = new ArrayAdapter<>(this, R.layout.album, u);
        listView.setAdapter(adapter);
        tagTypes.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(tagTypes);
    }

    public void saveTags(View view){
        finalTags = tags;
        // make Bundle
        Bundle bundle = new Bundle();
        bundle.putString(OpenPhoto.ALBUM_NAME, albumName);
        bundle.putSerializable("tags", finalTags);
        // send back to caller
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        finish();
    }

    public void collect(View view){
        if(tags == null){
            tags = new ArrayList<>();
            tags.add(new Tag(spinner.getSelectedItem().toString(), editText.getText().toString()));
            editText.setText("");
        }else{
            tags.add(new Tag(spinner.getSelectedItem().toString(), editText.getText().toString()));
            editText.setText("");
        }
        adapter.notifyDataSetChanged();
    }

    private Album getAlbum(){
        Album album = null;
        try{
            FileInputStream fis = openFileInput(albumName + ".dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            album = (Album) ois.readObject();
            fis.close();
            ois.close();
        }catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return album;
    }




}

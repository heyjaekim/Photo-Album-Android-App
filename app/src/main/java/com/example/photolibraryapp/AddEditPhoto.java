package com.example.photolibraryapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddEditPhoto extends AppCompatActivity {

    public static final String PHOTO_LOCATION = "photo_location";
    public  static final String ALBUM_NAME = "album_name";
    public static final String PHOTO_CAPTION = "photo_caption";

    public static final int ADD_TAG = 7;
    private ImageView imageView;
    private Uri photoLocation;
    private String photoCaption;
    private String albumName;
    private EditText caption;
    private Album album;
    private List<Tag> tags = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_photo);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = findViewById(R.id.imageView);

        Bundle bundle = getIntent().getExtras();
        photoLocation = bundle.getParcelable(PHOTO_LOCATION);
        albumName = bundle.getString(ALBUM_NAME);

        if(bundle.getString(PHOTO_CAPTION) != null){
            caption.setText(bundle.getString(PHOTO_CAPTION));
        }

        imageView.setImageIcon(Icon.createWithContentUri(photoLocation));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){

        if(resultCode != RESULT_OK) return;

        Bundle bundle = intent.getExtras();
        if(requestCode == ADD_TAG){
            tags = (List<Tag>) bundle.getSerializable("tags");
            return;
        }
    }
    public void savePhoto(View view){

        album = getAlbum();

        //Initializes the photo
        caption = findViewById(R.id.photoCaption);
        photoCaption = caption.getText().toString();
        File f = new File(photoLocation.toString());
        f.
        String filename = photoLocation.toString();

        Cursor cursor = getContentResolver().query(photoLocation, null, null, null, null);

        try {
            int index = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
            if (cursor != null && cursor.moveToFirst()) {
                filename = cursor.getString(index);
            }
        } catch (Exception e){
            e.printStackTrace();
            cursor.close();
        }

        //set the time of the file to the last modified date time.
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(f.lastModified()));

        Photo photo = new Photo(photoLocation.toString(), c, photoCaption, filename);

        //Add any tag that may have been added to the photo
        for(Tag tag : tags){
            photo.addTag(tag);
        }

        //Adds photo to album
        album.addPhoto(photo);

        saveAlbum();

        // make Bundle
        Bundle bundle = new Bundle();
        bundle.putString(ALBUM_NAME, albumName);

        // send back to caller
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        finish();
    }


    public void addTag(View view){
        Intent intent = new Intent(this, AddEditTag.class);

        Bundle bundle = getIntent().getExtras();
        bundle.putString(PHOTO_LOCATION, photoLocation.toString());
        bundle.putString(ALBUM_NAME, albumName);
        intent.putExtras(bundle);
        intent.putExtra("ALBUM", album);
        startActivityForResult(intent, ADD_TAG);
    }

    public Album getAlbum(){
        try{
            FileInputStream fis = openFileInput(albumName + ".dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            album = (Album) ois.readObject();
            fis.close();
            ois.close();
            return album;
        }catch(IOException | ClassNotFoundException e){
            return null;
        }
    }

    public void saveAlbum(){
        try{
            FileOutputStream fos = openFileOutput(albumName + ".dat", Context.MODE_PRIVATE);
            ObjectOutputStream ois = new ObjectOutputStream(fos);
            ois.writeObject(album);
            fos.close();
            ois.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }


    public void cancelAddPhoto(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

}

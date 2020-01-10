package com.example.photolibraryapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class OpenPhoto extends AppCompatActivity {

    public static final String PHOTO_LOCATION = "photo_location";
    public static final String PHOTO_INDEX = "photo_index";
    public static final String ALBUM_NAME = "album_name";
    public static final int ADD_TAG = 7;
    public static final int DEL_TAG = 9;

    private String albumName;
    private String photoLocation = null;
    private Photo photo;
    private Album photoAlbum;
    private List<Tag> tags = new ArrayList<>();

    private ImageView imageView;
    private ListView listView;
    private TextView captionTextView;
    private TextView dateTimeTextView;

    private GestureDetectorCompat gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_photo);

        listView = findViewById(R.id.tagsListView);
        captionTextView = findViewById(R.id.captionTextView);
        dateTimeTextView = findViewById(R.id.dateTimeTextView);


        Bundle bundle = getIntent().getExtras();
        photoLocation = bundle.getString(PHOTO_LOCATION);

        imageView = findViewById(R.id.photoImageView);
        photoLocation = bundle.getString(PHOTO_LOCATION);
        albumName = bundle.getString(ALBUM_NAME);
        photoAlbum = getAlbum();
        photo = photoAlbum.getPhotos().get(bundle.getInt(PHOTO_INDEX));
        imageView.setImageIcon(Icon.createWithContentUri(photoLocation));
        captionTextView.setText("FILENAME: " + photo.getFileName() + "\n" +photo.getCaption());
        dateTimeTextView.setText(photo.getDateTime().getTime().toString());

        ArrayAdapter<Tag> itemsAdapter = new ArrayAdapter<>(this, R.layout.album, photo.getTags());
        listView.setAdapter(itemsAdapter);

        gestureDetector = new GestureDetectorCompat(this, new FlingGestureDetector(this));

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Welcome to photo slide show");
        alert.setMessage("Swipe if you would like to see the previous or next photos in your albums");
        alert.setPositiveButton("OK", (dialog, which) -> {});
        alert.show();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.more_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.copy_photo:
                copyPhoto();
                return true;
            case R.id.move_photo:
                movePhoto();
                return true;
            case R.id.delete:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Are you sure you want to delete this picture?");
                alert.setMessage("This action cannot be undone");
                alert.setPositiveButton("DELETE", ((dialog, which) -> deletePhoto()));
                alert.setNegativeButton("CANCEL", (dialog, which) -> {});
                alert.show();
                return true;
            case R.id.add_photo_tag:
                addPhotoTag();
                return true;
            case R.id.del_photo_tag:
                deletePhotoTag();
                return true;
            default:
                super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    public void deletePhoto(){
        photoAlbum.deletePhoto(photo);
        saveAlbum();
        finish();
    }

    public void addPhotoTag(){

        Intent intent = new Intent(this, AddEditTag.class);

        Bundle bundle = getIntent().getExtras();
        bundle.putString(PHOTO_LOCATION, photoLocation);
        bundle.putString(ALBUM_NAME, albumName);
        intent.putExtras(bundle);
        intent.putExtra("ALBUM", photoAlbum);
        startActivityForResult(intent, ADD_TAG);
    }

    public void deletePhotoTag(){

        Intent intent = new Intent(this, DeleteTag.class);

        Bundle bundle = getIntent().getExtras();
        bundle.putString(PHOTO_LOCATION, photoLocation);
        bundle.putString(ALBUM_NAME, albumName);
        intent.putExtras(bundle);
        intent.putExtra("Album", photoAlbum);
        startActivityForResult(intent, DEL_TAG);
    }

    public void copyPhoto(){
        String[] albums = getAlbums();

        if(albums.length == 0){
            AlertDialog.Builder error = new AlertDialog.Builder(this);
            error.setTitle("There are no other albums to move the photo to");
            error.setMessage("Please create an album and try again");
            error.setPositiveButton("OK", ((dialog, which) -> {}));
            error.show();
            return;
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Choose which album to copy photo to");
        alert.setItems(albums, (dialog, which) -> {
            String chosen = albums[which];

            Album myAlbum = null;
            try{
                FileInputStream fis = openFileInput(chosen + ".dat");
                ObjectInputStream ois = new ObjectInputStream(fis);
                myAlbum = (Album) ois.readObject();
                fis.close();
                ois.close();
            }catch(IOException | ClassNotFoundException e){
                e.printStackTrace();
            }
            myAlbum.addPhoto(photo);
            saveAlbum(myAlbum.getAlbum(), myAlbum);
            saveAlbum();
        });
        alert.show();
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    public void movePhoto(){
        String[] albums = getAlbums();

        if(albums.length == 0){
            AlertDialog.Builder error = new AlertDialog.Builder(this);
            error.setTitle("There are no other albums to move the photo to");
            error.setMessage("Please create an album and try again");
            error.setPositiveButton("OK", ((dialog, which) -> {}));
            error.show();
            return;
        }
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Choose which album to move photo to");
        alert.setItems(albums, (dialog, which) -> {
            String chosen = albums[which];
            Album myAlbum = null;
            try{
                FileInputStream fis = openFileInput(chosen + ".dat");
                ObjectInputStream ois = new ObjectInputStream(fis);
                myAlbum = (Album) ois.readObject();
                ois.close();
                fis.close();
            }catch(IOException | ClassNotFoundException e){
                e.printStackTrace();
            }

            myAlbum.addPhoto(photo);
            photoAlbum.deletePhoto(photo);
            saveAlbum(myAlbum.getAlbum(), myAlbum);
            saveAlbum();
        });
        alert.show();

    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent intent) {

        if(resultCode != RESULT_OK) return;

        Bundle bundle = intent.getExtras();
        if(requestCode == ADD_TAG){
            tags = (List<Tag>) bundle.getSerializable("tags");
            List<Tag> tempTags = photo.getTags();
            for(Tag tag : tags) {
                if (!tempTags.contains(tag))
                    photo.addTag(tag);
            }
            ArrayAdapter<Tag> itemsAdapter = new ArrayAdapter<>(this, R.layout.album, photo.getTags());
            listView.setAdapter(itemsAdapter);
            saveAlbum();
            return;
        }

        if(requestCode == DEL_TAG){
            tags = (List<Tag>) bundle.getSerializable("tags");
            List<Tag> tempTags = new ArrayList<>();
            for(Tag tag : tags){
                tempTags.add(tag);
            }
            photo.setTags(tempTags);
            ArrayAdapter<Tag> itemsAdapter = new ArrayAdapter<>(this, R.layout.album, photo.getTags());
            listView.setAdapter(itemsAdapter);
            saveAlbum();
            return;


        }

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

    private void saveAlbum(){
        try{
            FileOutputStream fos = openFileOutput(albumName + ".dat", Context.MODE_PRIVATE);
            ObjectOutputStream ois = new ObjectOutputStream(fos);
            ois.writeObject(photoAlbum);
            fos.close();
            ois.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void saveAlbum(String name, Album album){
        try{
            FileOutputStream fos = openFileOutput(name + ".dat", Context.MODE_PRIVATE);
            ObjectOutputStream ois = new ObjectOutputStream(fos);
            ois.writeObject(album);
            fos.close();
            ois.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }


    public String[] getAlbums(){
        try{
            FileInputStream fis = openFileInput("albums.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            List<String> album = (List<String>) ois.readObject();
            for(int i = 0; i<album.size(); i++){
                if(album.get(i).equals(albumName)) album.remove(i);
            }
            fis.close();
            ois.close();
            return Arrays.copyOf(album.toArray(), album.toArray().length, String[].class);
        }catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
        }

        return null;
    }

    public void nextPhoto(){
        List<Photo> photos = photoAlbum.getPhotos();

        if(photos.size() <=1) return;
        int index = photos.indexOf(photo) + 1;

        if(index == photos.size()){
            index = 0;
        }

        photo = photos.get(index);
        photoLocation = photo.getLocation();
        imageView.setImageIcon(Icon.createWithContentUri(photoLocation));
        captionTextView.setText("FILENAME: " + photo.getFileName() + "\n" +photo.getCaption());
        dateTimeTextView.setText(photo.getDateTime().getTime().toString());
        ArrayAdapter<Tag> itemsAdapter = new ArrayAdapter<>(this, R.layout.album, photo.getTags());
        listView.setAdapter(itemsAdapter);

    }

    public void lastPhoto(){
        List<Photo> photos = photoAlbum.getPhotos();

        if(photos.size() <= 1 ) return;
        int index = photos.indexOf(photo);
        if(index == 0) {
            index = photos.size();
        }

        photo = photos.get(index-1);
        photoLocation = photo.getLocation();
        imageView.setImageIcon(Icon.createWithContentUri(photoLocation));
        captionTextView.setText("FILENAME: " + photo.getFileName() + "\n" +photo.getCaption());
        dateTimeTextView.setText(photo.getDateTime().getTime().toString());
        ArrayAdapter<Tag> itemsAdapter = new ArrayAdapter<>(this, R.layout.album, photo.getTags());
        listView.setAdapter(itemsAdapter);


    }

}

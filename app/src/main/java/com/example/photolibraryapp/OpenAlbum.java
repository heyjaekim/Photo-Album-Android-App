package com.example.photolibraryapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class OpenAlbum extends AppCompatActivity {
    private ListView listView;
    private List<Photo> photos = new ArrayList<>();
    private Album currAlbum;
    public static final int ADD_PHOTO_CODE = 2;
    public static final int REQUEST_GALLERY_PHOTO = 3;
    public static final int EDIT_ALBUM_CODE = 17;
    public static final String PHOTO_LOCATION = "photo_location";
    public static final String ALBUM_NAME = "album_name";
    public static final String PHOTO_INDEX = "photo_index";
    MyListView adapter;

    private String albumName;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_album);
        Bundle bundle = getIntent().getExtras();

        if(savedInstanceState == null)
            albumName = bundle.getString(MainActivity.ALBUM_NAME);
        else
            albumName = savedInstanceState.getString(MainActivity.ALBUM_NAME);
        listView = findViewById(R.id.opened_album_layout);

        setTitle(albumName);
        try{
            FileInputStream fis = openFileInput(albumName + ".dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            currAlbum = (Album) ois.readObject();
            photos.addAll(currAlbum.getPhotos());
            adapter = new MyListView(this, photos);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view, position, id) -> showPhoto(position));
            fis.close();
            ois.close();
        }catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        albumName = savedInstanceState.getString(MainActivity.ALBUM_NAME);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.in_album_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_menu) {
            fromGallery();
        }else if(item.getItemId() == R.id.search){
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Would you like to search by tag or by date?");
            alert.setMessage("Note: This search will happen across the current album");
            alert.setPositiveButton("SEARCH BY TAG", (dialog, which) -> searchByTag());
            alert.setNegativeButton("SEARCH BY DATE", (dialog, which) -> searchByDate());
            alert.show();
        }else if(item.getItemId() == R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void searchByTag(){

        Bundle bundle = new Bundle();
        bundle.putBoolean("all_albums", false);
        bundle.putString("album", albumName);

        Intent intent = new Intent(this, SearchByTag.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    public void searchByDate(){

        Bundle bundle = new Bundle();
        bundle.putBoolean("all_albums", false);
        bundle.putString("album", albumName);

        Intent intent = new Intent(this, SearchByDate.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void fromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY_PHOTO);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != RESULT_OK) return;

        if(requestCode == REQUEST_GALLERY_PHOTO && resultCode == RESULT_OK){
            Intent intent = new Intent(this, AddEditPhoto.class);
            Uri image = data.getData();

            final int flags = data.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getApplicationContext().getContentResolver().takePersistableUriPermission(image, flags);
            Bundle bundle = new Bundle();
            bundle.putParcelable(PHOTO_LOCATION, image);
            bundle.putString(ALBUM_NAME, albumName);
            intent.putExtras(bundle);
            startActivityForResult(intent, ADD_PHOTO_CODE);
        }else if(requestCode == ADD_PHOTO_CODE && resultCode == RESULT_OK){
            Album album = getAlbum();
            photos.addAll(album.getPhotos());
            ArrayAdapter<Photo> adapter;
            adapter = new ArrayAdapter<>(this, R.layout.album, photos);
            listView.setAdapter(adapter);
        }else if(requestCode == EDIT_ALBUM_CODE && resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            Album album = (Album) bundle.getSerializable("album");
            setTitle(album.getAlbum());
            List<String> albums = new ArrayList<>();

            try{
                FileInputStream fis = openFileInput("albums.dat");
                ObjectInputStream ois = new ObjectInputStream(fis);
                albums = (List<String>) ois.readObject();
                fis.close();
                ois.close();
            }catch(IOException | ClassNotFoundException e){
                e.printStackTrace();
            }

            for(int i = 0; i<albums.size(); i++){
                if(albums.get(i).equals(albumName)){
                    albums.remove(albums.get(i));
                    albums.add(i, album.getAlbum());
                    albumName = album.getAlbum();
                }
            }

            try{
                FileOutputStream fos = openFileOutput("albums.dat", Context.MODE_PRIVATE);
                ObjectOutputStream ois = new ObjectOutputStream(fos);
                ois.writeObject(albums);
                fos.close();
                ois.close();
            }catch(IOException e){
                e.printStackTrace();
            }


            /**
             * TODO change the album name in the list from the albumname
             */
        }
    }

    private void showPhoto(int position){
        Bundle bundle = new Bundle();
        bundle.putString(PHOTO_LOCATION, photos.get(position).getLocation());
        bundle.putString(ALBUM_NAME, albumName);
        bundle.putInt(PHOTO_INDEX, position);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, null, this, OpenPhoto.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, 20);
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

    @Override
    protected void onResume(){
        super.onResume();
        currAlbum = getAlbum();

        if(currAlbum == null) return;

        photos.clear();
        photos.addAll(currAlbum.getPhotos());
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }
}



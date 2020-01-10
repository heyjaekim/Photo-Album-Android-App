package com.example.photolibraryapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<String> albums = new ArrayList<>();
    public static final int ADD_MOVIE_CODE=2;
    public static final String ALBUM_NAME = "album_name";
    public static final int OPEN_ALBUM_CODE = 9;
    public static final int EDIT_MOVIE_CODE = 21;
    ArrayAdapter<String> itemsAdapter;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_layout);

        try{
            FileInputStream fis = openFileInput("albums.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            List<String> album = (List<String>) ois.readObject();
            albums.addAll(album);
            fis.close();
            ois.close();
        }catch(IOException | ClassNotFoundException e){
            albums = new ArrayList<>();
        }


        listView = findViewById(R.id.album_layout);
        itemsAdapter = new ArrayAdapter<>(this, R.layout.album, albums);
        listView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> openAlbum(position));
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Are you sure you want to delete this album and all of its contents?");
            alert.setMessage("This action cannot be undone");
            alert.setPositiveButton("DELETE", ((dialog, which) -> deleteSelectedAlbum(position)));
            alert.setNegativeButton("CANCEL", (dialog, which) -> {});
            alert.show();
            return true;
        });
    }

    public void openAlbum(int position){

        Bundle bundle = new Bundle();
        bundle.putString(ALBUM_NAME, albums.get(position));

        Intent intent = new Intent(this, OpenAlbum.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, OPEN_ALBUM_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.add_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.action_menu) {
            addAlbum();
            return true;
        }else if(item.getItemId() == R.id.search){
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Would you like to search by tag or by date?");
            alert.setMessage("Note: This search will happen across the current album");
            alert.setPositiveButton("SEARCH BY TAG", (dialog, which) -> searchByTag());
            alert.setNegativeButton("SEARCH BY DATE", (dialog, which) -> searchByDate());
            alert.show();
        }else if(item.getItemId() == R.id.deleteMainActivity){
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage("To delete an album, press and hold the album's name");
            alert.setPositiveButton("OK", (dialog, which) -> {});
            alert.show();
        }else if(item.getItemId() == R.id.editMainActivity){

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Please select the album you want to edit.");
            alert.setMessage("You will be redirected to another page when you do.");
            alert.setPositiveButton("OK", (((dialog, which) -> {})));
            alert.show();

            listView.setOnItemClickListener((parent, view, position, id) -> editAlbum(position));

        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteSelectedAlbum(int pos){
        albums.remove(pos);
        itemsAdapter.notifyDataSetChanged();
        listView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> openAlbum(position));
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        updateAlbums();

    }

    public void searchByTag(){
        Bundle bundle = new Bundle();
        bundle.putBoolean("all_albums", true);
        bundle.putStringArrayList("albums", albums);

        Intent intent = new Intent(this, SearchByTag.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void searchByDate(){
        Bundle bundle = new Bundle();
        bundle.putBoolean("all_albums", true);
        bundle.putStringArrayList("albums", albums);

        Intent intent = new Intent(this, SearchByDate.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void editAlbum(int position){
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("album_list", albums);
        bundle.putSerializable("album", getAlbum(albums.get(position)));
        bundle.putBoolean("is_edit", true);
        Intent intent = new Intent(this, AddEditAlbum.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, EDIT_MOVIE_CODE);
    }

    public void addAlbum(){
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("album_list", albums);
        bundle.putBoolean("is_edit", false);
        Intent intent = new Intent(this, AddEditAlbum.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, ADD_MOVIE_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){

        if(resultCode != RESULT_OK){
            if(requestCode == EDIT_MOVIE_CODE){
                listView.setOnItemClickListener((parent, view, position, id) -> openAlbum(position));
            }
            return;
        }

        Bundle bundle = intent.getExtras();

        if(bundle == null) return;

        String albumName = bundle.getString(AddEditAlbum.ALBUM_NAME);

        if(requestCode == ADD_MOVIE_CODE){
            albums.add(albumName);
            updateAlbums();
        }else if(requestCode == OPEN_ALBUM_CODE){
            boolean delete = bundle.getBoolean("delete");
            if(!delete) return;
            listView.invalidateViews();
        }else if(requestCode == EDIT_MOVIE_CODE){
            int pos = bundle.getInt("position");
            String name = bundle.getString("old_name");
            Album album = (Album) bundle.getSerializable("album");
            albums.remove(name);
            albums.add(pos, album.getAlbum());
            updateAlbums();
            listView.setOnItemClickListener((parent, view, position, id) -> openAlbum(position));
        }

    }

    private void updateAlbums(){
        try{
            FileOutputStream fos = openFileOutput("albums.dat", Context.MODE_PRIVATE);
            ObjectOutputStream ois = new ObjectOutputStream(fos);
            ois.writeObject(albums);
            fos.close();
            ois.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private Album getAlbum(String albumName){
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

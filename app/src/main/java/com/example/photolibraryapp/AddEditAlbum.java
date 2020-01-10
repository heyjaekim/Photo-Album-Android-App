package com.example.photolibraryapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class AddEditAlbum extends AppCompatActivity {


    public static final String ALBUM_NAME = "album_name";
    public boolean isEdit = false;
    private Album album = null;
    private ArrayList<String> albums =null;
    private EditText albumName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_album);
        setTitle("Enter Album Name");
        albumName = findViewById(R.id.album_name);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isEdit = bundle.getBoolean("is_edit");
            albumName.setText(bundle.getString(ALBUM_NAME));
            albums = bundle.getStringArrayList("album_list");
            album = (Album) bundle.getSerializable("album");
        }
    }

    public void cancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void save(View view) {
        String name = albumName.getText().toString().trim();

        if(name.isEmpty()){
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Album name is empty or contains only spaces");
            alert.setMessage("Please choose a valid album name");
            alert.setPositiveButton("OK", (dialog, which) -> {});
            alert.show();
            return;
        }

        for(int i = 0 ; i< name.length(); i++){
            if(name.charAt(i) == ' ') continue;
            if(!Character.isLetterOrDigit((name.charAt(i)))){
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Attempting to include non alphanumeric character");
                alert.setMessage("Please choose a valid name");
                alert.setPositiveButton("OK", (dialog, which) -> {});
                alert.show();
                return;
            }
        }

        if (name == null || name.length() == 0) {
            Bundle bundle = new Bundle();
            bundle.putString(AlbumDialogFragment.MESSAGE_KEY,
                    "Name is required");
            DialogFragment newFragment = new AlbumDialogFragment();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(), "badfields");
            return;
        }

        for(String str : albums){
            if(name.equalsIgnoreCase(str)){
                Bundle bundle = new Bundle();
                bundle.putString(AlbumDialogFragment.MESSAGE_KEY,
                        "Album name already present");
                DialogFragment newFragment = new AlbumDialogFragment();
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(), "badfields");
                return;
            }
        }

        if(isEdit){

            int pos = albums.indexOf(album.getAlbum());
            String oldName = album.getAlbum();
            album.setName(name);

            try{
                FileOutputStream fos = openFileOutput(name + ".dat", Context.MODE_PRIVATE);
                ObjectOutputStream ois = new ObjectOutputStream(fos);
                ois.writeObject(album);
                fos.close();
                ois.close();
            }catch(IOException e){
                e.printStackTrace();
            }

            Bundle bundle = new Bundle();
            bundle.putSerializable("album", album);
            bundle.putInt("position", pos);
            bundle.putString("old_name", oldName);

            Intent intent = new Intent();
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
        }else{
            try{
                FileOutputStream fos = openFileOutput(name + ".dat", Context.MODE_PRIVATE);
                ObjectOutputStream ois = new ObjectOutputStream(fos);
                ois.writeObject(new Album(name));
                fos.close();
                ois.close();
            }catch(IOException e){
                e.printStackTrace();
            }

            Bundle bundle = new Bundle();
            bundle.putString(ALBUM_NAME, name);

            Intent intent = new Intent();
            intent.putExtras(bundle);
            setResult(RESULT_OK,intent);
            finish();
        }
    }
}

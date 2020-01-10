package com.example.photolibraryapp;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class DeleteTag extends AppCompatActivity {
    public ArrayList<Tag> tags = new ArrayList<>();
    private ListView listView;
    private String albumName;
    private Album album;
    private String photoLocation;
    private Photo photo;
    private ArrayList<Tag> finalTags;
    private Spinner spinner;
    private EditText editText;

    public ArrayAdapter<Tag> adapter;
    public ArrayAdapter<String> tagTypes;
    public InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Entering into the Edit Tag Mode");
        alert.setMessage("Make sure to press 'Save' after you make changes in the tags.");
        alert.setPositiveButton("OK", (dialog, which) -> {});
        alert.show();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_delete_tag);

        spinner = findViewById(R.id.tagTypeId2);
        listView = findViewById(R.id.listView);
        Bundle bundle = getIntent().getExtras();
        albumName = bundle.getString(OpenPhoto.ALBUM_NAME);
        album = getAlbum();
        photoLocation = bundle.getString(OpenPhoto.PHOTO_LOCATION);

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

        tagTypes = new ArrayAdapter<>(this, R.layout.album, u);
        tagTypes.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(tagTypes);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice, tags);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        listView.setAdapter(adapter);

        findViewById(R.id.btn1).setOnClickListener(clickListener);
        findViewById(R.id.btn2).setOnClickListener(clickListener);
        findViewById(R.id.btn4).setOnClickListener(clickListener);
    }

    private Button.OnClickListener clickListener = new Button.OnClickListener() {
        //private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            editText = findViewById(R.id.et);
            if (v.getId() == R.id.btn1) {
                // 추가 버튼 to add tag button

                if(editText.getText().toString() == null || editText.getText().toString().isEmpty()){
                    Bundle bundle = new Bundle();
                    bundle.putString(AlbumDialogFragment.MESSAGE_KEY,
                            "You are trying to add an invalid field. Please enter a value.");
                    DialogFragment newFragment = new AlbumDialogFragment();
                    newFragment.setArguments(bundle);
                    newFragment.show(getSupportFragmentManager(), "badfields");
                }
                
                if (editText.getText().length() != 0) {
                    tags.add(new Tag(spinner.getSelectedItem().toString(), editText.getText().toString()));
                    editText.setText("");
                    // 갱신되었음을 어댑터에 통보한다.
                    adapter.notifyDataSetChanged();
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }save();
            } else if (v.getId() == R.id.btn2) {
                // 삭제 버튼 to delete tag button
                int pos = listView.getCheckedItemPosition();
                if (pos != ListView.INVALID_POSITION) {
                    tags.remove(pos);
                    listView.clearChoices();
                    adapter.notifyDataSetChanged();
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putString(AlbumDialogFragment.MESSAGE_KEY,
                            "Invalid item position. " +
                                    "Please choose an item in the list");
                    DialogFragment newFragment = new AlbumDialogFragment();
                    newFragment.setArguments(bundle);
                    newFragment.show(getSupportFragmentManager(), "badfields");
                }save();
            } else if (v.getId() == R.id.btn4){
                // 에딧 버튼 Edit btn
                int pos = listView.getCheckedItemPosition();

                if(pos == ListView.INVALID_POSITION){
                    Bundle bundle = new Bundle();
                    bundle.putString(AlbumDialogFragment.MESSAGE_KEY,
                            "Invalid item position. " +
                                    "Please choose an item in the list");
                    DialogFragment newFragment = new AlbumDialogFragment();
                    newFragment.setArguments(bundle);
                    newFragment.show(getSupportFragmentManager(), "badfields");
                    return;
                }

                if(!editText.getText().toString().isEmpty() && spinner.getSelectedItem().equals("location")){
                    tags.get(pos).setTagName(spinner.getSelectedItem().toString());
                    tags.get(pos).setTagValue(editText.getText().toString());
                    editText.setText("");
                    adapter.notifyDataSetChanged();
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }else if(!editText.getText().toString().isEmpty() && spinner.getSelectedItem().equals("person")){
                    tags.get(pos).setTagName(spinner.getSelectedItem().toString());
                    tags.get(pos).setTagValue(editText.getText().toString());
                    editText.setText("");
                    adapter.notifyDataSetChanged();
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
            }save();
        }
    };

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

    public void save(){
        finalTags = tags;
        // make Bundle
        Bundle bundle = new Bundle();
        bundle.putString(OpenPhoto.ALBUM_NAME, albumName);
        bundle.putSerializable("tags", finalTags);

        saveAlbum();
        // send back to caller
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        //finish();
    }

    public void saveTag(View view){
        finalTags = tags;
        // make Bundle
        Bundle bundle = new Bundle();
        bundle.putString(OpenPhoto.ALBUM_NAME, albumName);
        bundle.putSerializable("tags", finalTags);

        saveAlbum();
        // send back to caller
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        finish();
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

    @Override
    public void onBackPressed(){

        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }
}
package com.example.photolibraryapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class SearchByTag extends AppCompatActivity {

    List<String> albums;
    private Spinner spinner;
    private EditText editText;
    private Spinner orAndSpinner;
    private EditText seconTagValue;
    private Spinner secondSpinner;
    private boolean conjuction = false;
    private String choice;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_by_tag);

        Bundle bundle = getIntent().getExtras();

        spinner = findViewById(R.id.tagTypeSpinner);
        editText = findViewById(R.id.tagValueEditText);
        orAndSpinner = findViewById(R.id.orAndSpinner);
        seconTagValue = findViewById(R.id.secondTagValueEditText);
        secondSpinner = findViewById(R.id.secondSpinner);


        if(bundle.getBoolean("all_albums")){
            albums = bundle.getStringArrayList("albums");
        }else {
            albums = new ArrayList<>();
            albums.add(bundle.getString("album"));
        }

        List<String> u = new ArrayList<>();
        u.add("location");
        u.add("person");
        choice = u.get(0);
        List<String> opts = new ArrayList<>();
        opts.add("END");
        opts.add("AND");
        opts.add("OR");
        ArrayAdapter<String> options = new ArrayAdapter<>(this, R.layout.album, opts);
        ArrayAdapter<String> tagTypes = new ArrayAdapter<>(this, R.layout.album, u);
        orAndSpinner.setAdapter(options);

        orAndSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(orAndSpinner.getSelectedItem().equals("OR") ||
                        orAndSpinner.getSelectedItem().equals("AND")){
                    secondSpinner.setVisibility(View.VISIBLE);
                    seconTagValue.setVisibility(View.VISIBLE);
                    secondSpinner.setAdapter(tagTypes);
                    secondSpinner.setSelection((choice.equals("location") ? 0 : 1));
                    conjuction = true;
                }else if(orAndSpinner.getSelectedItem().equals("END")){
                    secondSpinner.setVisibility(View.INVISIBLE);
                    seconTagValue.setVisibility(View.INVISIBLE);
                    secondSpinner.setAdapter(tagTypes);
                    choice = "location";
                    conjuction = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                conjuction = false;
            }
        });

        tagTypes.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        secondSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                choice = secondSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setAdapter(tagTypes);
    }

    public void search(View view){
        ArrayList<Photo> res = new ArrayList<>();
        if(conjuction){
            boolean first =false;
            if(orAndSpinner.getSelectedItem().equals("AND")){
                for(String album : albums){
                    Album currAlbum = getAlbum(album);
                    for(Photo p : currAlbum.getPhotos()){
                        for(Tag tag : p.getTags()){

                            if(!first && contains(tag, spinner.getSelectedItem().toString(), editText.getText().toString())){
                                first = true;
                                continue;
                            }

                            if(first && contains(tag, secondSpinner.getSelectedItem().toString(), seconTagValue.getText().toString())){
                                if(!res.contains(p)) {
                                    res.add(p);
                                    first = false;
                                }
                            }
                        }
                    }
                }

            }else {
                for (String album : albums) {
                    Album currAlbum = getAlbum(album);
                    for (Photo p : currAlbum.getPhotos()) {
                        for(Tag tag : p.getTags()){
                            if(contains(tag, spinner.getSelectedItem().toString(), editText.getText().toString()) ||
                                    contains(tag, secondSpinner.getSelectedItem().toString(), seconTagValue.getText().toString())){
                                if(!res.contains(p)) res.add(p);
                            }
                        }
                    }
                }
            }
        }else{
            for(String album : albums){
                Album currAlbum = getAlbum(album);
                for(Photo p : currAlbum.getPhotos()){
                    for(Tag t : p.getTags()){
                        if(t.getTagName().equals(spinner.getSelectedItem().toString())){
                            for(String s : t.getTagValue()){
                                if(s.contains(editText.getText().toString()) &&
                                        s.substring(0, editText.getText()
                                                .toString()
                                                .length())
                                        .equals(editText.getText().toString())){
                                    if(!res.contains(p)) res.add(p);
                                }
                            }
                        }
                    }
                }
            }
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable("results", res);

        Intent intent = new Intent(this, SearchResults.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public boolean contains(Tag tag, String tagType, String tagValue){
        if(tag.getTagName().equals(tagType)){
            String[] values = new String[1];
            tag.getTagValue().toArray(values);
            return values[0].contains(tagValue) &&
                    values[0].substring(0, tagValue.length())
                            .equals(tagValue);

        }

        return false;
    }
    public void cancelSearchTag(View view){
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
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

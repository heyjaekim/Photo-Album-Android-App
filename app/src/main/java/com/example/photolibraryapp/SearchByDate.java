package com.example.photolibraryapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SearchByDate extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    Calendar calendar = Calendar.getInstance();
    Calendar from = Calendar.getInstance();
    Calendar to = Calendar.getInstance();
    EditText toDateEditText;
    EditText fromDateEditText;
    boolean toDate;
    List<String> albums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_by_date);

        toDateEditText = findViewById(R.id.toDateEditText);
        fromDateEditText = findViewById(R.id.fromDateEditText);

        Bundle bundle = getIntent().getExtras();

        if(bundle.getBoolean("all_albums")){
            albums = bundle.getStringArrayList("albums");
        }else {
            albums = new ArrayList<>();
            albums.add(bundle.getString("album"));
        }

    }

    public void popUpDialog(View view){
        DialogFragment fragment = new MyDatePicker();
        fragment.show(getSupportFragmentManager(), "myDatePicker");

        if(view.getId() == R.id.toDateButton){
            toDate = true;
        }else if(view.getId() == R.id.fromDateButton){
            toDate = false;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);

        if(toDate){
            to.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            to.set(Calendar.YEAR, year);
            to.set(Calendar.MONTH, month);
            setToEditText();
        }
        else {
            from.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            from.set(Calendar.YEAR, year);
            from.set(Calendar.MONTH, month);
            setFromEditText();
        }
    }

    public void setToEditText(){
        toDateEditText.setText(new StringBuilder().append(calendar.get(Calendar.MONTH) +1).append("/").append(calendar.get(Calendar.DAY_OF_MONTH)).append("/").append(calendar.get(Calendar.YEAR)).toString());
    }

    public void setFromEditText(){
        fromDateEditText.setText(new StringBuilder().append(calendar.get(Calendar.MONTH)+1).append("/").append(calendar.get(Calendar.DAY_OF_MONTH)).append("/").append(calendar.get(Calendar.YEAR)).toString());
    }

    public void checkAndSearch(View view){
        ArrayList<Photo> res = new ArrayList<>();
        for(String album : albums){
            Album currAlbum = getAlbum(album);

            for(Photo p : currAlbum.getPhotos()){
                if(from.before(p.getDateTime()) && to.after(p.getDateTime())){
                    res.add(p);
                }
            }
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable("results", res);

        Intent intent = new Intent(this, SearchResults.class);
        intent.putExtras(bundle);
        startActivity(intent);

        //TODO open a new activity to display all of the search results
    }

    public void cancelSearch(View view){
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

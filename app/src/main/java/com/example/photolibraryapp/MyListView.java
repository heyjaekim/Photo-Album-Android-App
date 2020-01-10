package com.example.photolibraryapp;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class MyListView extends ArrayAdapter<Photo> {

   private Context context;
   private List<Photo> photos;

    public MyListView(@NonNull Context context, @NonNull List<Photo> photoList) {
        super(context, R.layout.open_album, photoList);

        this.context = context;

        this.photos = photoList;
    }

    public View getView(int position, View view, ViewGroup parent){

        LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflate.inflate(R.layout.album_list, null, true);

        TextView txt = row.findViewById(R.id.photoinfo);
        ImageView img = row.findViewById(R.id.thumbnail);

        txt.setText(this.photos.get(position).toString());
        img.setImageIcon(Icon.createWithContentUri(photos.get(position).getLocation()));
        return row;

    }
}
